alter table wallets
  add reserved_bonus_balance bigint,
  add reserved_bonus_goal bigint;

update wallets set reserved_bonus_balance = 0, reserved_bonus_goal = 0;

alter table wallets
  modify reserved_bonus_balance bigint not null,
  modify reserved_bonus_goal bigint not null;
