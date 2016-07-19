# DbShare
DbShare is a management plugin for HikariDataSources. By building the
data sources once and reusing them in various plugins as needed, the
programmer can save time and memory.

# Step One: Edit the configuration file (config.yml)
```yaml
# DbShare Configuration File

# List of data source that plugins can access.
Databases:

  # Name of the data source
  MainDbShare:
    # Username for connecting to the database.
    Username: mc
    # Password for connecting to the database.
    Password: dbshare
    # This should have three parts: the main URL, the port number, and database name.
    URL: localhost:3306/minecraft
```

# Step Two: Use it in your plugin
```java
public void updateDatabase() {
  HikariDataSource dataSource = DbShare.getDataSource("MainDbShare");
  
  try(Connection connection = dataSource.getConnection()) {
    // Use the connection
  };
}
```

# Licence ([GPLv3](http://www.gnu.org/licenses/gpl-3.0.en.html))
```
DbShare - Shared HikariDataSource manager for Spigot
Copyright (C) 2015  Trace Bachi (tracebachi@gmail.com)

DbShare is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

DbShare is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with DbShare.  If not, see <http://www.gnu.org/licenses/>.
```
