package employee.validation

import employee.dtos.CreateEmployeeDto
import utils.Validator

object EmployeeValidator extends Validator {
  def validateCreate(dto: CreateEmployeeDto): Map[String, String] = {
    List(
      isNotEmpty("firstName", dto.firstName),
      isNotEmpty("lastName", dto.lastName),
      isNotEmpty("email", dto.email),
      isNotEmpty("mobileNumber", dto.mobileNumber),
      isNotEmpty("address", dto.address),
    ).flatten.toMap
  }
}
