package employee.validation

import employee.dtos.{CreateEmployeeDto, UpdateEmployeeDto}
import utils.Validator

object EmployeeValidator extends Validator {
  def validateCreate(dto: CreateEmployeeDto): Map[String, String] = {
    List(
      isNotEmpty("firstName", dto.firstName),
      isNotEmpty("lastName", dto.lastName),
//      isNotEmpty("email", dto.email).orElse(isValidValue("email", dto.email, """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r)),
      isNotEmpty("mobileNumber", dto.mobileNumber).orElse(isValidValue("mobileNumber", dto.mobileNumber, """^\+?[0-9]{7,15}$""".r)),
      isNotEmpty("address", dto.address),
    ).flatten.toMap
  }

  def validatePatch(dto: UpdateEmployeeDto): Map[String, String] = {
    List(
      isNonBlankIfDefined("firstName", dto.firstName),
      isNonBlankIfDefined("lastName", dto.lastName),
      dto.email.flatMap(isValidValue("email", _, """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r)),
      dto.mobileNumber.flatMap(isValidValue("mobileNumber", _, """^\+?[0-9]{7,15}$""".r)),
      isNonBlankIfDefined("address", dto.address)
    ).flatten.toMap
  }
}
