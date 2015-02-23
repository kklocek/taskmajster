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

CREATE TABLE registered_users (
  login VARCHAR UNIQUE,
  mail VARCHAR,
  password VARCHAR
);



# --- !Downs

DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS registered_users;