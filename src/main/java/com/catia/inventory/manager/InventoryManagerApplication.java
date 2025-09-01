package com.catia.inventory.manager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication
public class InventoryManagerApplication {

    public static void main(String[] args) {

        DataSource dataSource = createDataSource();

        try (Connection connection = dataSource.getConnection()){
            System.out.println("connection.isvalid(0)=" + connection.isValid(0));
        }catch(Exception e){
            System.out.println(e.toString());
        }

        SpringApplication.run(InventoryManagerApplication.class, args);
    }

    private static DataSource createDataSource() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/inventory_manager");
        config.setUsername("cc");
        config.setPassword("invpass");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");

        return new HikariDataSource(config);
    }

}
