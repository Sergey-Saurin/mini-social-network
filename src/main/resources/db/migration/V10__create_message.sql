

CREATE TYPE rd_status AS ENUM ('SENT', 'READ');

CREATE TABLE message (
	id bigserial not null,
    time timestamp not null,
    author_id int8 not null,
    recipient_id int8 not null,
    message_text text,
    dialog_id int8 not null,
	read_status rd_status not null,
	is_deleted boolean,
    primary key (id));

