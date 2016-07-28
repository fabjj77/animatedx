create table player_comments
(
  id            bigint                  not null auto_increment,
  player_id     bigint                  not null,
  created_date  timestamp               not null,
  comment       character varying(2000) not null,
  constraint player_comments_pkey primary key (id),
  constraint player_comments_player_id_fkey foreign key (player_id) references players (id)
);
