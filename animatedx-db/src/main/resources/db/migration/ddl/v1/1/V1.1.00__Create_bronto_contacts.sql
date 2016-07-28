create table bronto_contacts
(
  id            bigint                 not null auto_increment,
  player_id     bigint                 not null,
  bronto_id     character varying(200) not null,
  status        character varying(50)  not null,
  created_date  timestamp              not null,
  modified_by   bigint,
  modified_date timestamp              null,
  constraint bronto_contacts_pkey primary key (id),
  constraint bronto_contacts_player_id_fkey foreign key (player_id) references players (id),
  constraint bronto_contacts_modified_by_fkey foreign key (modified_by) references users (id)
);

create index bronto_contacts_bronto_id_idx on bronto_contacts (bronto_id)
