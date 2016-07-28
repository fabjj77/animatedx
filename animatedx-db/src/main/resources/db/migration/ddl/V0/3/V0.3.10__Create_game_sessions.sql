create table game_sessions
(
  id             bigint    not null auto_increment,
  player_id      bigint    not null,
  session_id     character varying(120),
  start_date     timestamp not null,
  end_date       timestamp null,
  session_length integer   not null,
  constraint game_sessions_pkey primary key (id),
  constraint game_sessions_player_id_fkey foreign key (player_id) references players (id)
);
