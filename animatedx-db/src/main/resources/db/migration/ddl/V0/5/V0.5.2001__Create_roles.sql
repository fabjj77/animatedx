-- Table: roles

-- DROP TABLE roles;

create table roles
(
  id            bigint                not null auto_increment,
  name          character varying(50) not null,
  status        character varying(50)  not null,
  created_by    bigint                not null,
  created_date  timestamp             not null,
  modified_by   bigint,
  modified_date timestamp             null,
  constraint roles_pkey primary key (id),
  constraint roles_created_by_fkey foreign key (created_by) references users (id),
  constraint roles_modified_by_fkey foreign key (modified_by) references users (id)
);

-- Index: roles_name_idx

-- DROP INDEX roles_name_idx;

create index roles_name_idx on roles (name);
