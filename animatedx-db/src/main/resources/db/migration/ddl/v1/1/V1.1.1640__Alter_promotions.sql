alter table promotions
add constraint promotions_required_level_fkey foreign key (required_level) references levels (level);
update promotions
set required_level = 1;
