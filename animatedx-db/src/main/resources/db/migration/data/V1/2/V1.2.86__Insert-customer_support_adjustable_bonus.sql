insert into bonuses (id, name, valid_from, valid_to, type, netent_bonus_code, created_by, created_date, promotion_id,
                     amount, maximum_amount, quantity, percentage, currency, required_level)
values
  (127, 'Customer Support Adjustable Bonus', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'BONUS_MONEY', null, 1, current_timestamp, 1,
   0, null, null, null, 'EUR', 1);
