create table bronto_fields
(
  id            bigint                 not null auto_increment,
  field_id      character varying(200) not null,
  name          character varying(100) not null,
  label         character varying(100) not null,
  type          character varying(30)  not null,
  visibility    character varying(20)  not null,
  created_date  timestamp              not null,
  modified_by   bigint,
  modified_date timestamp              null,
  constraint bronto_fields_pkey primary key (id),
  constraint bronto_fields_modified_by_fkey foreign key (modified_by) references users (id)
);

create index bronto_fields_field_id_idx on bronto_fields (field_id)
