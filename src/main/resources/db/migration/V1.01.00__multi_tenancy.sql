ALTER TABLE activity
    ADD tenant_id varchar(36) not null default '4ed0c11d-3d6a-41c1-9873-558e86084591';

ALTER TABLE project
    ADD tenant_id varchar(36) not null default '4ed0c11d-3d6a-41c1-9873-558e86084591';

ALTER TABLE users
    ADD tenant_id varchar(36) not null default '4ed0c11d-3d6a-41c1-9873-558e86084591';
