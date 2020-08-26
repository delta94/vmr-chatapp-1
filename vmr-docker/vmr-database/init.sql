drop database if exists vmrchat;
create database vmrchat character set utf8mb4 collate utf8mb4_unicode_ci;
use vmrchat;

create table users
(
    id        integer primary key auto_increment,
    username  varchar(20) not null unique,
    password  varchar(60) not null,
    name      varchar(45) not null,
    is_active boolean default true
);

create table friends
(
    id       integer primary key auto_increment,
    user_id  integer not null,
    user2_id integer not null,
    constraint friends_user_fk foreign key (user_id) references users (id),
    constraint friends_user2_fk foreign key (user2_id) references users (id)
);

create table conservations
(
    id                integer primary key auto_increment,
    conservation_name varchar(45),
    conservation_type enum ('NORMAL', 'GROUP') not null,
    owner_id          integer,
    constraint conservations_owner_id foreign key (owner_id) references users (id)
);

create table user_conservations
(
    id              integer primary key auto_increment,
    user_id         integer not null,
    conservation_id integer not null,
    constraint user_conservations_user_id foreign key (user_id) references users (id),
    constraint user_conservations_conservation_id foreign key (conservation_id) references conservations (id)
);

create table messages
(
    id              integer primary key auto_increment,
    user_id         integer   not null,
    conservation_id integer   not null,
    send_time       timestamp not null,
    message         text      not null
);
