create table chapter_translations
(
  id            integer                 not null auto_increment,
  text_id       integer                 not null,
  language      character varying(20)   not null,
  translation   character varying(1000) not null,
  created_by    bigint                  not null,
  created_date  timestamp               not null,
  modified_by   bigint,
  modified_date timestamp               null,
  constraint chapter_translations_pkey primary key (id),
  constraint chapter_translations_ukey unique key (text_id, language),
  constraint chapter_translations_created_by_fkey foreign key (created_by) references users (id),
  constraint chapter_translations_modified_by_fkey foreign key (modified_by) references users (id)
);
