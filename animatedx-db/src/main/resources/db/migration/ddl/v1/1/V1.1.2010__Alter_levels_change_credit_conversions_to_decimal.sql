alter table levels modify column money_credit_rate decimal(10, 2) not null, modify column bonus_credit_rate decimal(10, 2) not null;

update levels
set money_credit_rate = 0.50
where level between 1 and 29;

update levels
set bonus_credit_rate = 1.50
where level between 1 and 29;
