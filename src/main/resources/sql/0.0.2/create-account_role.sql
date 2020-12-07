--liquibase formatted sql


--changeset reference:create-account_role context:ddl runOnChange:false
create table public.account_role
(
    id uuid not null constraint account_role_pkey primary key,
    account_id uuid not null constraint fk_account_role_account references account(id),
    role_id uuid not null constraint fk_account_role_role references role(id),
    constraint uk_account_role unique (account_id, role_id)
);

alter table public.account_role owner to postgres;

comment on table  public.account_role is 'User account';
comment on column public.account_role.id is 'ID';
comment on column public.account_role.account_id is 'Account ID';
comment on column public.account_role.role_id is 'Role ID';
--rollback drop table public.account_role;