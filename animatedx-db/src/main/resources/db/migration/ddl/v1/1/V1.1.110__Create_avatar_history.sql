create table avatar_histories
(
  id            bigint                not null auto_increment,
  player_id     bigint                not null,
  avatar_id     bigint                not null,
  created_date  timestamp             not null,
  constraint avatar_histories_pkey primary key (id),
  constraint avatar_histories_player_id_fkey foreign key (player_id) references players (id),
  constraint avatar_histories_avatar_id_fkey foreign key (avatar_id) references avatars (id)
);
