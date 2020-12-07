--liquibase formatted sql


--changeset reference:create-permission context:ddl runOnChange:false
create table public.permission
(
    id uuid not null constraint permission_pkey primary key,
    account_id uuid not null constraint fk_permission_account references account(id),
    source_name text not null,
    source_id uuid not null,
    read_access boolean not null,
    create_access boolean not null,
    edit_access boolean not null,
    delete_access boolean not null,
    utc timestamp not null
);

alter table public.permission owner to postgres;

comment on table  public.permission is 'Account rights';
comment on column public.permission.id is 'ID';
comment on column public.permission.source_name is 'Name of source';
comment on column public.permission.source_id is 'ID of access object in source';
comment on column public.permission.read_access is 'Access to read';
comment on column public.permission.create_access is 'Access to create';
comment on column public.permission.edit_access is 'Access to edit';
comment on column public.permission.delete_access is 'Access to delete';
comment on column public.permission.utc is 'Update utc';
--rollback drop table public.permission;