--liquibase formatted sql


--changeset reference:update-configuration_value context:ddl runOnChange:false
alter table configuration_value rename constraint fk_configuration_value_environment_host to fk_configuration_value_environment_profile;
alter table configuration_value rename column environment_host_id to environment_profile_id;
comment on column configuration_value.environment_profile_id is 'Environment profile ID';
--rollback alter table configuration_value rename column environment_profile_id to environment_host_id; comment on column configuration_value.environment_host_id is 'Environment host ID'; alter table configuration_value rename constraint fk_configuration_value_environment_profile to fk_configuration_value_environment_host;
