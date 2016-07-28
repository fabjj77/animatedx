create table players_session_registries
(
  id           bigint                not null auto_increment,
  player_id    bigint                not null,
  session_id   character varying(50) not null,
  last_request timestamp             not null,
  expired      tinyint               not null,
  active       tinyint               not null,

  constraint players_session_registries_pkey primary key (id),
  constraint players_session_registries_player_id_fkey foreign key (player_id) references players (id)
);
