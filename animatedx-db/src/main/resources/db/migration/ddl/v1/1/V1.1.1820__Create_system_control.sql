create table system_control
(
  id                    bigint    not null auto_increment,
  logins_enabled        tinyint   not null,
  registrations_enabled tinyint   not null,
  bronto_enabled        tinyint   not null,

  modified_by           bigint,
  modified_date         timestamp null,

  constraint system_control_pkey primary key (id),
  constraint system_control_modified_by_fkey foreign key (modified_by) references users (id)
);