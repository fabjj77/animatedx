create table affiliates
(
  id            bigint                not null auto_increment,
  name          character varying(100) not null,
  status        character varying(50)   not null,
  created_by    bigint                 not null,
  created_date  timestamp              not null,
  modified_by   bigint,
  modified_date timestamp              null,
  constraint affiliates_pkey primary key (id),
  constraint affiliates_created_by_fkey foreign key (created_by) references users (id),
  constraint affiliates_modified_by_fkey foreign key (modified_by) references users (id)
);
