alter table bonuses
add constraint bonuses_required_level_fkey foreign key (required_level) references levels (level);
