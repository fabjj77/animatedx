alter table player_activities add index if not exists player_activities_player_type_idx (player_id, activity_type);

alter table players_bonuses  add index if not exists players_bonuses_player_status_idx (player_id, status);

alter table payment_transactions add index if not exists payment_transactions_player_event_status_created_idx (player_id, event_code, status, created_date);

alter table netent_transactions add index if not exists netent_transactions_player_created_idx (player_id, created_date asc);
