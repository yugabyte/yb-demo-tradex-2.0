create role mercury login password 'mercury123#';
grant yb_db_admin to mercury;
create database mercury with owner mercury;

create role venus login password 'venus123#';
grant yb_db_admin to venus;
create database venus with owner venus;


create role uranus login password 'uranus123#';
grant yb_db_admin to uranus;
create database uranus with owner uranus;

create role neptune login password 'neptuney123#';
grant yb_db_admin to neptune;
create database neptune with owner neptune;
