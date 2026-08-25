package com.dinuka.dev.user_service.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
    public DataSource dataSource() {

        System.out.println("\n========================================");
        System.out.println("   DATASOURCE INITIALIZATION STARTED");
        System.out.println("========================================");

        try {

            System.out.println("[1] JDBC URL:");
            System.out.println(url);

            System.out.println("[2] Username:");
            System.out.println(username);

            System.out.println("[3] Password length:");
            System.out.println(password == null ? "NULL" : password.length());

            System.out.println("[4] Driver:");
            System.out.println(driverClassName);

            System.out.println("[5] Creating database if not exists...");

            createDatabaseIfNotExists();

            System.out.println("[6] Database creation/check SUCCESS");

            System.out.println("[7] Creating HikariDataSource...");

            HikariDataSource ds = new HikariDataSource();

            System.out.println("[8] Setting JDBC URL...");

            ds.setJdbcUrl(url);

            System.out.println("[9] Setting username...");

            ds.setUsername(username);

            System.out.println("[10] Setting password...");

            ds.setPassword(password);

            System.out.println("[11] Setting driver...");

            ds.setDriverClassName(driverClassName);

            System.out.println("[12] HikariDataSource created SUCCESS");

            System.out.println("========================================");
            System.out.println("   DATASOURCE INITIALIZATION COMPLETE");
            System.out.println("========================================\n");

            return ds;

        } catch (Exception e) {

            System.out.println("\n========================================");
            System.out.println("   DATASOURCE INITIALIZATION FAILED");
            System.out.println("========================================");

            System.out.println("Exception Type:");
            System.out.println(e.getClass().getName());

            System.out.println("Exception Message:");
            System.out.println(e.getMessage());

            System.out.println("\nFULL STACK TRACE:");
            e.printStackTrace();

            System.out.println("========================================\n");

            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private void createDatabaseIfNotExists() throws SQLException {

        System.out.println("\n----------------------------------------");
        System.out.println("CREATE DATABASE CHECK");
        System.out.println("----------------------------------------");

        System.out.println("[DB-1] Original URL:");
        System.out.println(url);

        // Extract database name
        String dbName = url.substring(
                url.lastIndexOf('/') + 1
        );

        System.out.println("[DB-2] Extracted database part:");
        System.out.println(dbName);

        if (dbName.contains("?")) {
            dbName = dbName.substring(
                    0,
                    dbName.indexOf('?')
            );
        }

        System.out.println("[DB-3] Database name:");
        System.out.println(dbName);

        // Remove database name from URL
        String baseUrl = url.substring(
                0,
                url.lastIndexOf('/') + 1
        );

        System.out.println("[DB-4] Base URL:");
        System.out.println(baseUrl);

        String serverUrl =
                baseUrl
                        + "?allowPublicKeyRetrieval=true"
                        + "&useSSL=true"
                        + "&verifyServerCertificate=false";

        System.out.println("[DB-5] Server connection URL:");
        System.out.println(serverUrl);

        System.out.println("[DB-6] Username:");
        System.out.println(username);

        System.out.println("[DB-7] Password length:");
        System.out.println(
                password == null ? "NULL" : password.length()
        );

        System.out.println("[DB-8] Attempting JDBC connection...");

        try (Connection conn =
                     DriverManager.getConnection(
                             serverUrl,
                             username,
                             password
                     )) {

            System.out.println("[DB-9] JDBC CONNECTION SUCCESS");

            System.out.println("[DB-10] Connection class:");
            System.out.println(conn.getClass().getName());

            System.out.println("[DB-11] Checking connection...");

            System.out.println(
                    "[DB-12] Connection valid: "
                            + conn.isValid(5)
            );

            System.out.println("[DB-13] Getting database metadata...");

            var metadata = conn.getMetaData();

            System.out.println(
                    "[DB-14] Database product: "
                            + metadata.getDatabaseProductName()
            );

            System.out.println(
                    "[DB-15] Database version: "
                            + metadata.getDatabaseProductVersion()
            );

            System.out.println(
                    "[DB-16] JDBC URL reported by driver: "
                            + metadata.getURL()
            );

            System.out.println(
                    "[DB-17] Connected username: "
                            + metadata.getUserName()
            );

            System.out.println("[DB-18] Creating Statement...");

            try (Statement stmt = conn.createStatement()) {

                System.out.println("[DB-19] Statement created");

                String sql =
                        "CREATE DATABASE IF NOT EXISTS `"
                                + dbName
                                + "`";

                System.out.println("[DB-20] SQL:");
                System.out.println(sql);

                System.out.println("[DB-21] Executing CREATE DATABASE...");

                stmt.executeUpdate(sql);

                System.out.println(
                        "[DB-22] CREATE DATABASE SUCCESS"
                );
            }

            System.out.println("[DB-23] Checking databases...");

            try (Statement stmt = conn.createStatement();
                 ResultSet rs =
                         stmt.executeQuery("SHOW DATABASES")) {

                while (rs.next()) {

                    String database = rs.getString(1);

                    System.out.println(
                            "[DB-24] Database found: "
                                    + database
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println("\n----------------------------------------");
            System.out.println("DATABASE CONNECTION FAILED");
            System.out.println("----------------------------------------");

            System.out.println("SQL Error Code:");
            System.out.println(e.getErrorCode());

            System.out.println("SQL State:");
            System.out.println(e.getSQLState());

            System.out.println("Message:");
            System.out.println(e.getMessage());

            System.out.println("\nFULL SQL EXCEPTION:");
            e.printStackTrace();

            System.out.println("----------------------------------------\n");

            throw e;
        }
    }
}