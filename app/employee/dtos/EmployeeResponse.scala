package employee.dtos

import employee.models.Employee
import play.api.libs.json._

import java.time.LocalDateTime

case class EmployeeResponse(id: Long, firstName: String, lastName: String, email: String, mobileNumber: String, address: String, createdAt: LocalDateTime, updatedAt: LocalDateTime)

object EmployeeResponse {
  implicit val format: OFormat[EmployeeResponse] = Json.format[EmployeeResponse]

  def fromModel(model: Employee): EmployeeResponse = {
    EmployeeResponse(id = model.id.getOrElse(0L), firstName = model.firstName, lastName = model.lastName, email = model.email, mobileNumber = model.mobileNumber, address = model.address, createdAt = model.createdAt.toLocalDateTime, updatedAt = model.updatedAt.toLocalDateTime)
  }
}
