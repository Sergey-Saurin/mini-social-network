

create table post (
    id  bigserial not null,
    is_blocked boolean not null,
    post_text text,
    time timestamp not null,
    title varchar(255),
    author_id bigint not null,
    primary key (id));

