[![Github Actions Status for Baralga/baralga](https://github.com/baralga/backend/workflows/Build/badge.svg)](https://github.com/Baralga/baralga/actions) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=baralga-backend&metric=alert_status)](https://sonarcloud.io/dashboard?id=baralga-backend)

# Baralga Backend

Backend for multi user usage of the Baralga time tracking application.

## Administration

### Running the server backend

To run the server just start executable jar with the command `java -jar baralga-backend-0.0.1.jar`. You need at 
least Java version 13 but newer ones should work too.

### Accessing the Web User Interface

The web user interface is available at `http://localhost:8080/`. You can log in as administrator with `admin/adm1n` or as user with `user1/us3r`.

### Configuration

The backend is configured using the following environment variables:

| Environment Variable  | Default Value                        | Description  |
| --------------------- |:------------------------------------| :--------|
| `BARALGA_DS_URL`      | `jdbc:mysql://localhost:3306/baralga`| JDBC Connection String for database |
| `BARALGA_DS_USER`     | `root`                       |   database user |
| `BARALGA_DS_PASSWORD` | `mysql`      |    database password |
| `SERVER_PORT` | `8080`      |    http server port |

### Users and Roles

Baralga supports the following roles:

| Role  | DB Name | Description                        |
| ----- |:------- |:------------------------------------|
| User  | `ROLE_USER` |Full access to his own activities but can only read projects. |
| Admin | `ROLE_ADMIN`  | Full access to activities of all users and projects.          |


#### Administration

Users are stored in the database in the table `users` and the user role in the table `authorities`.  Users can be 
created using the following sql statements.

```mysql-sql
INSERT INTO users (username, password, enabled)
  values (
    'user2',
    '$2a$10$IhFsXJYqYG56/b1JgzZzv.kPcPsJnXeQzD9evMOUHg2LT/.Oz9uEu', -- us3r
    1 -- enabled
);
INSERT INTO authorities (username, authority)
  values ('user2', 'ROLE_USER');
```

Passwords are encoded in BCrypt with BCrypt version `$2a` and strength 10. The tool https://8gwifi.org/bccrypt.jsp
can be used to create a hashed password to be used in sql.

### Supported Databases

Supported databases are:
* [MySQL](https://www.mysql.com/)
* [PostgreSQL](https://www.postgresql.org/)

#### MySQL Configuration
```
BARALGA_DS_URL=jdbc:mysql://localhost:3306/baralga
BARALGA_DS_USER=root
BARALGA_DS_PASSWORD=mysql
```

#### PostgreSQL Configuration
```bash
BARALGA_DS_URL=jdbc:postgresql://localhost:5432/baralga
BARALGA_DS_USER=postgres
BARALGA_DS_PASSWORD=postgres
```
                         
### Health Check

A health check is available at `http://localhost:8080/actuator/health`.


### Troubleshooting


#### MySQL error `Invalid default value for 'end_time'`

If you get the following error on MySql:

```
Caused by: org.flywaydb.core.internal.sqlscript.FlywaySqlScriptException:
Migration V1__setup_database.sql failed
---------------------------------------
SQL State  : 42000
Error Code : 1067
Message    : Invalid default value for 'end_time'
Location   : db/migration/V1__setup_database.sql (/home/valadmin/file:/home/valadmin/baralga-backend-0.1.0.jar!/BOOT-INF/classes!/db/migration/V1__setup_database.sql)
Line       : 11
Statement  : create table activity (
     activity_id  varchar(36) not null,
     description  varchar(4000),
     username     varchar(36) not null,
     start_time   timestamp,
     end_time     timestamp,
     project_id   varchar(36) not null,
     FOREIGN key (project_id) REFERENCES project(project_id)
)
```

**Solution**

This can be fixed with the following settings:
```
Sql_mode =ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
```

## Development

## Run ui build
The ui is build with parcel.js which bundles and compresses all javascript and css. The ui build copies the hashed
assets to the respective maven directories.

    mvn generate-resources -P ui