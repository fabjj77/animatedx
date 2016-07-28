create table players_items
(
  player_id  bigint                not null,
  item_id    bigint                not null,
  item_state character varying(30) not null,
  used_date  timestamp             null,
  constraint players_items_pkey primary key (player_id, item_id),
  constraint players_items_players_id_fkey foreign key (player_id) references players (id),
  constraint players_items_items_id_fkey foreign key (item_id) references items (id)
);
