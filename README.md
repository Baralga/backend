# Baralga Backend

Backend for multi user usage of the Baralga time tracking application.

## Administration

### User Administration
Users are stored in the database in the table `users` and the user role in the table `authorities`.  Users can be 
created using the following sql statements.

```mysql-sql
INSERT INTO users (username, password, enabled)
  values (
    'user2',
    '$2a$10$tHZDvbBK5.43Dw/rOnbg4e8.o1Js4/T7kb2tXJgM7I2ZhnsBECeAa', -- us3r2
    1 -- enabled
);
INSERT INTO authorities (username, authority)
  values ('user2', 'USER');
```

Passwords are encoded in BCrypt with BCrypt version `$2a` and strength 10. The tool https://8gwifi.org/bccrypt.jsp
can be used to create a hashed password to be used in sql.
                         
