# Baralga Backend

Backend for multi user usage of the Baralga time tracking application.

## Administration

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

A health check is available at `http://localhost/actuator/health`.
