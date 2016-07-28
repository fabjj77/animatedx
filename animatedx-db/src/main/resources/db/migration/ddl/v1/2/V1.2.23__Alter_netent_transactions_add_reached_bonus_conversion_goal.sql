alter table netent_transactions add column reached_bonus_conversion_goal tinyint;

update netent_transactions
set reached_bonus_conversion_goal = 0;

alter table netent_transactions modify column reached_bonus_conversion_goal tinyint not null;
