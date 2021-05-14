create table organization (
     org_id   varchar(36) not null,
     title        varchar(255),
     description  varchar(4000)
);

INSERT INTO organization (org_id, title, description)
    values ('4ed0c11d-3d6a-41c1-9873-558e86084591', 'main', null);

ALTER TABLE organization
    ADD CONSTRAINT pk_organization PRIMARY KEY (org_id);

ALTER TABLE activity
    ADD org_id varchar(36) not null default '4ed0c11d-3d6a-41c1-9873-558e86084591';

ALTER TABLE project
    ADD org_id varchar(36) not null default '4ed0c11d-3d6a-41c1-9873-558e86084591';

ALTER TABLE users
    ADD org_id varchar(36) not null default '4ed0c11d-3d6a-41c1-9873-558e86084591';
