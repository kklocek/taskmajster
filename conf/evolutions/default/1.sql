# --- !Ups

CREATE TABLE tasks (
    id LONG AUTO_INCREMENT,
    kind VARCHAR,
    name VARCHAR,
    priority INT,
    createDate DATETIME,
    data TEXT
);

CREATE TABLE users (
    id LONG AUTO_INCREMENT,
    name VARCHAR UNIQUE
);

# --- !Downs

DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS users;