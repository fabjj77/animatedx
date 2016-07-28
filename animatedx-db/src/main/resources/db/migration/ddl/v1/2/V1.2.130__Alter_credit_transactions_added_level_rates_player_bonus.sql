alter table credit_transactions add column level bigint;

alter table credit_transactions add column money_credit_rate decimal(10, 2);

alter table credit_transactions add column bonus_credit_rate decimal(10, 2);

alter table credit_transactions add column player_bonus_id bigint;

alter table credit_transactions add constraint credit_transactions_player_bonus_id_fkey foreign key (player_bonus_id) references players_bonuses (id);

update credit_transactions ct
set ct.level = (
  select p.level
  from players p
  where p.id = ct.player_id);

alter table credit_transactions modify column level bigint not null;

