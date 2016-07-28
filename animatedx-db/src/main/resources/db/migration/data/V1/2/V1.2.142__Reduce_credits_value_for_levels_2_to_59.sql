update levels
set money_credit_rate = 0.00
where level < 60;

update levels
set bonus_credit_rate = 0.25
where level between 2 and 29;

update levels
set bonus_credit_rate = 1.00
where level between 30 and 59;
