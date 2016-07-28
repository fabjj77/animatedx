create table player_activities
(
  id                   bigint                 	not null auto_increment,
  player_id            bigint                 	not null,
  activity_type		     character varying(100)   not null,
  activity_date        timestamp              	not null,
  session_id           character varying(200),
  ip_address           character varying(15),
  constraint player_activities_pkey primary key (id),
  constraint player_activities_fkey foreign key (player_id) references players (id)
);
