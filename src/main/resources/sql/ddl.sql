CREATE TABLE users
(
    user_id    BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(100) NOT NULL,
    nickname   VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email),
    UNIQUE KEY uk_users_nickname (nickname)
);

CREATE TABLE categories
(
    category_id   BIGINT      NOT NULL AUTO_INCREMENT,
    code          VARCHAR(50) NOT NULL,
    name          VARCHAR(50) NOT NULL,
    display_order INT         NOT NULL,
    PRIMARY KEY (category_id),
    UNIQUE KEY uk_categories_code (code),
    UNIQUE KEY uk_categories_display_order (display_order)
);

INSERT INTO categories (code, name, display_order)
VALUES ('EXERCISE', '운동', 1),
       ('VISIT', '방문', 2),
       ('GATHERING', '모임', 3),
       ('CREATION', '창작', 4),
       ('LEARNING', '학습', 5),
       ('APPRECIATION', '감상', 6),
       ('REST', '휴식', 7),
       ('ETC', '기타', 8);
