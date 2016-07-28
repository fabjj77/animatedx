alter table items
  drop foreign key items_promotion_id_fkey,
  drop promotion_id,
  add bonus_id bigint not null,
  add constraint items_bonus_id_fkey foreign key (bonus_id) references bonuses (id)

