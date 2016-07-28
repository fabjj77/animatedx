alter table devcode_transactions
add fee bigint,
add fee_currency character varying(3),
add psp_amount bigint,
add psp_currency character varying(3);
