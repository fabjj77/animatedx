-- Database: anix

-- drop database anix;

create database if not exists anix;

-- This will create the users if they don't exist, otherwise give them privileges
grant all on anix.* to 'anix_sa'@'%' identified by 'anix' with grant option;
grant select, insert, update, delete on anix.* to 'anix'@'%' identified by 'anix';
