create table bonuses
(
  id                bigint                 not null auto_increment,
  name              character varying(200) not null,
  valid_from        timestamp              not null,
  valid_to          timestamp              not null,
  target_group      character varying(50)  not null,
  type              character varying(50)  not null,
  netent_bonus_code character varying(50),
  created_by        bigint                 not null,
  created_date      timestamp              not null,
  modified_by       bigint,
  modified_date     timestamp              null,
  promotion_id      bigint,
  amount            bigint,
  maximum_amount    bigint,
  quantity          integer,
  percentage        integer,
  currency          character varying(3)   not null,
  constraint bonuses_pkey primary key (id),
  constraint bonuses_created_by_fkey foreign key (created_by) references users (id),
  constraint bonuses_modified_by_fkey foreign key (modified_by) references users (id),
  constraint bonuses_promotion_id_fkey foreign key (promotion_id) references promotions (id)
);

create index bonuses_valid_from_idx on bonuses (valid_from);

create index bonuses_valid_to_idx on bonuses (valid_to);

create index bonuses_valid_from_valid_to_idx on bonuses (valid_from, valid_to);

create index bonuses_target_group_idx on bonuses (target_group);

create index bonuses_type_idx on bonuses (type);
