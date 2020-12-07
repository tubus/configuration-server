--liquibase formatted sql


--changeset reference:create-configuration context:ddl runOnChange:false
create table public.configuration
(
    id uuid not null constraint configuration_pkey primary key,
    name text not null,
    path text not null constraint uk_configuration_full_path unique,
    description text null,
    configuration_pattern_id uuid null constraint fk_configuration_configuration_pattern references configuration_pattern(id),
    "group" boolean not null,
    parent_id uuid constraint lk_configuration_id references configuration(id)
);

alter table public.configuration owner to postgres;

comment on table  public.configuration is 'Component application';
comment on column public.configuration.id is 'ID';
comment on column public.configuration.name is 'Name';
comment on column public.configuration.path is 'Full configuration path';
comment on column public.configuration.description is 'Description';
comment on column public.configuration.configuration_pattern_id is 'Configuration Pattern ID';
comment on column public.configuration.group is 'Group of configurations';
comment on column public.configuration.parent_id is 'Configuration parent ID';

--rollback drop table public.configuration;