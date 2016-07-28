delete from bonuses;
insert into bonuses (id, name, valid_from, valid_to, target_group, type, netent_bonus_code, created_by, created_date, promotion_id, amount, maximum_amount, quantity,
                     percentage, currency, required_level)
values
  (1, 'Level 2 bonus: 40 Starburst FR', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'FREE_ROUND', 'net_ent_bonus_code', 1,
   current_timestamp, 2, null, null, null, null, 'EUR', 2),
  (2, 'Level 5 bonus: 30 Gonzo FR', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'FREE_ROUND', 'net_ent_bonus_code', 1, current_timestamp,
   2, null, null, null, null, 'EUR', 5),
  (3, 'Level 8 bonus: €15 Free', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'BONUS_MONEY', null, 1, current_timestamp, 2, 1500, null,
   null, null, 'EUR', 8),

  (4, 'Level 12 bonus: 40 Starburst FR', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'FREE_ROUND', 'net_ent_bonus_code', 1,
   current_timestamp, 2, null, null, null, null, 'EUR', 12),
  (5, 'Level 15 bonus: 150% dep Bonus', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'DEPOSIT_BONUS', null, 1, current_timestamp, 2, null,
   120000, null, 150, 'EUR', 15),
  (6, 'Level 18 bonus: 25€ Free', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'BONUS_MONEY', null, 1, current_timestamp, 2, 2500, null,
   null, null, 'EUR', 18),

  (7, 'Level 22 bonus: 30 New Game FR', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'FREE_ROUND', 'net_ent_bonus_code', 1,
   current_timestamp, 2, null, null, null, null, 'EUR', 22),
  (8, 'Level 25 bonus: 150% dep Bonus', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'DEPOSIT_BONUS', null, 1, current_timestamp, 2, null,
   120000, null, 150, 'EUR', 25),
  (9, 'Level 28 bonus: 35€ Free', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'BONUS_MONEY', null, 1, current_timestamp, 2, 3500, null,
   null, null, 'EUR', 28),

  (10, 'Level 32 bonus: 150% dep Bonus', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'DEPOSIT_BONUS', null, 1, current_timestamp, 2, null,
   120000, null, 150, 'EUR', 32),
  (11, 'Level 35 bonus: 25€ Free', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'BONUS_MONEY', null, 1, current_timestamp, 2, 2500, null,
   null, null, 'EUR', 35),
  (12, 'Level 38 bonus: 50€ Free', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'BONUS_MONEY', null, 1, current_timestamp, 2, 5000, null,
   null, null, 'EUR', 38),

  (13, 'Level 42 bonus: 75€ Free', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'BONUS_MONEY', null, 1, current_timestamp, 2, 7500, null,
   null, null, 'EUR', 42),
  (14, 'Level 45 bonus: 200% dep Bonus', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'DEPOSIT_BONUS', null, 1, current_timestamp, 2, null,
   120000, null, 200, 'EUR', 45),
  (15, 'Level 48 bonus: 100€ Free', timestamp('2013-12-31 23:59:59'), timestamp('2029-12-31 23:59:59'), 'ALL', 'BONUS_MONEY', null, 1, current_timestamp, 2, 10000, null,
   null, null, 'EUR', 48);
