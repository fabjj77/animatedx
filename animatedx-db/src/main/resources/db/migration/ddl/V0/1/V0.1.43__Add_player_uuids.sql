create table player_uuids
(
  uuid         character varying(36) not null,
  player_id    bigint                not null,
  type         character varying(30) not null,
  created_date timestamp             not null,
  used_date    timestamp             null,
  data         character varying(50) null,
  constraint player_uuids_pkey primary key (uuid),
  constraint player_uuids_player_id_fkey foreign key (player_id) references players (id),
  constraint player_uuids_type_check check (type in ('PAYMENT', 'RESET_PASSWORD'))
);

-- Index: player_uuids_player_id_idx

-- DROP INDEX player_uuids_player_id_idx;

create index player_uuids_player_id_idx on player_uuids (player_id);
