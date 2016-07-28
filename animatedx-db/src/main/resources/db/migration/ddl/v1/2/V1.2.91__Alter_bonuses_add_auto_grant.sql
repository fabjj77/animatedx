alter table bonuses add column auto_grant_next tinyint;

update bonuses
set auto_grant_next = 0
where id <= 15;

update bonuses
set auto_grant_next = 1
where id >= 16 and id <= 126;

update bonuses
set auto_grant_next = 0
where id >= 127;

alter table bonuses modify column auto_grant_next tinyint not null;
