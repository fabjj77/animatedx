create table players_affiliates
(
  player_id  bigint                not null,
  affiliate_id    bigint                not null,
  constraint players_affiliates_pkey primary key (player_id, affiliate_id),
  constraint players_affiliates_players_id_fkey foreign key (player_id) references players (id),
  constraint players_affiliates_affiliates_id_fkey foreign key (affiliate_id) references affiliates (id)
);
