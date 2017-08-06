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

import com.google.common.base.Preconditions;

/**
 * @author GeeItsZee (tracebachi@gmail.com)
 */
class DataSourceDetails
{
  private final String sourceName;
  private final String username;
  private final String password;
  private final String url;

  public DataSourceDetails(String sourceName, String username, String password, String url)
  {
    this.sourceName = Preconditions.checkNotNull(sourceName, "sourceName").toLowerCase();
    this.username = Preconditions.checkNotNull(username, "username");
    this.password = Preconditions.checkNotNull(password, "password");
    this.url = Preconditions.checkNotNull(url, "url");
  }

  public String getSourceName()
  {
    return sourceName;
  }

  public String getUsername()
  {
    return username;
  }

  public String getPassword()
  {
    return password;
  }

  public String getUrl()
  {
    return url;
  }

  @Override
  public String toString()
  {
    return "{sourceName: '" + sourceName + "', username: '" + username + "', url: '" + url + "'}";
  }
}
