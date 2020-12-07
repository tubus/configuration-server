--liquibase formatted sql


--changeset reference:create-component_configuration context:ddl runOnChange:false
create table public.component_configuration
(
    id uuid not null constraint component_configuration_pkey primary key,
    component_id uuid null constraint fk_component_configuration_value_component references component,
    configuration_id uuid not null constraint fk_component_configuration_value_configuration references configuration,
    constraint uk_component_id_configuration_id unique (component_id, configuration_id)
);

alter table public.component_configuration owner to postgres;

comment on table  public.component_configuration is 'Component configuration';
comment on column public.component_configuration.id is 'ID';
comment on column public.component_configuration.component_id is 'Component ID';
comment on column public.component_configuration.configuration_id is 'Configuration ID';

--rollback drop table public.component_configuration;