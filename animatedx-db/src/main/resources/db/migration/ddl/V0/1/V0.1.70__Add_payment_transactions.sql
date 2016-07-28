-- Table: payment_transactions

-- DROP TABLE payment_transactions;

create table payment_transactions
(
  id                       bigint                 not null auto_increment,
  player_id                bigint                 not null,
  uuid                     character varying(36),
  withdraw_reference       character varying(120),
  payment_method           character varying(80),
  amount                   bigint                 not null,
  currency                 character varying(3)   not null,
  provider_reference       character varying(120) not null,
  original_reference       character varying(120),
  status                   character varying(50)  not null,
  authorization_operations character varying(30),
  reason                   character varying(120),
  event_code               character varying(120) not null,
  created_date             timestamp              not null,
  event_date               timestamp              null,
  process_date             timestamp              null,
  withdraw_confirm_date    timestamp              null,
  provider_id              integer                not null,
  constraint payment_transactions_pkey primary key (id),
  constraint payment_transactions_player_id_fkey foreign key (player_id) references players (id),
  constraint payment_transactions_uuid_fkey foreign key (uuid) references player_uuids (uuid),
  constraint payment_transactions_provider_id_fkey foreign key (provider_id) references providers (id)
);
