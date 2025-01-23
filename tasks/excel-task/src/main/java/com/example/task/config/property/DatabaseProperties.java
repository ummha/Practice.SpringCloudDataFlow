package com.example.task.config.property;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "database")
public class DatabaseProperties {

    /**
     * JPA 데이터소스 설정
     */
    private PrimaryDataSource primary;
    /**
     * Mybatis 데이터소스 설정
     */
    private BatchDataSource batch;

    @Getter
    @Setter
    public static class PrimaryDataSource extends HikariDataSource {
    }

    @Getter
    @Setter
    public static class BatchDataSource extends HikariDataSource {
    }
}
