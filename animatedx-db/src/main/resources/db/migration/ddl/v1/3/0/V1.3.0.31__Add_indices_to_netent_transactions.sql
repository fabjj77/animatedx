alter table netent_transactions add index if not exists netent_transactions_transaction_ref_idx (transaction_ref);

alter table netent_transactions add index if not exists netent_transactions_player_gameround_idx (player_id, game_round_ref);
