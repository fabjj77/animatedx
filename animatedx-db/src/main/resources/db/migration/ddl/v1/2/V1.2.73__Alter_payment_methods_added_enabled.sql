alter table payment_methods add column enabled tinyint;

update payment_methods
set enabled = 1;

alter table payment_methods modify column enabled tinyint not null;
