package com.example.task.config;

import com.example.task.config.property.DatabaseProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class PrimaryDataSourceConfig {

    private final DatabaseProperties databaseProperties;

    @Primary
    @Bean(DatabaseBeanNames.PRIMARY_DATA_SOURCE)
    public DataSource loadDataSource() {
        return databaseProperties.getPrimary();
    }

    @Primary
    @Bean(DatabaseBeanNames.PRIMARY_JDBC_TEMPLATE)
    public JdbcTemplate jdbcTemplate(@Qualifier(DatabaseBeanNames.PRIMARY_DATA_SOURCE) DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Primary
    @Bean(DatabaseBeanNames.PRIMARY_TRANSACTION_MANAGER)
    public DataSourceTransactionManager transactionManager(@Qualifier(DatabaseBeanNames.PRIMARY_DATA_SOURCE) DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
