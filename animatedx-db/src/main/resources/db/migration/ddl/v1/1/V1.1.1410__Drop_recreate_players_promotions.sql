drop table players_promotions;

create table players_promotions
(
  id              bigint    not null auto_increment,
  player_id       bigint    not null,
  promotion_id    bigint    not null,
  activation_date timestamp null,
  constraint players_promotions_pkey primary key (id),
  constraint players_promotions_players_id_fkey foreign key (player_id) references players (id),
  constraint players_promotions_promotions_id_fkey foreign key (promotion_id) references promotions (id)
);
