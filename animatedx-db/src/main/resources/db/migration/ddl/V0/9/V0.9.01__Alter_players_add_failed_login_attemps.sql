alter table players
  add column failed_login_attempts  integer   not null default 0,
  add column last_failed_login_date timestamp null;

alter table players
  alter column failed_login_attempts drop default;
