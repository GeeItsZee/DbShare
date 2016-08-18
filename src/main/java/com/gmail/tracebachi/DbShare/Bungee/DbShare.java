package com.gmail.tracebachi.DbShare.Bungee;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 1/23/16.
 */
public class DbShare extends Plugin
{
    private Configuration config;
    private static HashMap<String, HikariDataSource> sources;

    @Override
    public synchronized void onEnable()
    {
        reloadConfig();

        sources = new HashMap<>();
        createDataSources();
    }

    @Override
    public synchronized void onDisable()
    {
        closeDataSources();
        sources.clear();
        sources = new HashMap<>();
    }

    public static synchronized HikariDataSource getDataSource(String name)
    {
        return sources.get(name.toLowerCase());
    }

    private void createDataSources()
    {
        Configuration section = config.getSection("Databases");
        Collection<String> sourceNames = section.getKeys();

        for(String sourceName : sourceNames)
        {
            String username = section.getString(sourceName + ".Username");
            String password = section.getString(sourceName + ".Password");
            String url = section.getString(sourceName + ".URL");

            try
            {
                getLogger().info("Creating DataSource {name: " + sourceName.toLowerCase() + ") ...");
                HikariDataSource dataSource = createDataSource(username, password, url);

                try(Connection connection = dataSource.getConnection())
                {
                    try(Statement statement = connection.createStatement())
                    {
                        getLogger().info("Testing DataSource connection {name: " +
                            sourceName.toLowerCase() + ") ...");

                        statement.execute("SELECT 1;");
                    }
                }

                sources.put(sourceName.toLowerCase(), dataSource);
            }
            catch(Exception ex)
            {
                getLogger().severe("Failed to create DataSource: " + sourceName.toLowerCase());
                ex.printStackTrace();
            }
        }
    }

    private synchronized void closeDataSources()
    {
        for(Map.Entry<String, HikariDataSource> entry : sources.entrySet())
        {
            try
            {
                // Close the data source
                if(entry.getValue() != null)
                {
                    getLogger().info("Closing DataSource {name: " + entry.getKey() + "} ...");
                    entry.getValue().close();
                }
            }
            catch(Throwable throwable)
            {
                getLogger().severe("Failed to close DataSource: " + entry.getKey());
                throwable.printStackTrace();
            }
        }
    }

    private HikariDataSource createDataSource(String username, String password, String url)
    {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + url);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        return new HikariDataSource(config);
    }

    private void reloadConfig()
    {
        try
        {
            File file = ConfigUtil.saveResource(this, "config.yml", "config.yml");
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);

            if(config == null)
            {
                ConfigUtil.saveResource(this, "config.yml", "config.yml", true);
            }
        }
        catch(IOException e)
        {
            getLogger().severe("Failed to load configuration file.");
            e.printStackTrace();
        }
    }
}
