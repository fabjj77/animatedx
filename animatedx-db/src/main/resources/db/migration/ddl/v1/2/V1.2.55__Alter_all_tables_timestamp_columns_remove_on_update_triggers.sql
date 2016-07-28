alter table affiliates
modify created_date timestamp not null default current_timestamp;

alter table avatar_base_types
modify created_date timestamp not null default current_timestamp;

alter table avatar_histories
modify created_date timestamp not null default current_timestamp;

alter table avatars
modify created_date timestamp not null default current_timestamp;

alter table bonuses
modify valid_from timestamp not null default 0,
modify valid_to timestamp not null default 0,
modify created_date timestamp not null default current_timestamp;

alter table bronto_contacts
modify created_date timestamp not null default current_timestamp;

alter table bronto_fields
modify created_date timestamp not null default current_timestamp;

alter table bronto_lists
modify created_date timestamp not null default current_timestamp;

alter table chapter_translations
modify created_date timestamp not null default current_timestamp;

alter table credit_transactions
modify created_date timestamp not null default current_timestamp;

alter table bronto_contacts
modify created_date timestamp not null default current_timestamp;

alter table game_sessions
modify start_date timestamp not null default current_timestamp;

alter table items
modify created_date timestamp not null default current_timestamp;

alter table levels
modify created_date timestamp not null default current_timestamp;

alter table netent_games
modify created_date timestamp not null default current_timestamp;

alter table netent_transactions
modify created_date timestamp not null default current_timestamp;

alter table payment_transactions
modify created_date timestamp not null default current_timestamp;

alter table player_activities
modify activity_date timestamp not null default current_timestamp;

alter table player_comments
modify created_date timestamp not null default current_timestamp;

alter table player_limitation_lockouts
modify start_date timestamp not null default current_timestamp,
modify end_date timestamp null;

alter table player_limitation_traces
modify created_date timestamp not null default current_timestamp,
modify apply_date timestamp null;

alter table players
modify created_date timestamp not null default current_timestamp,
modify block_end_date timestamp null;

alter table players_bonuses
modify created_date timestamp not null default current_timestamp;

alter table player_uuids
modify created_date timestamp not null default current_timestamp;

alter table promotions
modify valid_from timestamp not null default 0,
modify valid_to timestamp not null default 0,
modify created_date timestamp not null default current_timestamp;

alter table providers
modify created_date timestamp not null default current_timestamp;

alter table roles
modify created_date timestamp not null default current_timestamp;

alter table user_activities
modify activity_date timestamp not null default current_timestamp;

alter table users
modify created_date timestamp not null default current_timestamp;
