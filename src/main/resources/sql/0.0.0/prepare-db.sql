--liquibase formatted sql


--changeset reference:prepare-db context:ddl runOnChange:false
create extension if not exists pgcrypto;
--rollback select 1;