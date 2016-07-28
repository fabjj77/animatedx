update wallets w
set w.player_bonus_id = (
  select
    pb.id
  from players_bonuses pb
  where pb.bonus_id = '129' and pb.player_id = w.player_id and pb.status = 'ACTIVE');

