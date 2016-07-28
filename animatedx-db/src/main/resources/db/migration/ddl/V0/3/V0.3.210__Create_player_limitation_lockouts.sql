create table player_limitation_lockouts
(
  id         bigint                not null auto_increment,
  player_id  bigint                not null,
  block_type character varying(80) not null,
  start_date timestamp             not null,
  end_date   timestamp             not null,
  constraint player_limitation_lockouts_pkey primary key (id),
  constraint player_limitation_lockouts_player_id_fkey foreign key (player_id) references players (id)
);
