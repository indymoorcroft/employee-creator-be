package employee.validation

import employee.dtos.{CreateEmployeeDto, UpdateEmployeeDto}
import utils.Validator

object EmployeeValidator extends Validator {
  def validateCreate(dto: CreateEmployeeDto): Map[String, String] = {
    List(
      isNotEmpty("firstName", dto.firstName),
      isNotEmpty("lastName", dto.lastName),
      isNotEmpty("email", dto.email).orElse(isValidEmail("email", dto.email)),
      isNotEmpty("mobileNumber", dto.mobileNumber).orElse(isValidMobile("mobileNumber", dto.mobileNumber)),
      isNotEmpty("address", dto.address),
    ).flatten.toMap
  }

  def validatePatch(dto: UpdateEmployeeDto): Map[String, String] = {
    List(
      isNonBlankIfDefined("firstName", dto.firstName),
      isNonBlankIfDefined("lastName", dto.lastName),
      dto.email.flatMap(isValidEmail("email", _)),
      dto.mobileNumber.flatMap(isValidMobile("mobileNumber", _)),
      isNonBlankIfDefined("address", dto.address)
    ).flatten.toMap
  }
}
