create table promotions
(
  id                bigint                not null auto_increment,
  valid_from        timestamp             not null,
  valid_to          timestamp             not null,
  target_group      character varying(30) not null,
  type              character varying(30) not null,
  netent_bonus_code character varying(30),
  created_by        bigint                not null,
  created_date      timestamp             not null,
  modified_by       bigint,
  modified_date     timestamp             null,
  constraint promotions_pkey primary key (id),
  constraint promotions_created_by_fkey foreign key (created_by) references users (id),
  constraint promotions_modified_by_fkey foreign key (modified_by) references users (id)
);

create index promotions_valid_from_idx on promotions (valid_from);

create index promotions_valid_to_idx on promotions (valid_to);

create index promotions_valid_from_valid_to_idx on promotions (valid_from, valid_to);

create index promotions_target_group_idx on promotions (target_group);

create index promotions_type_idx on promotions (type);
