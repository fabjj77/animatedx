alter table devcode_transactions
modify created_date timestamp not null default current_timestamp;

alter table white_listed_players
modify created_date timestamp not null default current_timestamp;

alter table white_listed_ip_addresses
modify created_date timestamp not null default current_timestamp;
