/*
 * DbShare - Multiple HikariDataSource manager for Spigot
 * Copyright (C) 2017 tracebachi@gmail.com (GeeItsZee)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.gmail.tracebachi.DbShare;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
public class DbShare
{
  /* Start: For singleton reference */

  private static DbShare instance;

  public static DbShare instance()
  {
    return instance;
  }

  static void setInstance(DbShare newInstance)
  {
    instance = newInstance;
  }

  /* End: For singleton reference */

  private final Map<String, HikariDataSource> sources;

  DbShare()
  {
    sources = new HashMap<>();
  }

  public synchronized HikariDataSource getDataSource(String name)
  {
    return sources.get(name.toLowerCase());
  }

  synchronized void createDataSources(
    List<DataSourceDetails> dataSourceDetailsList, Logger logger)
  {
    for (DataSourceDetails details : dataSourceDetailsList)
    {
      try
      {
        // Create the data source
        HikariDataSource dataSource = createDataSource(details);

        try (Connection connection = dataSource.getConnection())
        {
          try (Statement statement = connection.createStatement())
          {
            // Test the data source with a simple statement
            statement.execute("SELECT 1;");
          }
        }

        sources.put(details.getSourceName(), dataSource);
        logger.info("Created DataSource " + details + ".");
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        logger.severe("Failed to create DataSource '" + details + "'.");
      }
    }
  }

  synchronized void closeAndRemoveDataSources(Logger logger)
  {
    Iterator<Map.Entry<String, HikariDataSource>> iter = sources.entrySet().iterator();

    while (iter.hasNext())
    {
      Map.Entry<String, HikariDataSource> entry = iter.next();
      String sourceName = entry.getKey();
      HikariDataSource dataSource = entry.getValue();

      // Remove in all cases
      iter.remove();

      if (dataSource != null)
      {
        try
        {
          // Close the data source
          dataSource.close();
          logger.info("Closed DataSource '" + sourceName + "'.");
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
          logger.severe("Failed to close DataSource '" + sourceName + "'.");
        }
      }
    }
  }

  private HikariDataSource createDataSource(DataSourceDetails dataSourceDetails)
  {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://" + dataSourceDetails.getUrl());
    config.setUsername(dataSourceDetails.getUsername());
    config.setPassword(dataSourceDetails.getPassword());
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    return new HikariDataSource(config);
  }
}
