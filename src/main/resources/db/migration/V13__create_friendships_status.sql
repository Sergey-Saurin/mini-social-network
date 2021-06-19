


    CREATE TABLE friendships_status (
        id bigserial NOT NULL,
        time TIMESTAMP NOT NULL,
        name VARCHAR(255) NOT NULL,
        code VARCHAR(255) NOT NULL,
        PRIMARY KEY (id));



    --    code
    --    REQUEST - Запрос на добавление в друзья
    --    FRIEND - Друзья
    --    BLOCKED - Пользователь в чёрном списке
    --    DECLINED - Запрос на добавление в друзья отклонён
    --    SUBSCRIBED - Подписан



