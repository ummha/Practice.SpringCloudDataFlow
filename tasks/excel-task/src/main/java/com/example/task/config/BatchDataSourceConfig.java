package com.example.task.config;

import com.example.task.config.property.DatabaseProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
//@EnableBatchProcessing(
//        dataSourceRef = DatabaseBeanNames.BATCH_DATA_SOURCE,
//        transactionManagerRef = DatabaseBeanNames.BATCH_TRANSACTION_MANAGER
//)
public class BatchDataSourceConfig extends DefaultBatchConfiguration {

    private final DatabaseProperties databaseProperties;

    @Bean(DatabaseBeanNames.BATCH_DATA_SOURCE)
    public DataSource loadDataSource() {
        return databaseProperties.getBatch();
    }

    @Bean(DatabaseBeanNames.BATCH_JDBC_TEMPLATE)
    public JdbcTemplate jdbcTemplate(@Qualifier(DatabaseBeanNames.BATCH_DATA_SOURCE) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(DatabaseBeanNames.BATCH_TRANSACTION_MANAGER)
    public DataSourceTransactionManager transactionManager(@Qualifier(DatabaseBeanNames.BATCH_DATA_SOURCE) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}

