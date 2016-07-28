alter table payment_transactions add column level bigint;

update payment_transactions pt
set pt.level = (
  select p.level
  from players p
  where p.id = pt.player_id);

alter table payment_transactions modify column level bigint not null;


alter table players_bonuses add column level bigint;

update players_bonuses pb
set pb.level = (
  select p.level
  from players p
  where p.id = pb.player_id);

alter table players_bonuses modify column level bigint not null;


alter table player_activities add column level bigint;

update player_activities pa
set pa.level = (
  select p.level
  from players p
  where p.id = pa.player_id);

alter table player_activities modify column level bigint not null;
