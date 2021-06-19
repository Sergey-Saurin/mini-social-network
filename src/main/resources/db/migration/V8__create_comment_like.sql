
create table comment_like(
    id bigserial not null,
    time timestamp not null,
    person_id bigint not null,
    comment_id bigint not null,
    primary key (id)
);