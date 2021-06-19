

create table comment (
    id  bigserial not null,
    author_id int8,
    comment_text text,
    is_blocked boolean not null,
    parent_id bigint,
    time timestamp not null,
    post_id bigint not null,
    primary key (id));

