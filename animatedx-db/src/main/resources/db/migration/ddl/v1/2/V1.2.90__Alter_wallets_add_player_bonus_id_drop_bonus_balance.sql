alter table wallets drop column bonus_balance;

alter table wallets drop column bonus_conversion_goal;

alter table wallets drop column bonus_conversion_progress;

alter table wallets drop column reserved_bonus_balance;

alter table wallets drop column reserved_bonus_progression_goal;

alter table wallets add column player_bonus_id bigint;

alter table wallets add constraint wallets_player_bonus_id_fkey foreign key (player_bonus_id) references players_bonuses (id);
