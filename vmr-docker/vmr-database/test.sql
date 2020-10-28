use vmrchat;

select sum(balance), count(*) from users;

select * from users;

select count(*) from transfers;

show open tables in vmrchat;

SHOW ENGINE INNODB STATUS;

show status like 'Conn%';