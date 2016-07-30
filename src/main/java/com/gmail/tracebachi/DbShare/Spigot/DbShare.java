package com.gmail.tracebachi.DbShare.Spigot;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Trace Bachi (tracebachi@gmail.com, BigBossZee) on 1/23/16.
 */
public class DbShare extends JavaPlugin
{
    private static HashMap<String, HikariDataSource> sources;

    @Override
    public void onLoad()
    {
        saveDefaultConfig();
    }

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
        ConfigurationSection section = getConfig().getConfigurationSection("Databases");
        Set<String> sourceNames = section.getKeys(false);

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

    private void closeDataSources()
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
}
