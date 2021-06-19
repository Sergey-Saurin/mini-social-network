
create table post_like (
    id  bigserial not null,
    time timestamp not null,
    person_id bigint not null,
    post_id bigint not null,
    primary key (id));


