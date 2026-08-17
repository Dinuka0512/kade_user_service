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
        createDatabaseIfNotExists();

        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setDriverClassName(driverClassName);
        return ds;
    }

    private void createDatabaseIfNotExists() throws SQLException {
        String dbName = url.substring(url.lastIndexOf('/') + 1);
        if (dbName.contains("?")) dbName = dbName.substring(0, dbName.indexOf('?'));
        String serverUrl = url.substring(0, url.lastIndexOf('/') + 1) + "?allowPublicKeyRetrieval=true&useSSL=false";

        try (Connection conn = DriverManager.getConnection(serverUrl, username, password);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
            System.out.println("Database '" + dbName + "' is ready.");
        }
    }
}
