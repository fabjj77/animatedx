-- Table: players

-- DROP TABLE players;

create table players
(
  id                 bigint                not null auto_increment,
  first_name         character varying(30) not null,
  last_name          character varying(30) not null,
  email_address      character varying(50) not null,
  password           character varying(70) not null,
  nickname           character varying(30) not null,
  birthday           date                  not null,
  avatar_id          bigint                not null,
  level              bigint                not null,
  street             character varying(70) not null,
  street2            character varying(70),
  zip_code           character varying(10) not null,
  city               character varying(50) not null,
  state              character varying(50),
  country            character varying(40) not null,
  currency           character varying(3)  not null,
  phone_number       character varying(30) not null,
  status             character varying(50)  not null,
  verification       character varying(30) not null,
  email_verification character varying(30) not null,
  created_date       timestamp             not null,
  modified_date      timestamp             null,
  trust_level        character varying(20) not null,
  block_type         character varying(80) not null,
  block_end_date     timestamp             not null,
  language           character varying(20) not null,

  constraint players_pkey primary key (id),
  constraint players_avatar_id_fkey foreign key (avatar_id) references avatars (id),
  constraint players_level_fkey foreign key (level) references levels (level),
  constraint players_email_address_key unique (email_address),
  constraint players_nickname_key unique (nickname)
);

-- Index: players_active_idx

-- DROP INDEX players_active_idx;

create index players_active_idx on players (status);

-- Index: players_avatar_id_idx

-- DROP INDEX players_avatar_id_idx;

create index players_avatar_id_idx on players (avatar_id);

-- Index: players_country_idx

-- DROP INDEX players_country_idx;

create index players_country_idx on players (country);

-- Index: players_email_address_active_idx

-- DROP INDEX players_email_address_active_idx;

create index players_email_address_active_idx on players (email_address, status);

-- Index: players_email_address_idx

-- DROP INDEX players_email_address_idx;

create index players_email_address_idx on players (email_address);

-- Index: players_level_idx

-- DROP INDEX players_level_idx;

create index players_level_idx on players (level);

-- Index: players_nickname_idx

-- DROP INDEX players_nickname_idx;

create index players_nickname_idx on players (nickname);
