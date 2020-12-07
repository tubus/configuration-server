--liquibase formatted sql


--changeset reference:create-account context:ddl runOnChange:false
create table public.account
(
    id uuid not null constraint account_pkey primary key,
    "name" text not null constraint uk_account_name unique,
    encrypted_password text not null,
    creation_utc timestamp not null,
    utc timestamp not null
);

alter table public.account owner to postgres;

comment on table  public.account is 'User account';
comment on column public.account.id is 'ID';
comment on column public.account.name is 'Name';
comment on column public.account.encrypted_password is 'Description';
comment on column public.account.creation_utc is 'Creation utc';
comment on column public.account.utc is 'Update utc';
--rollback drop table public.account;