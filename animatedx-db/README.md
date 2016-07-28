# AnimatedX Database

MariaDB version: 10.0.X (Currently 10.0.12).

[http://mariadb.org/](https://mariadb.org/)

The AnimatedX database instance is named `anix`.

## Setup

### Installation

##### Windows

Download the 64 bit MSI version of MariaDB 10 from [https://downloads.mariadb.org/](https://downloads.mariadb.org/).

Use the default installation options, although the installation location is changeable.

Set the password of the MariaDB `root` user to `mariadb`.

Check the `Use UTF8 as default server's character set`.

##### Linux

Go to [https://downloads.mariadb.org/mariadb/repositories/](https://downloads.mariadb.org/mariadb/repositories/) and following the procedure to add the repository to the package manager.

Set the password of the MariaDB `root` user to `mariadb`.

#### Character encoding and collation

The character set and collation encoding _have_ to be set. Without this there will be text encoding issues, and sorting/equality issues.

Open the MariaDB configuration file and add/change the following settings:

- In the `[mysql]` and `[client]` sections:
    - Change the `default-character-set` to `utf8`.

- In the `[mysqld]` section:
    - Change the `character-set-server` to `utf8`.
    - Change the `collation_server` to `utf8_general_ci`.

The configuration file is called `my.ini` on Windows and located in the MariaDB `bin` directory.

On Linux the configuration files are split up into different files handling different ares, but are all located in the `/etc/mysql` directory tree.

### Setting up the AnimatedX database

The scripts for creating the users and database are located in `src/main/resources/db/setup`.

The scripts can be run from the command line using the `mysql` application; `cd` to the `$PROJECT_ROOT/animatedx-db/src/main/resources/db/setup` directory.

- Run the `V0.1.5__Create_anix_database_and_users.sql` script first.

    `mysql -u root -p < V0.1.5__Create_anix_database_and_users.sql`

Enter the password for the root user from above.

## WildFly

Changes here might also affect the WildFly configuration, if so, the configuration has to be re-run as explained in the [WildFly upgrade](../wildfly/README.md#upgrade).

## Database evolutions

We do not use automatic evolutions, such as used by Rails, Play or any similar frameworks. Instead we use a the Flyway database migration tool, [http://flywaydb.org/](http://flywaydb.org/).

Run

    mvn clean compile flyway:migrate
    
in the `$PROJECT_ROOT/animatedx-db` directory, to do the automatic migration. The Flyway plugin requires that the `compile` is run to find the sources.

### Errors

Although this should never happen, there are situations where it is possible during development. 

If an error occurs during migration, the migration version will be set to an error state. `mvn flyway:repair` resets the state so the migration can be run again after the error in the script has been fixed.

If an error is encountered during migrations, or an error is encountered during runtime, the database schema can be reset. Running the following command accomplishes this:

    mvn compile flyway:clean

## Development
Database evolutions are stored in the `$PROJECT_ROOT/src/main/resources/db/migration` directory. The naming of directories and script follow both the Flyway and semantic versioning scheme ([http://semver.org/](http://semver.org/).

Directories are created in a `V{MAJOR}/{MINOR}/{PATCH}` scheme, and files are named `V{MAJOR}.{MINOR}.{PATCH}__{descriptive name}.sql`. There can be ***only*** one file per version, otherwise Flyway borks.

The sql script should be a complete functional script, with ***no*** transactions, transactions are handled by the Flyway engine. All alterations of the schema should be included. Each file should only handle on "logical" unit of work, e.g.:

- create one table with associated sequence and indices.
- one feature, e.g. add one column to one table, add a foreign key constraint to that column in another table.

A script should ***never*** be altered, a new script should be created instead.

If a pull contains a script with the same version number as one you have just created locally, ***re-version*** your script.

For now we only use pure SQL scripts, and avoid Java-based migration scripts, this might be re-evaluated in the future.

