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
    balance      bigint      not null default 0,
    last_updated bigint      not null,
    fulltext (username, name)
);

create table friends
(
    id                 bigint primary key auto_increment,
    user_id            bigint                                                              not null,
    friend_id          bigint                                                              not null,
    status             enum ('ACCEPTED', 'WAITING', 'NOT_ANSWER', 'REMOVED', 'UNFRIENDED') not null,
    last_message_id    bigint,
    num_unread_message int default 0,
    foreign key (user_id) references users (id),
    foreign key (friend_id) references users (id),
    unique key user_friend_unique (user_id, friend_id)
);


create table transfers
(
    id          bigint primary key auto_increment,
    sender_id   bigint not null,
    receiver_id bigint not null,
    amount      bigint not null,
    message     text   not null,
    timestamp   bigint not null,
    request_id  bigint not null,
    unique (sender_id, request_id),
    foreign key (sender_id) references users (id),
    foreign key (receiver_id) references users (id)
);

create table messages
(
    id          bigint primary key auto_increment,
    sender_id   bigint                    not null,
    receiver_id bigint                    not null,
    send_time   bigint                    not null,
    message     text                      not null,
    type        enum ('CHAT', 'TRANSFER') not null default 'CHAT',
    transfer_id bigint,
    index sender_receiver (sender_id, receiver_id)
);


create table account_logs
(
    id          bigint primary key auto_increment,
    user_id     bigint                       not null,
    transfer_id bigint                       not null,
    type        enum ('TRANSFER', 'RECEIVE') not null,
    foreign key (user_id) references users (id),
    foreign key (transfer_id) references transfers (id)
);

set @pw = '$2a$05$oaUZlei0KVo9hWwG.0mpqebMwvGcHlaM7.Kgrpf10ncG7A36xvMGu';

insert into users (id, username, name, password, last_updated, balance)
values (1, 'danganhvan', 'Đặng Anh Văn', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (2, 'dangduymanh', 'Đặng Duy Mạnh', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (3, 'phamanhtuan', 'Phạm Anh Tuấn', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (4, 'lengocphuong', 'Lê Ngọc Phương', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (5, 'nguyenvannam', 'Nguyễn Văn Nam', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (6, 'nguyenthithuylinh', 'Nguyen Thi Thuy Linh', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (7, 'dangphuongthao', 'Đặng Phương Thảo', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (8, 'dangtunglam', 'Đặng Tùng Lâm', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (9, 'vovanduc', 'Võ Văn Đức', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (10, 'nguyenphuan', 'Nguyễn Phú An', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (11, 'nguyenthuhuong', 'Nguyễn Thu Hương', @pw,
        UNIX_TIMESTAMP(), 1000000),
       (12, 'lethanhdat', 'Lê Thành Đạt', @pw,
        UNIX_TIMESTAMP(), 1000000);

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
       (7, 1, 'WAITING'),
       (1, 8, 'ACCEPTED'),
       (8, 1, 'ACCEPTED'),
       (1, 9, 'ACCEPTED'),
       (9, 1, 'ACCEPTED'),
       (1, 10, 'ACCEPTED'),
       (10, 1, 'ACCEPTED'),
       (1, 11, 'ACCEPTED'),
       (11, 1, 'ACCEPTED'),
       (1, 12, 'ACCEPTED'),
       (12, 1, 'ACCEPTED'),
       (2, 3, 'ACCEPTED'),
       (3, 2, 'ACCEPTED'),
       (2, 4, 'ACCEPTED'),
       (4, 2, 'ACCEPTED'),
       (2, 5, 'ACCEPTED'),
       (5, 2, 'ACCEPTED'),
       (2, 6, 'ACCEPTED'),
       (6, 2, 'ACCEPTED'),
       (2, 7, 'ACCEPTED'),
       (7, 2, 'ACCEPTED'),
       (2, 8, 'ACCEPTED'),
       (8, 2, 'ACCEPTED'),
       (2, 9, 'ACCEPTED'),
       (9, 2, 'ACCEPTED'),
       (2, 10, 'ACCEPTED'),
       (10, 2, 'ACCEPTED'),
       (2, 11, 'ACCEPTED'),
       (11, 2, 'ACCEPTED'),
       (2, 12, 'ACCEPTED'),
       (12, 2, 'ACCEPTED');

delimiter $$
create procedure fake_user()
begin
    declare x int;
    set x = 13;
    loop_label:
    loop
        if x > 100 then
            leave loop_label;
        end if;
        insert into users(id, username, name, password, last_updated)
        values (x, concat('user', x), concat('User Name ', x), @pw, UNIX_TIMESTAMP());
        set x = x + 1;
    end loop;
end;
$$

-- call fake_user();
