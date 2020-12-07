--liquibase formatted sql


--changeset reference:update-configuration context:ddl runOnChange:false
alter table configuration drop constraint uk_configuration_full_path;
alter table configuration add constraint uk_configuration_full_path_group unique (path, "group");
--rollback alter table configuration drop constraint uk_configuration_full_path_group; alter table configuration add constraint uk_configuration_full_path unique (path);
