insert into bonuses (name, valid_from, valid_to, type, created_by, created_date, promotion_id,
                     amount, maximum_amount, quantity, percentage, currency, required_level,
                      allowed_countries, max_grant_numbers)
values
  ('Welcome Bonus: 200% Match bonus up to €200', timestamp('2014-04-22 23:59:59'), timestamp('2015-12-31 23:59:59'), 'DEPOSIT_BONUS', 1, current_timestamp, 1001,
   null, 20000, null, 200, 'EUR', 1,
   null, 1),

  ('€13 FREE Chip - Bonus money', timestamp('2014-04-22 23:59:59'), timestamp('2014-05-04 23:59:59'), 'BONUS_MONEY', 1, current_timestamp, 1001,
   1300, null, null, null, 'EUR', 1,
   'SWEDEN', 1),

  ('100% Match bonus up to 200€', timestamp('2014-04-22 23:59:59'), timestamp('2015-12-31 23:59:59'), 'DEPOSIT_BONUS', 1, current_timestamp, 1002,
   null, 20000, null, 100, 'EUR', 1,
   null, 1),

  ('5€ Free Chip', timestamp('2014-04-22 23:59:59'), timestamp('2015-12-31 23:59:59'), 'BONUS_MONEY', 1, current_timestamp, 1,
   500, null, null, null, 'EUR', 1,
   null, null),

  ('10€ Free Chip', timestamp('2014-04-22 23:59:59'), timestamp('2015-12-31 23:59:59'), 'BONUS_MONEY', 1, current_timestamp, 1,
   1000, null, null, null, 'EUR', 1,
   null, null),

  ('20€ Free Chip', timestamp('2014-04-22 23:59:59'), timestamp('2015-12-31 23:59:59'), 'BONUS_MONEY', 1, current_timestamp, 1,
   2000, null, null, null, 'EUR', 1,
   null, null),

  ('20 Free Spins', timestamp('2014-04-22 23:59:59'), timestamp('2015-12-31 23:59:59'), 'FREE_ROUND', 1, current_timestamp, 1,
   null, null, null, null, 'EUR', 1,
   null, null);
