--liquibase formatted sql


--changeset reference:update-environment_host context:ddl runOnChange:false
alter table environment_host rename to environment_profile;
alter table environment_profile rename constraint environment_host_pkey to environment_profile_pkey;
alter table environment_profile drop constraint uk_ip_name;
alter table environment_profile drop column ip;
alter table environment_profile rename constraint fk_environment_environment_host to fk_environment_environment_profile;

comment on table environment_profile is 'Environment profile';

--rollback comment on table environment_profile is 'Environment host'; alter table environment_profile rename constraint fk_environment_environment_profile to fk_environment_environment_host; alter table environment_profile add column ip text not null default ''; comment on column environment_profile.ip is 'IP address'; alter table environment_profile add constraint uk_ip_name unique (ip, environment_id); alter table environment_profile rename constraint environment_profile_pkey to environment_host_pkey; alter table environment_profile rename to environment_host;
