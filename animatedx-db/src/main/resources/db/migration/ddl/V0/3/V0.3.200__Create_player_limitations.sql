create table player_limitations
(
  id             bigint                not null auto_increment,
  player_id      bigint                not null,
  loss_limit     bigint                not null,
  loss_time_unit character varying(20) not null,
  bet_limit      bigint                not null,
  bet_time_unit  character varying(20) not null,
  session_length integer               not null,
  modified_date  timestamp             null,
  constraint player_limitations_pkey primary key (id),
  constraint player_limitations_player_id_fkey foreign key (player_id) references players (id)
);
