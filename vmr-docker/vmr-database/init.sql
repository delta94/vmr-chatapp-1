drop database if exists vmrchat;
create database vmrchat character set utf8mb4 collate utf8mb4_unicode_ci;
use vmrchat;

create table users
(
    id        bigint primary key auto_increment,
    username  varchar(20) not null unique,
    password  varchar(60) not null,
    name      varchar(45) not null,
    is_active boolean     not null default true,
    fulltext (username, name)
);

create table friends
(
    id        bigint primary key auto_increment,
    user_id   bigint                                                 not null,
    friend_id bigint                                                 not null,
    status    enum ('ACCEPTED', 'WAITING', 'NOT_ANSWER', 'REJECTED') not null,
    last_message_id bigint,
    num_unread_message int default 0,
    foreign key (user_id) references users (id),
    foreign key (friend_id) references users (id),
    unique key user_friend_unique (user_id, friend_id)
);

create table messages
(
    id        bigint primary key auto_increment,
    sender    bigint not null,
    receiver  bigint not null,
    send_time bigint not null,
    message   text   not null,
    index sender_receiver (sender, receiver)
);
