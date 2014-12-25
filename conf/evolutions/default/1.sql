# --- !Ups //#A

CREATE TABLE tasks ( //#B
    id long,
    kind varchar,
    name varchar,
    priority int,
    createDate datetime,
    data text
);

# --- !Downs //#C

DROP TABLE IF EXISTS tasks;
