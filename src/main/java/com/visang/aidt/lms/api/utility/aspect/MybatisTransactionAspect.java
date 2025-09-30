package com.visang.aidt.lms.api.utility.aspect;

import lombok.RequiredArgsConstructor;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.RollbackRuleAttribute;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * packageName : com.visang.aidt.lms.api.utility.aspect
 * fileName : MybatisTransactionAspect
 * USER : ysw
 * date : 2024-02-07
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-02-07         ysw          최초 생성
 */
@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
public class MybatisTransactionAspect {

    @Qualifier("mybatisTrManager")
    private final PlatformTransactionManager transactionManager;

    @Bean
    public TransactionInterceptor transactionAdvice() {

        TransactionInterceptor txAdvice = new TransactionInterceptor();

        List<RollbackRuleAttribute> rollbackRules = new ArrayList<>();
        rollbackRules.add(new RollbackRuleAttribute(Exception.class));

        DefaultTransactionAttribute attribute = new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED, rollbackRules);
        String transactionAttributesDefinition = attribute.toString();

        Properties txAttributes = new Properties();
//        txAttributes.setProperty("insert*", transactionAttributesDefinition);
//        txAttributes.setProperty("add*", transactionAttributesDefinition);
        txAttributes.setProperty("create*", transactionAttributesDefinition);
        txAttributes.setProperty("modify*", transactionAttributesDefinition);
        txAttributes.setProperty("copy*", transactionAttributesDefinition);
        txAttributes.setProperty("use*", transactionAttributesDefinition);
        txAttributes.setProperty("update*", transactionAttributesDefinition);
//        txAttributes.setProperty("delete*", transactionAttributesDefinition);
        txAttributes.setProperty("remove*", transactionAttributesDefinition);
        txAttributes.setProperty("save*", transactionAttributesDefinition);

        txAdvice.setTransactionAttributes(txAttributes);
        txAdvice.setTransactionManager(transactionManager);
        return txAdvice;

    }

    @Bean
    public Advisor transactionAdvisor(){
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* com.visang.aidt.lms.api..service..*(..))");
        return new DefaultPointcutAdvisor(pointcut, transactionAdvice());
    }
}