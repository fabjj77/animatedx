alter table items
  add promotion_id bigint not null,
  add constraint items_promotion_id_fkey foreign key (promotion_id) references promotions (id)
