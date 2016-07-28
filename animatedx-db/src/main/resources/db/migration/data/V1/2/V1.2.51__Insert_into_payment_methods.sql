INSERT INTO payment_methods (name, method_name, max_deposit_amount, allowed_countries, recurring_contracts, embedded_frame)
VALUES
  ('visa', 'visa', 300000, null, 'PAYOUT', 0),
  ('MasterCard', 'mc', 300000, null, 'PAYOUT', 0),
  ('Bank Transfer', 'bankTransfer_NL', 0, 'NETHERLANDS', 'PAYOUT', 0),
  ('Bank Transfer', 'bankTransfer_PL', 0, 'POLAND', 'PAYOUT', 0),
  ('Bank Transfer', 'bankTransfer_GB', 0, 'UNITED_KINGDOM', 'PAYOUT', 0),
  ('Bank Transfer', 'bankTransfer_DE', 0, 'GERMANY', 'PAYOUT', 0),
  ('Bank Transfer', 'bankTransfer_IBAN', 0, null, 'PAYOUT', 0),
  ('Trustly', 'trustly', 0, null, '', 1),
  ('Skrill', 'moneybookers', 0, null, 'PAYOUT', 0),
  ('NETELLER', 'neteller', 0, null, 'PAYOUT', 0),
  ('Paysafecard', 'paysafecard', 100000, null, '', 0),
  ('Giropay', 'giropay', 0, 'AUSTRIA;GERMANY', 'PAYOUT', 0),
  ('Direct-ebanking', 'directEbanking', 0, 'AUSTRIA;GERMANY', 'PAYOUT', 0),
  ('Ebanking', 'ebanking_FI', 0, 'FINLAND', '', 0),
  ('iDEAL', 'ideal', 0, 'NETHERLANDS', 'PAYOUT', 0),
  ('Dotpay', 'dotpay', 0, 'POLAND', '', 0);
