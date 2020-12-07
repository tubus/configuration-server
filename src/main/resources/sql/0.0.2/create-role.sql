--liquibase formatted sql


--changeset reference:create-role context:ddl runOnChange:false
create table public.role
(
    id uuid not null constraint role_pkey primary key,
    "name" text not null constraint uk_role_name unique,
    description text null,
    creation_utc timestamp not null
);

alter table public.role owner to postgres;

comment on table  public.role is 'User account';
comment on column public.role.id is 'ID';
comment on column public.role.name is 'Name';
comment on column public.role.description is 'Description';
comment on column public.role.creation_utc is 'Creation utc';
--rollback drop table public.role;


--changeset reference:fill-role context:reference runOnChange:false
insert into role (id, name, description, creation_utc) values
    ('6125ac77-6609-4b50-8542-2ebf5c692519', 'SUPER_ADMINISTRATOR', 'Super administrator role', now()),
    ('e1a9b01a-5068-471d-ad6d-d15901618bf8', 'USER_ADMINISTRATOR',  'User administrator role',  now()),
    ('6cb9dc56-8447-4c50-8007-ee35232b6ae2', 'USER',                'User role',                now());
--rollback delete from public.role;