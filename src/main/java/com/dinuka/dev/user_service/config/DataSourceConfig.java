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

        // Create database if it does not exist
        createDatabaseIfNotExists();

        // Configure HikariCP DataSource
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);

        return ds;
    }

    private void createDatabaseIfNotExists() throws SQLException {

        // Extract database name from JDBC URL
        String dbName = url.substring(url.lastIndexOf('/') + 1);

        if (dbName.contains("?")) {
            dbName = dbName.substring(0, dbName.indexOf('?'));
        }

        // Create connection URL without database name
        String serverUrl =
                url.substring(0, url.lastIndexOf('/') + 1)
                        + "?allowPublicKeyRetrieval=true"
                        + "&useSSL=true"
                        + "&verifyServerCertificate=false";

        // Connect to MySQL server
        try (Connection conn =
                     DriverManager.getConnection(serverUrl, username, password);
             Statement stmt = conn.createStatement()) {

            // Create database only if it doesn't already exist
            stmt.executeUpdate(
                    "CREATE DATABASE IF NOT EXISTS `" + dbName + "`"
            );
        }
    }
}