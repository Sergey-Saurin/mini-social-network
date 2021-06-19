
CREATE TABLE notification
(
    id bigserial NOT NULL,
    type_id bigint NOT NULL,
    sent_time timestamp NOT NULL,
    entity_id bigint NOT NULL,
    info varchar(255) NOT NULL,
    person_id bigint NOT NULL,
    contact varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

