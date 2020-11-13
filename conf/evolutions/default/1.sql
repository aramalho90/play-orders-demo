# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table item (
  item_id                       integer auto_increment not null,
  name                          varchar(255),
  constraint pk_item primary key (item_id)
);

create table orders (
  order_id                      integer auto_increment not null,
  creation_date                 varchar(255),
  item_id                       integer,
  quantity                      integer,
  user_id                       integer,
  fulfilled                     integer,
  constraint pk_orders primary key (order_id)
);

create table order_stocks (
  orders_order_id               integer not null,
  stock_stock_id                integer not null,
  constraint pk_order_stocks primary key (orders_order_id,stock_stock_id)
);

create table stock (
  stock_id                      integer auto_increment not null,
  creation_date                 varchar(255),
  item_id                       integer,
  quantity                      integer,
  constraint pk_stock primary key (stock_id)
);

create table stock_orders (
  stock_stock_id                integer not null,
  orders_order_id               integer not null,
  constraint pk_stock_orders primary key (stock_stock_id,orders_order_id)
);

create table user (
  user_id                       integer auto_increment not null,
  name                          varchar(255),
  email                         varchar(255),
  constraint pk_user primary key (user_id)
);

create index ix_orders_item_id on orders (item_id);
alter table orders add constraint fk_orders_item_id foreign key (item_id) references item (item_id) on delete restrict on update restrict;

create index ix_orders_user_id on orders (user_id);
alter table orders add constraint fk_orders_user_id foreign key (user_id) references user (user_id) on delete restrict on update restrict;

create index ix_order_stocks_orders on order_stocks (orders_order_id);
alter table order_stocks add constraint fk_order_stocks_orders foreign key (orders_order_id) references orders (order_id) on delete restrict on update restrict;

create index ix_order_stocks_stock on order_stocks (stock_stock_id);
alter table order_stocks add constraint fk_order_stocks_stock foreign key (stock_stock_id) references stock (stock_id) on delete restrict on update restrict;

create index ix_stock_item_id on stock (item_id);
alter table stock add constraint fk_stock_item_id foreign key (item_id) references item (item_id) on delete restrict on update restrict;

create index ix_stock_orders_stock on stock_orders (stock_stock_id);
alter table stock_orders add constraint fk_stock_orders_stock foreign key (stock_stock_id) references stock (stock_id) on delete restrict on update restrict;

create index ix_stock_orders_orders on stock_orders (orders_order_id);
alter table stock_orders add constraint fk_stock_orders_orders foreign key (orders_order_id) references orders (order_id) on delete restrict on update restrict;


# --- !Downs

alter table orders drop constraint if exists fk_orders_item_id;
drop index if exists ix_orders_item_id;

alter table orders drop constraint if exists fk_orders_user_id;
drop index if exists ix_orders_user_id;

alter table order_stocks drop constraint if exists fk_order_stocks_orders;
drop index if exists ix_order_stocks_orders;

alter table order_stocks drop constraint if exists fk_order_stocks_stock;
drop index if exists ix_order_stocks_stock;

alter table stock drop constraint if exists fk_stock_item_id;
drop index if exists ix_stock_item_id;

alter table stock_orders drop constraint if exists fk_stock_orders_stock;
drop index if exists ix_stock_orders_stock;

alter table stock_orders drop constraint if exists fk_stock_orders_orders;
drop index if exists ix_stock_orders_orders;

drop table if exists item;

drop table if exists orders;

drop table if exists order_stocks;

drop table if exists stock;

drop table if exists stock_orders;

drop table if exists user;

