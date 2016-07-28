create table white_listed_ip_addresses
(
  id            bigint    not null auto_increment,
  from_ip       bigint    not null,
  to_ip         bigint    not null,
  created_date  timestamp not null,
  modified_date timestamp null,
  deleted       tinyint   not null,

  constraint white_listed_ip_addresses_pkey primary key (id)
);
