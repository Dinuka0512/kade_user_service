package com.dinuka.dev.user_service.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() throws SQLException {

        // Create database if it doesn't exist
        createDatabaseIfNotExists();

        // Connect to the actual database
        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);

        return dataSource;
    }

    private void createDatabaseIfNotExists() throws SQLException {

        // Extract database name from JDBC URL
        String databaseName = url.substring(
                url.lastIndexOf('/') + 1
        );

        // Remove query parameters
        if (databaseName.contains("?")) {
            databaseName = databaseName.substring(
                    0,
                    databaseName.indexOf('?')
            );
        }

        // Remove database name from URL
        String serverUrl = url.substring(
                0,
                url.lastIndexOf('/') + 1
        );

        // Connect to MySQL server without selecting a database
        String connectionUrl =
                serverUrl +
                        "?allowPublicKeyRetrieval=true&useSSL=false";

        try (
                Connection connection = DriverManager.getConnection(
                        connectionUrl,
                        username,
                        password
                );
                Statement statement = connection.createStatement()
        ) {

            statement.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS `" +
                            databaseName +
                            "`"
            );

            System.out.println(
                    "Database '" + databaseName + "' is ready."
            );
        }
    }
}