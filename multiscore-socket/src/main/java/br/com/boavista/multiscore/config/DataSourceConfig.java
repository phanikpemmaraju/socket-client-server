package br.com.boavista.multiscore.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

//@Profile("gcp")
@Configuration
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.tomcat")
    public DataSource dataSource(final DataSourceProperties properties) {
        return createPooledDataSource(properties);
    }

    private DataSource createPooledDataSource(final DataSourceProperties properties) {
        final org.apache.tomcat.jdbc.pool.DataSource dataSource = properties.initializeDataSourceBuilder()
                .type(org.apache.tomcat.jdbc.pool.DataSource.class).build();
        final DatabaseDriver databaseDriver = DatabaseDriver.fromJdbcUrl(properties.determineUrl());
        String validationQuery = databaseDriver.getValidationQuery();
        if (validationQuery != null) {
            dataSource.setTestOnBorrow(true);
            dataSource.setValidationQuery(validationQuery);
        }
        return dataSource;
    }
}
