alter table wallets add column bonus_conversion_progress bigint;

update wallets
set bonus_conversion_progress = turnover_bonus;

alter table wallets modify column bonus_conversion_progress bigint not null;

alter table wallets
change column turnover_cashback accumulated_cashback bigint not null,
change column turnover_progress level_progress bigint not null,
change column turnover_bonus_goal bonus_conversion_goal bigint not null,
change column turnover_money accumulated_money_turnover bigint not null,
change column turnover_bonus accumulated_bonus_turnover bigint not null,
change column turnover_weekly accumulated_weekly_turnover bigint not null,
change column turnover_monthly accumulated_monthly_turnover bigint not null,
change column turnover_bonus_monthly accumulated_monthly_bonus_turnover bigint not null,
change column reserved_bonus_goal reserved_bonus_progression_goal bigint not null;

