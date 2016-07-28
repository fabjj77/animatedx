create table bronto_lists
(
  id            bigint                 not null auto_increment,
  list_id       character varying(200) not null,
  name          character varying(100) not null,
  label         character varying(100) not null,
  active_count  bigint                 not null,
  status        character varying (50) not null,
  created_date  timestamp              not null,
  modified_by   bigint,
  modified_date timestamp              null,
  constraint bronto_lists_pkey primary key (id),
  constraint bronto_lists_modified_by_fkey foreign key (modified_by) references users (id)
);

create index bronto_lists_list_id_idx on bronto_lists (list_id)
