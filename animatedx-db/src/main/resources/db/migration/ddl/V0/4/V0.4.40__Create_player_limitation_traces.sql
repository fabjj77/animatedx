create table player_limitation_traces
(
  id                       bigint 					      not null auto_increment,
  player_id                bigint                 not null,
  user_id					         bigint,
  loss_limit               bigint,
  loss_time_unit           character varying(20),
  bet_limit                bigint,
  bet_time_unit            character varying(20),
  session_length           integer,
  created_date             timestamp               not null,
  apply_date				       timestamp               not null,
  status					         character varying(20)	 not null,
  constraint player_limitation_traces_pkey primary key (id),
  constraint player_limitation_traces_created_by_player_fkey foreign key (player_id) references players (id),
  constraint player_limitation_traces_created_by_user_fkey foreign key (user_id) references users (id)
);
