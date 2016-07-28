insert into players_promotions (promotion_id, activation_date, player_id)
  select
    4, current_timestamp, id
  from players;
