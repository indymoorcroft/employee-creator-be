# --- !Ups

CREATE TABLE contracts (
 id INT AUTO_INCREMENT PRIMARY KEY,
 employee_id INT NOT NULL,
 start_date DATE NOT NULL,
 end_date DATE,
 contract_type ENUM('PERMANENT', 'CONTRACT') NOT NULL,
 employment_type ENUM('FULL_TIME', 'PART_TIME') NOT NULL,
 hours_per_week DECIMAL(4, 2) NOT NULL,
 created_at TIMESTAMP NOT NULL,
 updated_at TIMESTAMP NOT NULL,

 CONSTRAINT fk_employee
   FOREIGN KEY (employee_id)
   REFERENCES employees(id)
   ON DELETE CASCADE
);


# --- !Downs

DROP TABLE IF EXISTS contracts;