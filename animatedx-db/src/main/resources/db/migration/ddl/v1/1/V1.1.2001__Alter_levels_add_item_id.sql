alter table levels add column item_id bigint;
alter table levels add constraint levels_item_id_fkey foreign key (item_id) references items (id);
