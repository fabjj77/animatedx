-- Table: netent_transactions

-- DROP TABLE netent_transactions;

create table netent_transactions
(
  id              bigint                 not null auto_increment,
  player_id       bigint                 not null,
  session_id      character varying(120),
  game_id         character varying(120),
  game_round_ref  character varying(120) null,
  money_deposit   bigint                 not null,
  money_withdraw  bigint                 not null,
  bonus_deposit   bigint                 not null,
  bonus_withdraw  bigint                 not null,
  money_balance   bigint                 not null,
  bonus_balance   bigint                 not null,
  transaction_ref character varying(120) not null,
  currency        character varying(3)   not null,
  rollback_date   timestamp              null,
  reason          character varying(120),
  created_date    timestamp              not null,
  constraint netent_transactions_pkey primary key (id),
  constraint netent_transactions_user_id_fkey foreign key (player_id) references players (id)
);
