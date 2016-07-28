-- Insert the current term and conditions
insert into terms_and_conditions_versions (version, created_date)
values ('1.2', timestamp('2014-05-12 00:00:00'));

-- Get the id
set @last_id = LAST_INSERT_ID();

-- Map all players to it
insert into players_terms_and_conditions_versions (player_id, terms_and_conditions_versions_id, accepted_date)
  select
    id, @last_id, current_timestamp
  from players;
