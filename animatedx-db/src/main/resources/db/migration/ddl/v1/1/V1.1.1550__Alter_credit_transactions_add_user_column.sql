alter table credit_transactions
    add column user_id bigint;

alter table credit_transactions
add constraint credit_transactions_user_id_fkey foreign key (user_id) references users (id)