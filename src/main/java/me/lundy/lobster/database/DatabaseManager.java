package me.lundy.lobster.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.lundy.lobster.Lobster;
import me.lundy.lobster.config.BotConfig;
import me.lundy.lobster.config.DatabaseConfig;

public class DatabaseManager {

    private final HikariDataSource dataSource;

    public DatabaseManager() {
        HikariConfig config = new HikariConfig();
        BotConfig botConfig = Lobster.getInstance().getConfig();
        DatabaseConfig databaseConfig = botConfig.getDatabaseConfig();
        config.setJdbcUrl(databaseConfig.getUrl());
        config.setPassword(databaseConfig.getPassword());
        config.setUsername(databaseConfig.getUsername());
        this.dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

}
