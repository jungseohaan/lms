package com.visang.aidt.lms.api.configuration.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> dataSourceKey = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        String lookupKey = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? "slave" : "master";
        /*if (lookupKey == null) {
            lookupKey = RoutingDataSourceConfig.MASTER_SERVER;
        }*/
        //log.info("Current DataSource is {}", lookupKey);
        /*
        String lookupKey = dataSourceKey.get();
        if (lookupKey == null) {
            lookupKey = RoutingDataSourceConfig.MASTER_SERVER;
        }
        */
        log.info("Current DataSource is {}", lookupKey);
        return lookupKey;
    }

    public static void setDataSourceKey(String key) {
        dataSourceKey.set(key);
    }

    public static String getDatabaseName() {
        return dataSourceKey.get();
    }

    public static void clearDataSourceKey() {
        dataSourceKey.remove();
    }

}
