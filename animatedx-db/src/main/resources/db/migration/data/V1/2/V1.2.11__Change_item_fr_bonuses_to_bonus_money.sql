update bonuses
set name = 'Level 2 bonus: €5 Free', type = 'BONUS_MONEY', amount = 500, modified_by = 1, modified_date = current_timestamp
where id = 1;

update bonuses
set name = 'Level 5 bonus: €10 Free', type = 'BONUS_MONEY', amount = 1000, modified_by = 1, modified_date = current_timestamp
where id = 2;

update bonuses
set name = 'Level 12 bonus:: €20 Free', type = 'BONUS_MONEY', amount = 2000, modified_by = 1, modified_date = current_timestamp
where id = 4;

update bonuses
set name = 'Level 22 bonus:: €30 Free', type = 'BONUS_MONEY', amount = 3000, modified_by = 1, modified_date = current_timestamp
where id = 7;
