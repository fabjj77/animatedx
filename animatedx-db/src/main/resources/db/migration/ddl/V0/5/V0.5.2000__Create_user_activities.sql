create table user_activities
(
  id            bigint                not null auto_increment,
  user_id       bigint                not null,
  activity_type character varying(80) not null,
  activity_date timestamp             not null,
  description   character varying(800),
  constraint user_activities_pkey primary key (id),
  constraint user_activities_fkey foreign key (user_id) references users (id)
);
