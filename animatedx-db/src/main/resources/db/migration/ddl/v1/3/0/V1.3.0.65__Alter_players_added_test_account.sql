alter table players add test_account tinyint;

update players
set test_account = 0;

alter table players modify column test_account tinyint not null;
