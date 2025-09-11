# --- !Ups

CREATE TABLE users (
 id INT PRIMARY KEY AUTO_INCREMENT,
 email VARCHAR(255) UNIQUE NOT NULL,
 password_hash VARCHAR(255) NOT NULL,
 created_at TIMESTAMP NOT NULL,
 updated_at TIMESTAMP NOT NULL
);


# --- !Downs

DROP TABLE IF EXISTS employees;