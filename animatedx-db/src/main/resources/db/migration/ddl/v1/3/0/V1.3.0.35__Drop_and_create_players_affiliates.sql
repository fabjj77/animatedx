drop table players_affiliates;

create table players_affiliates
(
  id            bigint                not null auto_increment,
  player_id     bigint                not null,
  affiliate_id  character varying(50) not null,
  btag          character varying(50) not null,
  created_date  timestamp             not null default current_timestamp,
  reported_date timestamp             null,
  constraint players_affiliates_pkey primary key (id),
  constraint players_affiliates_player_id_fkey foreign key (player_id) references players (id)
);

create index players_affiliates_affiliate_id_idx on players_affiliates (affiliate_id);

create index players_affiliates_created_date_idx on players_affiliates (created_date);
