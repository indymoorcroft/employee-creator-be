package employee.dtos

import play.api.libs.json._

case class CreateEmployeeDto(firstName: String, lastName: String, email: String, mobileNumber: String, address: String)

object CreateEmployeeDto {
  implicit val reads: Reads[CreateEmployeeDto] = Json.reads[CreateEmployeeDto]
}