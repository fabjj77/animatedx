create table netent_game_infos
(
  id                  bigint                 not null auto_increment,
  game_id             character varying(100) not null,
  language            character varying(100) not null,
  width               character varying(100) not null,
  height              character varying(100) not null,
  help_file           character varying(200) not null,
  client              character varying(100) not null,
  static_url          character varying(300),
  game_server_url     character varying(200),
  mobile_game_url     character varying(200),
  base                character varying(300),
  vars                character varying(400),
  allow_script_access character varying(200),
  flash_version       character varying(100),
  window_mode         character varying(100),

  constraint netent_game_infos_pkey primary key (id),
  constraint netent_game_infos_game_id_fkey foreign key (game_id) references netent_games (game_id),
  constraint netent_game_infos_game_id_language_unique unique (game_id, language)
);
