update payment_transactions
set amount = abs(amount)
where amount < 0;
