create database wewallet character set utf8;
use wewallet


create table account
(
   account_id           int not null primary key auto_increment,
   email                 varchar(50),
   username             varchar(30),
   identity_id          varchar(100),
   address              varchar(256)
);
CREATE UNIQUE INDEX account_email_uindex ON wewallet.account (email);
CREATE UNIQUE INDEX account_name_uindex ON wewallet.account (username);
alter table account comment '账户表';




create table wallet
(
   wallet_id         int not null primary key auto_increment,
   walletname        varchar(50),
   wallet_address              varchar(100)
);


