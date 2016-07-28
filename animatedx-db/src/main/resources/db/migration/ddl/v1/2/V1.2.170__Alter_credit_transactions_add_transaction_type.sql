alter table credit_transactions add column transaction_type character varying (100) null;

update credit_transactions ct
set ct.transaction_type = 'CONVERSION';

alter table credit_transactions modify transaction_type character varying (100) not null;
