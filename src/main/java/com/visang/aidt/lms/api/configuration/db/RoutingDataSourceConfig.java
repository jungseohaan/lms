package com.visang.aidt.lms.api.configuration.db;

import com.visang.aidt.lms.api.common.interceptor.SlowQueryInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = { "com.visang.aidt.lms.api", "com.visang.aidt.lms.api.contents.repository" },
        annotationClass = Mapper.class,
        sqlSessionFactoryRef = "mybatisSessionFactory")
@Slf4j
public class RoutingDataSourceConfig {

    private final SlowQueryInterceptor slowQueryInterceptor;

    public RoutingDataSourceConfig(@Lazy SlowQueryInterceptor slowQueryInterceptor) {
        this.slowQueryInterceptor = slowQueryInterceptor;
    }

    public static final String MASTER_SERVER = "master";
    public static final String SLAVE_SERVER = "slave";

    @ConfigurationProperties(prefix = "spring.datasource.hikari.master")
    @Bean
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @ConfigurationProperties(prefix = "spring.datasource.hikari.slave")
    @Bean
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @DependsOn({"masterDataSource", "slaveDataSource"})
    @Bean("routingDataSource")
    public DataSource routingDataSource(
            @Qualifier("masterDataSource") DataSource master,
            @Qualifier("slaveDataSource") DataSource slave) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();

        dataSourceMap.put(MASTER_SERVER, master);
        dataSourceMap.put(SLAVE_SERVER, slave);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(master);

        return routingDataSource;
    }

    @Bean("routingLazyDataSource")
    public DataSource dataSource(@Qualifier("routingDataSource") DataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }

    @Bean(name="mybatisSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("routingLazyDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/**/*.xml"));

        // MyBatis Interceptor 등록 (쿼리 성능 모니터링)
        if (slowQueryInterceptor != null) {
            sqlSessionFactoryBean.setPlugins(slowQueryInterceptor);
            log.info("SlowQueryInterceptor registered successfully");
        } else {
            log.warn("SlowQueryInterceptor is null, skipping registration");
        }

//        Properties mybatisProp = new Properties();
//        mybatisProp.setProperty("mapUnderscoreToCamelCase", "true");
//        sqlSessionFactoryBean.setConfigurationProperties(mybatisProp);

        // ResultType vo package location
        // mybatis-config.xml : typeAliases > package에 설정해도 됨.
        // Spring Boot Build 시 MyBatis Type Alias 미적용 문제 해결
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);  // Spring Boot 전용 VFS 사용하도록 지정
        //sqlSessionFactoryBean.setTypeAliasesPackage("com.visaing.aidt.lms.api.repository.entity");
        //sqlSessionFactoryBean.setTypeAliasesPackage("com.atom.karas.vo");

        return sqlSessionFactoryBean.getObject();
    }

    @Bean(name="mybatisSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Autowired @Qualifier("mybatisSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Primary
    @Bean(name="mybatisTrManager")
    public PlatformTransactionManager mybatisTrManager(@Qualifier(value = "routingLazyDataSource") DataSource lazyRoutingDataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(lazyRoutingDataSource);
        return transactionManager;
    }

    /**************************** JPA 설정 ****************************/
    @Bean(name="jpaProperties")
    @ConfigurationProperties(prefix = "spring.jpa")
    public JpaProperties jpaProperties() {
        return new JpaProperties();
    }

    @Bean("jpaRoutingLazyDataSource")
    public DataSource jpaRoutingLazyDataSource(@Qualifier("routingDataSource") DataSource dataSource) {
        return new LazyConnectionDataSourceProxy(dataSource);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory(
            @Qualifier("jpaRoutingLazyDataSource") DataSource dataSource
            , @Qualifier("jpaProperties" ) JpaProperties jpaProperties) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(jpaProperties.isShowSql());
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPackagesToScan("com.visang.aidt.lms.api");
        factory.setDataSource(dataSource);
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(jpaProperties().getProperties());
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean(name="transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}