alter table promotions add column max_grant_numbers integer;

update promotions set max_grant_numbers = 1;

alter table promotions modify column max_grant_numbers integer not null;

alter table promotions add column allowed_countries character varying(2000);
