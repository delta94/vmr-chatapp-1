set names 'utf8mb4';
drop database if exists vmrchat;
create database vmrchat character set utf8mb4 collate utf8mb4_unicode_ci;
use vmrchat;

create table users
(
    id           bigint primary key auto_increment,
    username     varchar(20) not null unique,
    password     varchar(60) not null,
    name         varchar(45) not null,
    balance      bigint      not null default 10000000,
    last_updated bigint      not null,
    fulltext (username, name)
);

create table friends
(
    id                 bigint primary key auto_increment,
    user_id            bigint                                                not null,
    friend_id          bigint                                                not null,
    status             enum ('ACCEPTED', 'WAITING', 'NOT_ANSWER', 'REMOVED') not null,
    last_message_id    bigint,
    num_unread_message int default 0,
    foreign key (user_id) references users (id),
    foreign key (friend_id) references users (id),
    unique key user_friend_unique (user_id, friend_id)
);


create table transfers
(
    id         bigint primary key auto_increment,
    sender     bigint not null,
    receiver   bigint not null,
    amount     bigint not null,
    message    text   not null,
    timestamp  bigint not null,
    request_id bigint not null,
    unique (sender, request_id),
    foreign key (sender) references users (id),
    foreign key (receiver) references users (id)
);

create table messages
(
    id          bigint primary key auto_increment,
    sender      bigint                    not null,
    receiver    bigint                    not null,
    send_time   bigint                    not null,
    message     text                      not null,
    type        enum ('CHAT', 'TRANSFER') not null default 'CHAT',
    transfer_id bigint,
    index sender_receiver (sender, receiver)
);


create table account_logs
(
    id       bigint primary key auto_increment,
    user     bigint                       not null,
    transfer bigint                       not null,
    balance  bigint                       not null,
    type     enum ('TRANSFER', 'RECEIVE') not null,
    foreign key (user) references users (id),
    foreign key (transfer) references transfers (id)
);

insert into users (id, username, name, password, last_updated)
values (1, 'danganhvan', 'Đặng Anh Văn', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP()),
       (2, 'dangduymanh', 'Đặng Duy Mạnh', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP()),
       (3, 'phamanhtuan', 'Phạm Anh Tuấn', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP()),
       (4, 'lengocphuong', 'Lê Ngọc Phương', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP()),
       (5, 'nguyenvannam', 'Nguyễn Văn Nam', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP()),
       (6, 'nguyenthithuylinh', 'Nguyễn Thị Thùy Linh', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP()),
       (7, 'dangphuongthao', 'Đặng Phương Thảo', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP()),
       (8, 'dangtunglam', 'Đặng Tùng Lâm', '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
        UNIX_TIMESTAMP());

insert into friends (user_id, friend_id, status)
values (1, 2, 'ACCEPTED'),
       (1, 3, 'ACCEPTED'),
       (1, 4, 'ACCEPTED'),
       (1, 5, 'ACCEPTED'),
       (5, 1, 'ACCEPTED'),
       (3, 1, 'ACCEPTED'),
       (2, 1, 'ACCEPTED'),
       (4, 1, 'ACCEPTED'),
       (1, 6, 'WAITING'),
       (6, 1, 'NOT_ANSWER'),
       (1, 7, 'NOT_ANSWER'),
       (7, 1, 'WAITING');

delimiter $$
create procedure fake_user()
begin
    declare x int;
    set x = 9;
    loop_label:
    loop
        if x > 100 then
            leave loop_label;
        end if;
        insert into users(id, username, name, password, last_updated)
        values (x, concat('user', x), concat('User Name ', x),
                '$2a$10$UjfgnhYV9gRWM/2HmOuuleRCA1e9bzfB7H95d/4eGFHvCUmYgH/u.',
                UNIX_TIMESTAMP());
        set x = x + 1;
    end loop;
end;


call fake_user();
