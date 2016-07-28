-- Table: avatars

-- DROP TABLE avatars;

create table avatars
(
  id                    bigint                 not null auto_increment,
  avatar_base_type_id   integer                not null,
  level                 bigint                 not null,
  skin                  smallint               not null,
  hair                  smallint               not null,
  picture_url           character varying(120) not null,
  night_background_url  character varying(120) not null,
  day_background_url    character varying(120) not null,
  text_id               integer                not null,
  status                character varying(50)   not null,
  created_by            bigint                 not null,
  created_date          timestamp              not null,
  modified_by           bigint,
  modified_date         timestamp              null,
  constraint avatars_pkey primary key (id),
  constraint avatars_avatar_base_type_id_fkey foreign key (avatar_base_type_id) references avatar_base_types (id),
  constraint avatars_created_by_fkey foreign key (created_by) references users (id),
  constraint avatars_level_fkey foreign key (level) references levels (level),
  constraint avatars_modified_by_fkey foreign key (modified_by) references users (id)
);

-- Index: avatars_active_idx

-- DROP INDEX avatars_active_idx;

create index avatars_active_idx on avatars (status);

--  Index: avatars_avatar_base_type_id_level_idx

-- DROP INDEX avatars_avatar_base_type_id_level_idx;

create index avatars_avatar_base_type_id_level_idx on avatars (avatar_base_type_id, level);
