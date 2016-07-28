-- Table: providers

-- DROP TABLE providers;

create table providers
(
  id            integer                not null auto_increment,
  name          character varying(100) not null,
  status        character varying(50)  not null,
  created_by    bigint                 not null,
  created_date  timestamp              not null,
  modified_by   bigint,
  modified_date timestamp              null,
  constraint providers_pkey primary key (id),
  constraint providers_created_by_fkey foreign key (created_by) references users (id),
  constraint providers_modified_by_fkey foreign key (modified_by) references users (id)
);

-- Index: providers_active_idx

-- DROP INDEX providers_active_idx;

create index providers_active_idx on providers (status);
