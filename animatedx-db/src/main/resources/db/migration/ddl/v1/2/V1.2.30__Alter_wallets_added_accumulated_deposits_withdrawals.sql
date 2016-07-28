# accumulated_deposits

alter table wallets add column accumulated_deposits bigint;

update wallets w
  inner join
  (
    select
      player_id, sum(amount) 'accumulated'
    from payment_transactions p
    where p.status = 'SUCCESS' and p.event_code = 'AUTHORISATION'
    group by player_id

  ) temp on w.player_id = temp.player_id
set w.accumulated_deposits = temp.accumulated;

update wallets
set accumulated_deposits = 0
where accumulated_deposits is null;

alter table wallets modify column accumulated_deposits bigint not null;

# accumulated_withdrawals

alter table wallets add column accumulated_withdrawals bigint;

update wallets w
  inner join
  (
    select
      player_id, sum(amount) 'accumulated'
    from payment_transactions p
    where p.status = 'SUCCESS' and p.event_code = 'REFUND_WITH_DATA'
    group by player_id

  ) temp on w.player_id = temp.player_id
set w.accumulated_withdrawals = temp.accumulated;

update wallets
set accumulated_withdrawals = 0
where accumulated_withdrawals is null;

alter table wallets modify column accumulated_withdrawals bigint not null;
