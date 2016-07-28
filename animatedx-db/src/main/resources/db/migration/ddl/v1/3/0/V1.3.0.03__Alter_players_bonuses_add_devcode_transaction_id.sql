alter table players_bonuses add column devcode_transaction_id bigint;

alter table players_bonuses add constraint players_bonuses_devcode_transaction_id_fkey foreign key (devcode_transaction_id) references devcode_transactions (id);

