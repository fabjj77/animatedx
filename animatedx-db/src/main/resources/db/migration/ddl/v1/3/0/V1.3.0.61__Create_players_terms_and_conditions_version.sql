create table players_terms_and_conditions_versions
(
  player_id                        bigint(20) not null,
  terms_and_conditions_versions_id bigint(20) not null,
  accepted_date                    timestamp  not null default current_timestamp,

  constraint players_terms_and_conditions_version_pkey primary key (terms_and_conditions_versions_id, player_id),
  constraint players_terms_and_conditions_version_player_id_fkey foreign key (player_id) references players (id),
  constraint players_terms_and_conditions_version_terms_id_fkey foreign key (terms_and_conditions_versions_id) references terms_and_conditions_versions (id)
);
