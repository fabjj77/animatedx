alter table promotions
drop column netent_bonus_code,
change type name character varying(200);
