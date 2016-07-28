create table players_bonuses
(
  player_id    bigint    not null,
  bonus_id     bigint    not null,
  created_date timestamp not null,
  used_date    timestamp null,
  constraint players_bonuses_pkey primary key (player_id, bonus_id),
  constraint players_bonuses_players_id_fkey foreign key (player_id) references players (id),
  constraint players_bonuses_bonuses_id_fkey foreign key (bonus_id) references bonuses (id)
);
