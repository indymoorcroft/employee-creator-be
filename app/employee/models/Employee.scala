package employee.models

import java.sql.Timestamp


case class Employee(
                     id: Option[Long] = None,
                     firstName: String,
                     lastName: String,
                     email: String,
                     mobileNumber: String,
                     address: String,
                     createdAt: Timestamp,
                     updatedAt: Timestamp
                   )