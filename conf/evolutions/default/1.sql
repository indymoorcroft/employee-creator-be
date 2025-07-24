# --- !Ups

CREATE TABLE employees (
 id INT PRIMARY KEY AUTO_INCREMENT,
 first_name VARCHAR(100) NOT NULL,
 last_name VARCHAR(100) NOT NULL,
 email VARCHAR(255) UNIQUE NOT NULL,
 mobile_number VARCHAR(20) NOT NULL,
 address TEXT NOT NULL,
 created_at TIMESTAMP NOT NULL,
 updated_at TIMESTAMP NOT NULL
);


# --- !Downs

DROP TABLE IF EXISTS employees;