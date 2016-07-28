alter table netent_transactions add column player_bonus_id bigint;

alter table netent_transactions add constraint netent_transactions_player_bonus_id_fkey foreign key (player_bonus_id) references players_bonuses (id);
