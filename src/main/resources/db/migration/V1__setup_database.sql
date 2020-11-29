create table project (
     project_id   varchar(36) not null,
     title        varchar(255),
     description  varchar(4000),
     active       boolean
);

ALTER TABLE project
ADD CONSTRAINT pk_project PRIMARY KEY (project_id);

create table activity (
     activity_id  varchar(36) not null,
     description  varchar(4000),
     user         varchar(36) not null,
     start        timestamp,
     end          timestamp,
     project_id   varchar(36) not null,
     FOREIGN key (project_id) REFERENCES project(project_id)
);

ALTER TABLE activity
ADD CONSTRAINT pk_activity PRIMARY KEY (activity_id);

ALTER TABLE activity
ADD CONSTRAINT fk_activity_project
FOREIGN KEY (project_id) REFERENCES project (project_id);

CREATE INDEX activity_idx_user
ON activity (user, start);

CREATE TABLE users (
  username VARCHAR(50) NOT NULL,
  password VARCHAR(100) NOT NULL,
  enabled TINYINT NOT NULL DEFAULT 1,
  PRIMARY KEY (username)
);

CREATE TABLE authorities (
  username VARCHAR(50) NOT NULL,
  authority VARCHAR(50) NOT NULL,
  FOREIGN KEY (username) REFERENCES users(username)
);

CREATE UNIQUE INDEX authorities_idx_username
  on authorities (username,authority);

INSERT INTO users (username, password, enabled)
  values ('admin',
        '$2a$10$NuzYobDOSTCx/EKBClGwGe0A9c8/yC7D4IP75hwz1jn.RCBfdEtb2', -- adm1n
        1
);
INSERT INTO authorities (username, authority)
  values ('admin', 'ADMIN');

INSERT INTO users (username, password, enabled)
  values ('user1',
        '$2a$10$IhFsXJYqYG56/b1JgzZzv.kPcPsJnXeQzD9evMOUHg2LT/.Oz9uEu', -- us3r
        1
);
INSERT INTO authorities (username, authority)
  values ('user1', 'USER');


INSERT INTO users (username, password, enabled)
  values ('user2',
        '$2a$10$IhFsXJYqYG56/b1JgzZzv.kPcPsJnXeQzD9evMOUHg2LT/.Oz9uEu', -- us3r
        1
);
INSERT INTO authorities (username, authority)
  values ('user2', 'USER');
