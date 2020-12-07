--liquibase formatted sql


--changeset reference:create-configuration_pattern context:ddl runOnChange:false
create table public.configuration_pattern
(
    id uuid not null constraint configuration_pattern_pkey primary key,
    name text not null constraint uk_configuration_pattern_name unique,
    description text null
);

alter table public.configuration_pattern owner to postgres;

comment on table  public.configuration_pattern is 'Configuration pattern';
comment on column public.configuration_pattern.id is 'ID';
comment on column public.configuration_pattern.name is 'Name';
comment on column public.configuration_pattern.description is 'Description';
--rollback drop table public.configuration_pattern;