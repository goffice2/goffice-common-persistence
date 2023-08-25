create table user_details
(
    id           serial
        primary key,
    full_name    varchar(255) default NULL::character varying,
    uuid         varchar(255) default NULL::character varying,
    datecreated  timestamp,
    datemodified timestamp,
    createdby    varchar(255) default NULL::character varying,
    modifiedby   varchar(255) default NULL::character varying,
    hash         varchar(255) default NULL::character varying
);

alter table user_details
    owner to kk;


create table user_details
(
    id           bigint unsigned auto_increment
        primary key,
    full_name    varchar(255) null,
    uuid         varchar(255) null,
    datecreated  timestamp    null,
    datemodified timestamp    null,
    createdby    varchar(255) null,
    modifiedby   varchar(255) null,
    hash         varchar(255) null,
    constraint id
        unique (id)
)
    auto_increment = 3;

