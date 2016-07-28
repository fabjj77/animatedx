insert into promotions (id, name, valid_from, valid_to, created_by, created_date, promotion_type, required_level, auto_grant_bonuses)
values
  (4, 'Bling Conversion Promotion', timestamp('2014-05-13 19:29:26'), timestamp('2029-12-31 23:59:59'), 1, current_timestamp, 'SIGN_UP', 1, 0);

insert into bonuses (id, name, valid_from, valid_to, type, netent_bonus_code, created_by, created_date, promotion_id,
                     amount, maximum_amount, quantity, percentage, currency, required_level, bonus_code, auto_grant_next)
values
  (128, 'Bling To Bonus Money Conversion Bonus', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'BONUS_MONEY', null, 1, current_timestamp, 4,
   0, null, null, null, 'EUR', 1, 'BLING_TO_BONUS_MONEY_CONVERSION', 0);
