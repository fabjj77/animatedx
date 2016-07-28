create table payment_methods
(
  id                  bigint                  not null auto_increment,
  name                character varying(100)  not null,
  method_name         character varying(100)  unique not null,
  max_deposit_amount  bigint                  not null,
  allowed_countries   character varying(3000),
  recurring_contracts character varying(200)  not null,
  embedded_frame      tinyint                 not null,
  constraint payment_methods_pkey primary key (id)
);

-- Index: payment_methods_method_name_idx

-- DROP INDEX payment_methods_method_name_idx;

create index payment_methods_method_name_idx on payment_methods (method_name);
