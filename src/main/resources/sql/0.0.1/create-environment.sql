--liquibase formatted sql


--changeset reference:create-environment context:ddl runOnChange:false
create table public.environment
(
    id uuid not null constraint environment_pkey primary key,
    name text not null constraint uk_environment_name unique,
    description text null,
    creation_utc timestamp not null,
    utc timestamp not null
);

alter table public.environment owner to postgres;

comment on table  public.environment is 'Logical component container';
comment on column public.environment.id is 'ID';
comment on column public.environment.name is 'Name';
comment on column public.environment.description is 'Description';
comment on column public.environment.creation_utc is 'Creation utc';
comment on column public.environment.utc is 'Update utc';
--rollback drop table public.environment;


--changeset reference:fill-environment context:reference runOnChange:false
INSERT INTO public.environment (id, name, description, creation_utc, utc) VALUES (gen_random_uuid(), 'LOCALHOST', 'Local developer machine', now(), now());
--rollback delete from public.environment;