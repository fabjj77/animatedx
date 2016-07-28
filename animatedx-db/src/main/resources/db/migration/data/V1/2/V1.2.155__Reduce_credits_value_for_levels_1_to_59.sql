update levels
set bonus_credit_rate = 0.75
where level between 1 and 29;

update levels
set bonus_credit_rate = 3.00
where level between 30 and 59;
