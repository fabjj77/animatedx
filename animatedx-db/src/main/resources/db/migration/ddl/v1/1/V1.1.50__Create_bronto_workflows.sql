create table bronto_workflows
(
  id          bigint                 not null auto_increment,
  workflow_id character varying(200) not null,
  name        character varying(200) not null,
  status      character varying(100) not null,
  constraint bronto_workflows_pkey primary key (id)
);

create index bronto_fields_field_id_idx on bronto_workflows (workflow_id)
