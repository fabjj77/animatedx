create table white_listed_players
(
  id            bigint    not null auto_increment,
  player_id     bigint    not null,
  created_date  timestamp not null,
  modified_date timestamp null,
  deleted       tinyint   not null,

  constraint white_listed_players_pkey primary key (id),
  constraint white_listed_players_player_id_fkey foreign key (player_id) references players (id)
);
