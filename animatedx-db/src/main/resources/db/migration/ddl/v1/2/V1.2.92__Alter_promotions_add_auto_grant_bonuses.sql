alter table promotions add column auto_grant_bonuses tinyint;

update promotions
set auto_grant_bonuses = 0
where id <= 5;

update promotions
set auto_grant_bonuses = 1
where id = 1001;

update promotions
set auto_grant_bonuses = 0
where id = 1002;

alter table promotions modify column auto_grant_bonuses tinyint not null;
