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

INSERT INTO tasks VALUES (1, 'deadline', 'Task 1', 1, '2015-01-01 01:00:00', '');
INSERT INTO tasks VALUES (2, 'deadline', 'Task 2', 2, '2015-01-01 01:00:00', '');

# --- !Downs

DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS users;