insert into promotions (id, valid_from, valid_to, target_group, name, created_by, created_date)
values
  (1, timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'Customer Support Promotion', 1, current_timestamp),
  (2, timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'Citizen Level Item Promotion', 1, current_timestamp);
