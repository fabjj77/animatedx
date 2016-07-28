create table credit_conversions
(
  id           bigint      not null auto_increment,
  player_id    bigint      not null,
  credit       bigint      not null,
  bonus_money  bigint,
  real_money   bigint,
  currency     varchar(30) not null,
  created_date timestamp   not null,

  constraint credit_conversions_pkey primary key (id),
  constraint credit_conversions_player_id_fkey foreign key (player_id) references players (id)
);
