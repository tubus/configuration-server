--liquibase formatted sql


--changeset reference:create-configuration_value context:ddl runOnChange:false
create table public.configuration_value
(
    id uuid not null constraint configuration_value_pkey primary key,
    value text not null,
    component_configuration_id uuid null constraint fk_configuration_value_component_configuration references component_configuration (id),
    environment_id uuid not null constraint fk_configuration_value_environment references environment (id),
    environment_host_id uuid not null constraint fk_configuration_value_environment_host references environment_host (id)
);

alter table public.configuration_value owner to postgres;

comment on table  public.configuration_value is 'Component application';
comment on column public.configuration_value.id is 'ID';
comment on column public.configuration_value.value is 'Name';
comment on column public.configuration_value.component_configuration_id is 'Description';
comment on column public.configuration_value.environment_id is 'Environment ID';
comment on column public.configuration_value.environment_host_id is 'Environment host ID';

--rollback drop table public.configuration_value;