alter table player_comments
add column user_id bigint not null,
add constraint player_comments_user_id_fkey foreign key (user_id) references users (id);
