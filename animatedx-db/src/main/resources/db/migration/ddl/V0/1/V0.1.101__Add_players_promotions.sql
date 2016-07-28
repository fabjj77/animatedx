create table players_promotions
(
  player_id    bigint not null,
  promotion_id bigint not null,
  used_date    timestamp null,
  constraint players_promotions_pkey primary key (player_id, promotion_id),
  constraint players_promotions_players_id_fkey foreign key (player_id) references players (id),
  constraint players_promotions_promotions_id_fkey foreign key (promotion_id) references promotions (id)
);
