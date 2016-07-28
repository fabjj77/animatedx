alter table players_bonuses add column current_balance bigint;

alter table players_bonuses add column bonus_conversion_goal bigint;

alter table players_bonuses add column bonus_conversion_progress bigint;

alter table players_bonuses add column status character varying(100);

alter table players_bonuses add column completion_date timestamp null;
