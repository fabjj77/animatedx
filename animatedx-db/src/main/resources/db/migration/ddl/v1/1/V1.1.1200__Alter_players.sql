alter table players
add column receive_promotion character varying(200);

update players
set receive_promotion = "SUBSCRIBED";

alter table players
modify COLUMN receive_promotion
character varying(200) not null;
