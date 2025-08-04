package utils

trait Validator {
  def isNotEmpty(fieldName: String, value: String): Option[(String, String)] =
    if (value.trim.isEmpty) Some(fieldName -> s"$fieldName cannot be empty")
    else None

  def isNonBlankIfDefined(fieldName: String, value: Option[_]): Option[(String, String)] =
    value match {
      case Some(v) if v.toString.trim.isEmpty => Some(fieldName -> s"$fieldName cannot be blank if provided")
      case _ => None
    }

  def isCorrectValue(fieldName: String, value: String, validValues: Set[String]): Option[(String, String)] = {
    if (validValues.contains(value)) None
    else Some(fieldName -> s"invalid $fieldName")
  }

  def isCorrectValueIfDefined(fieldName: String, valueOpt: Option[String], validValues: Set[String]): Option[(String, String)] = {
    valueOpt match {
      case Some(value) if !validValues.contains(value) =>
        Some(fieldName -> s"invalid $fieldName, must be one of: ${validValues.mkString(", ")}")
      case _ => None
    }
  }

}