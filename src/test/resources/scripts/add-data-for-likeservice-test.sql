DELETE FROM person;
DELETE FROM post;
DELETE FROM comment;
DELETE FROM post_like;
DELETE FROM comment_like;

INSERT INTO person
    (id, first_name, reg_date, password, is_approved, messages_permission, last_online_time, is_blocked, email)
    VALUES
    (1, 'Anny', current_timestamp, '$2y$12$mWD0Uz3iQN9VKoU0TjkDteXm.ngUgEz13AiUqMGKeQ6J1Th24K.uq', TRUE, 'ALL', current_timestamp, FALSE, 'my@mail.ru');

INSERT INTO post
    (id, is_blocked, post_text, time, title, author_id)
    VALUES
    (1, FALSE, 'post test', current_timestamp, 'Title', 1);

INSERT INTO comment
    (id, author_id, comment_text, is_blocked, time, post_id)
    VALUES
    (1, 1, 'comment text', FALSE, current_timestamp, 1);

INSERT INTO post_like
    (id, time, person_id, post_id)
    VALUES
    (1, current_timestamp, 1, 1);

INSERT INTO comment_like
    (id, time, person_id, comment_id)
    VALUES
    (1, current_timestamp, 1, 1);