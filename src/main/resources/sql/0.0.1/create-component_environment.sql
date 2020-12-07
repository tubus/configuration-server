--liquibase formatted sql


--changeset reference:create-component_environment context:ddl runOnChange:false
create table public.component_environment
(
    component_id uuid not null constraint fk_component_environment_component_id references component(id),
    environment_id uuid not null constraint fk_component_environment_environment_id references environment(id),
    creation_utc timestamp not null,
    constraint component_environment_pkey primary key (component_id, environment_id)
);

alter table public.component_environment owner to postgres;

comment on table  public.component_environment is 'Component - Environment link';
comment on column public.component_environment.component_id is 'Component ID';
comment on column public.component_environment.environment_id is 'Environment ID';
comment on column public.component_environment.creation_utc is 'Creation utc';
--rollback drop table public.component_environment;