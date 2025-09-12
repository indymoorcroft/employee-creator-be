package user.validation

import user.dtos.RegisterRequest
import utils.Validator

object UserValidator extends Validator {

  def validateRegistration(dto: RegisterRequest): Map[String, String] = {
    List(
      isNotEmpty("email", dto.email).orElse(isValidValue("email", dto.email, """^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$""".r)),
      isNotEmpty("password", dto.password).orElse(isValidValue("password", dto.password, """^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$""".r
      )),
    ).flatten.toMap
  }
}
