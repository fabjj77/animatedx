alter table players_bonuses add column max_redemption_amount bigint;

alter table players_bonuses add column valid_from timestamp null;

alter table players_bonuses add column valid_to timestamp null;
