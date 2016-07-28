-- Table: avatar_base_types

-- DROP TABLE avatar_base_types;

create table avatar_base_types
(
  id            integer               not null auto_increment,
  name          character varying(50) not null,
  status        character varying(50)  not null,
  created_by    bigint                not null,
  created_date  timestamp             not null,
  modified_by   bigint,
  modified_date timestamp             null,
  constraint avatar_base_types_pkey primary key (id),
  constraint avatar_base_types_created_by_fkey foreign key (created_by) references users (id),
  constraint avatar_base_types_modified_by_fkey foreign key (modified_by) references users (id)
);

-- Index: avatar_base_types_active_idx

-- DROP INDEX avatar_base_types_active_idx;

create index avatar_base_types_active_idx on avatar_base_types (status);

-- Index: avatar_base_types_name_idx

-- DROP INDEX avatar_base_types_name_idx;

create index avatar_base_types_name_idx on avatar_base_types (name);
