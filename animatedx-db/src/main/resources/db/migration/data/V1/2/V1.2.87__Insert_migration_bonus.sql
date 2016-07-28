insert into promotions (id, name, valid_from, valid_to, created_by, created_date, promotion_type, required_level)
values
  (5, 'System Migration To Separate Bonuses Promotion', timestamp('2014-05-13 19:29:26'), timestamp('2014-12-31 23:59:59'), 1, current_timestamp, 'MANUAL', 1);

insert into bonuses (id, name, valid_from, valid_to, type, netent_bonus_code, created_by, created_date, promotion_id,
                     amount, maximum_amount, quantity, percentage, currency, required_level, max_grant_numbers)
values
  (129, 'System Migration To Separate Bonuses', timestamp('2013-12-31 23:59:59'), timestamp('2014-12-31 23:59:59'), 'BONUS_MONEY', null, 1, current_timestamp, 5,
   0, null, null, null, 'EUR', 1, 1);

insert into players_promotions (promotion_id, activation_date, player_id)
  select 5 , current_timestamp, id
  from players;

insert into players_bonuses (bonus_id, created_date, used_date, status, player_id, used_amount, current_balance, bonus_conversion_progress, bonus_conversion_goal)
  select '129', current_timestamp, current_timestamp, 'ACTIVE', player_id, bonus_balance, bonus_balance, bonus_conversion_progress, bonus_conversion_goal
  from wallets
  where bonus_balance > 0;

insert into players_bonuses (bonus_id, created_date, used_date, status, player_id, used_amount, current_balance, bonus_conversion_progress, bonus_conversion_goal)
  select '129', current_timestamp, current_timestamp, 'RESERVED', player_id, reserved_bonus_balance, reserved_bonus_balance, bonus_conversion_progress,
    reserved_bonus_progression_goal
  from wallets
  where reserved_bonus_balance > 0;
