-- Table: user_roles

-- DROP TABLE user_roles;

create table user_roles
(
  id      bigint not null auto_increment,
  user_id bigint not null,
  role_id bigint not null,
  constraint user_roles_pkey primary key (id),
  constraint user_roles_user_id_fkey foreign key (user_id) references users (id),
  constraint user_roles_role_id_fkey foreign key (role_id) references roles (id)
);

-- Index: user_roles_user_id_idx

-- DROP INDEX user_roles_user_id_idx;

create index user_roles_user_id_idx on user_roles (user_id);

-- Index: user_roles_role_id_idx

-- DROP INDEX user_roles_role_id_idx;

create index user_roles_role_id_idx on user_roles (role_id);
