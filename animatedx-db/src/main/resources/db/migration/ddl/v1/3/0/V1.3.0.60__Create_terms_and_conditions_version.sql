create table terms_and_conditions_versions
(
  id           bigint(20)  not null auto_increment,
  version      varchar(10) not null,
  created_date timestamp   not null default current_timestamp,

  constraint terms_and_conditions_versions_pkey primary key (id)
);
