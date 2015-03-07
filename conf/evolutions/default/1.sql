# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table blog (
  id                        bigint not null,
  label                     varchar(255),
  private_link              varchar(255),
  public_link               varchar(255),
  timezone                  varchar(255),
  created_on                timestamp,
  constraint uq_blog_private_link unique (private_link),
  constraint uq_blog_public_link unique (public_link),
  constraint pk_blog primary key (id))
;

create table blog_entry (
  id                        bigint not null,
  blog_id                   bigint,
  mood                      varchar(6),
  tstamp                    timestamp,
  notes                     varchar(255),
  constraint ck_blog_entry_mood check (mood in ('HAPPY','NORMAL','SAD','ANGRY')),
  constraint pk_blog_entry primary key (id))
;

create sequence blog_seq;

create sequence blog_entry_seq;

alter table blog_entry add constraint fk_blog_entry_blog_1 foreign key (blog_id) references blog (id) on delete restrict on update restrict;
create index ix_blog_entry_blog_1 on blog_entry (blog_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists blog;

drop table if exists blog_entry;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists blog_seq;

drop sequence if exists blog_entry_seq;

