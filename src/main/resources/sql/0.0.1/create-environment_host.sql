--liquibase formatted sql


--changeset reference:create-environment_host context:ddl runOnChange:false
create table public.environment_host
(
    id uuid not null constraint environment_host_pkey primary key,
    ip text not null,
    name text not null,
    description text null,
    environment_id uuid not null constraint fk_environment_environment_host references environment,
    creation_utc timestamp not null,
    utc timestamp not null,
    constraint uk_environment_id_name unique (environment_id, name),
    constraint uk_ip_name unique (ip, environment_id)
);

alter table public.environment_host owner to postgres;

comment on table  public.environment_host is 'Environment host';
comment on column public.environment_host.id is 'ID';
comment on column public.environment_host.ip is 'IP address';
comment on column public.environment_host.name is 'Name';
comment on column public.environment_host.description is 'Description';
comment on column public.environment_host.environment_id is 'Environment ID';
comment on column public.environment_host.creation_utc is 'Creation utc';
comment on column public.environment_host.utc is 'Update utc';
--rollback drop table public.environment_host;


--changeset reference:fill-environment_host context:reference runOnChange:false
INSERT INTO public.environment_host (id, ip, name, description, environment_id, creation_utc, utc) VALUES (gen_random_uuid(), '0.0.0.0', 'localhost', 'Local developer machine', (select e.id from environment e where e.name = 'LOCALHOST'), now(), now());
--rollback delete from public.environment_host;