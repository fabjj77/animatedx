create table netent_games
(
  game_id       character varying(100) not null,
  full_name     character varying(200) not null,
  name          character varying(100) not null,
  category      character varying(100) not null,
  slug          character varying(50),
  featured      tinyint                not null,
  status        character varying(30)  not null,
  created_by    bigint                 not null,
  created_date  timestamp              not null,
  modified_by   bigint,
  modified_date timestamp              null,
  constraint netent_games_pkey primary key (game_id),
  constraint netent_games_created_by_fkey foreign key (created_by) references users (id),
  constraint netent_games_modified_by_fkey foreign key (modified_by) references users (id)
);
