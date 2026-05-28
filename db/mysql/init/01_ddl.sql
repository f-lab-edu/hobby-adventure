SET NAMES 'utf8mb4';

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

CREATE TABLE explorations
(
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    category_id       BIGINT       NOT NULL,
    title             VARCHAR(255) NOT NULL,
    thumbnail_url     VARCHAR(500),
    short_description VARCHAR(500) NOT NULL,
    description       TEXT         NOT NULL,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_explorations_category FOREIGN KEY (category_id) REFERENCES categories (category_id)
);

CREATE TABLE user_explorations
(
    id             BIGINT      NOT NULL AUTO_INCREMENT,
    user_id        BIGINT      NOT NULL,
    exploration_id BIGINT      NOT NULL,
    status         VARCHAR(20) NOT NULL,
    created_at     TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at   TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_user_explorations_user FOREIGN KEY (user_id) REFERENCES users (user_id),
    CONSTRAINT fk_user_explorations_exploration FOREIGN KEY (exploration_id) REFERENCES explorations (id)
);
