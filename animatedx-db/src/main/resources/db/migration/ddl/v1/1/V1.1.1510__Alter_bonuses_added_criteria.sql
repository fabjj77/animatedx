alter table bonuses add column required_level bigint;

alter table bonuses add column required_amount bigint;

alter table bonuses add column required_repetition integer;

alter table bonuses add column required_time_unit character varying(50);

alter table bonuses add column required_recurring_time character varying(50);

alter table bonuses add column allowed_countries character varying(2000);
