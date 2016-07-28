update avatars
set night_background_url = 'night1.png', day_background_url = 'day1.png'
where avatar_base_type_id = 1 and level between 1 and 9;

update avatars
set night_background_url = 'night10.png', day_background_url = 'day10.png'
where avatar_base_type_id = 1 and level between 10 and 19;

update avatars
set night_background_url = 'night20.png', day_background_url = 'day20.png'
where avatar_base_type_id = 1 and level between 20 and 29;

update avatars
set night_background_url = 'night30.png', day_background_url = 'day30.png'
where avatar_base_type_id = 1 and level between 30 and 39;

update avatars
set night_background_url = 'night40.png', day_background_url = 'day40.png'
where avatar_base_type_id = 1 and level between 40 and 49;
