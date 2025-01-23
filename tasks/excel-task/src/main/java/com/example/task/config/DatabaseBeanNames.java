package com.example.task.config;

public interface DatabaseBeanNames {
    String PRIMARY_DATA_SOURCE = "primaryDataSource";
    String PRIMARY_JDBC_TEMPLATE = "primaryJdbcTemplate";
    String PRIMARY_TRANSACTION_MANAGER = "primaryTransactionManager";
    String BATCH_DATA_SOURCE = "batchDataSource";
    String BATCH_JDBC_TEMPLATE = "batchJdbcTemplate";
    String BATCH_TRANSACTION_MANAGER = "batchTransactionManager";
}
