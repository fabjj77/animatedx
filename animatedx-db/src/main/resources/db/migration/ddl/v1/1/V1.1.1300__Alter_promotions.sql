alter table promotions add column promotion_type character varying(1000);

update promotions set promotion_type = "DEPOSIT";

alter table promotions modify column promotion_type character varying(1000) not null;

alter table promotions add column required_level bigint;

alter table promotions add column required_amount bigint;

alter table promotions add column required_repetition integer;

alter table promotions add column required_time_unit character varying(50);

alter table promotions add column required_recurring_time character varying(50);

