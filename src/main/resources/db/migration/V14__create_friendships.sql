

    CREATE TABLE friendships (
        id bigserial NOT NULL,
        status_id bigint references friendships_status (id) NOT NULL,
        src_person_id bigint references person (id) NOT NULL,
        dst_person_id bigint references person (id) NOT NULL,
        PRIMARY KEY (id));


