-- Table: wallets

-- DROP TABLE wallets;

create table wallets
(
  id                        bigint not null auto_increment,
  player_id                 bigint not null,
  money_balance             bigint not null,
  bonus_balance             bigint not null,
  reserved_balance          bigint not null,
  credits_balance           bigint not null,
  turnover_cashback         bigint not null,
  turnover_bonus            bigint not null,
  turnover_bonus_goal       bigint not null,
  turnover_money            bigint not null,
  turnover_progress         bigint not null,
  turnover_weekly           bigint not null,
  turnover_monthly          bigint not null,
  accumulated_daily_loss    bigint not null,
  accumulated_weekly_loss   bigint not null,
  accumulated_monthly_loss  bigint not null,
  accumulated_daily_bet     bigint not null,
  accumulated_weekly_bet    bigint not null,
  accumulated_monthly_bet   bigint not null,
  constraint wallets_pkey   primary key (id),
  constraint wallets_user_id_fkey foreign key (player_id) references players (id)
);

-- Index: wallets_money_balance_idx

-- DROP INDEX wallets_money_balance_idx;

create index wallets_money_balance_idx on wallets (money_balance);

-- Index: wallets_bonus_balance_idx

-- DROP INDEX wallets_bonus_balance_idx;

create index wallets_bonus_balance_idx on wallets (bonus_balance);

-- Index: wallets_reserved_balance_idx

-- DROP INDEX wallets_reserved_balance_idx;

create index wallets_reserved_balance_idx on wallets (reserved_balance);

-- Index: wallets_credits_balance_idx

-- DROP INDEX wallets_credits_balance_idx;

create index wallets_credits_balance_idx on wallets (credits_balance);

-- Index: wallets_turnover_cashback_idx

-- DROP INDEX wallets_turnover_cashback_idx;

create index wallets_turnover_cashback_idx on wallets (turnover_cashback);

-- Index: wallets_turnover_bonus_idx

-- DROP INDEX wallets_turnover_bonus_idx;

create index wallets_turnover_bonus_idx on wallets (turnover_bonus);

-- Index: wallets_turnover_bonus_goal_idx

-- DROP INDEX wallets_turnover_bonus_goal_idx;

create index wallets_turnover_bonus_goal_idx on wallets (turnover_bonus_goal);

-- Index: wallets_turnover_money_idx

-- DROP INDEX wallets_turnover_money_idx;

create index wallets_turnover_money_idx on wallets (turnover_money);

-- Index: wallets_turnover_progress_idx

-- DROP INDEX wallets_turnover_progress_idx;

create index wallets_turnover_progress_idx on wallets (turnover_progress);
