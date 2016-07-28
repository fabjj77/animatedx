create table players_register_tracks
(
  id        bigint                  not null auto_increment,
  player_id bigint                  not null,
  campaign  character varying(200)  not null,
  source    character varying(200)  not null,
  medium    character varying(200)  not null,
  content   character varying(1000) not null,
  version   character varying(1000) not null,
  constraint players_register_tracks_pkey primary key (id),
  constraint players_register_tracks_fkey foreign key (player_id) references players (id)
);
