drop table players_bonuses;

create table players_bonuses
(
  id              bigint    not null auto_increment,
  player_id       bigint    not null,
  bonus_id        bigint    not null,
  created_date    timestamp not null,
  activation_date timestamp null,
  used_date       timestamp null,
  used_amount     bigint,
  used_quantity   integer,
  constraint players_bonuses_pkey primary key (id),
  constraint players_bonuses_players_id_fkey foreign key (player_id) references players (id),
  constraint players_bonuses_bonuses_id_fkey foreign key (bonus_id) references bonuses (id)
);
