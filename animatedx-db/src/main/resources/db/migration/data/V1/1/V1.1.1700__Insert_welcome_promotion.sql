insert into promotions (name, valid_from, valid_to, created_by, created_date, promotion_type, required_level)
values
  ('Welcome Promotion', timestamp('2014-04-21 23:59:59'), timestamp('2016-12-31 23:59:59'), 1, current_timestamp, 'SIGN_UP', 1);
