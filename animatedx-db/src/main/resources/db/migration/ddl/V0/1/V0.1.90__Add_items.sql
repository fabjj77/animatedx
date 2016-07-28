create table items
(
  id            bigint                 not null auto_increment,
  url           character varying(120) not null,
  status        character varying(30)  not null,
  item_type     character varying(30)  not null,
  amount        integer                not null,
  level         bigint                 not null,
  created_by    bigint                 not null,
  created_date  timestamp              not null,
  modified_by   bigint,
  modified_date timestamp              null,
  constraint items_pkey primary key (id),
  constraint items_level_fkey foreign key (level) references levels (level),
  constraint items_created_by_fkey foreign key (created_by) references users (id),
  constraint items_modified_by_fkey foreign key (modified_by) references users (id)
);
