-- Table: levels

-- DROP TABLE levels;

create table levels
(
  level                    bigint                not null,
  turnover                 bigint                not null,
  cashback_percentage      decimal(10, 2)        not null,
  deposit_bonus_percentage decimal               not null,
  credit_dices             smallint              not null,
  money_credit_rate        integer               not null,
  bonus_credit_rate        integer               not null,
  status                   character varying(50) not null,
  created_by               bigint                not null,
  created_date             timestamp             not null,
  modified_by              bigint,
  modified_date            timestamp             null,
  constraint levels_pkey primary key (level),
  constraint levels_created_by_fkey foreign key (created_by) references users (id),
  constraint levels_modified_by_fkey foreign key (modified_by) references users (id)
);

-- Index: levels_active_idx

-- DROP INDEX levels_active_idx;

create index levels_active_idx on levels (status);

-- Index: levels_turnover_idx

-- DROP INDEX levels_turnover_idx;

create index levels_turnover_idx on levels (turnover);
