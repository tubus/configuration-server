--liquibase formatted sql


--changeset reference:create-component context:ddl runOnChange:false
create table public.component
(
    id uuid not null constraint component_pkey primary key,
    name text not null constraint uk_component_name unique,
    description text null,
    creation_utc timestamp not null,
    utc timestamp not null
);

alter table public.component owner to postgres;

comment on table  public.component is 'Component application';
comment on column public.component.id is 'ID';
comment on column public.component.name is 'Name';
comment on column public.component.description is 'Description';
comment on column public.component.creation_utc is 'Creation utc';
comment on column public.component.utc is 'Update utc';
--rollback drop table public.component;