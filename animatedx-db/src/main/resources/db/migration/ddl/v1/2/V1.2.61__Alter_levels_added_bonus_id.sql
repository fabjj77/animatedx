alter table levels drop column deposit_bonus_percentage;
alter table levels add column bonus_id bigint;
alter table levels add constraint levels_bonus_id_fkey foreign key (bonus_id) references bonuses (id);