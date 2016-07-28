-- Table: users

-- DROP TABLE users;

create table users
(
  id            bigint                not null auto_increment,
  first_name    character varying(30) not null,
  last_name     character varying(30) not null,
  email_address character varying(50) not null,
  password      character varying(70) not null,
  nickname      character varying(30) not null,
  type          character varying(7)  not null,
  phone_number  character varying(30) not null,
  status        character varying(50) not null,
  created_by    bigint                not null,
  created_date  timestamp             not null,
  modified_by   bigint,
  modified_date timestamp             null,
  constraint users_pkey primary key (id),
  constraint users_created_by_fkey foreign key (created_by) references users (id),
  constraint users_modified_by_fkey foreign key (modified_by) references users (id),
  constraint users_email_address_key unique (email_address),
  constraint users_nickname_key unique (nickname),
  constraint users_type_check check (type in ('SUPPORT', 'ADMIN'))
);

-- Index: users_email_address_active_idx

-- DROP INDEX users_email_address_active_idx;

create index users_email_address_active_idx on users (email_address, status);
