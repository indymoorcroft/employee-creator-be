package utils

import scala.util.matching.Regex

trait Validator {
  def isNotEmpty(fieldName: String, value: String): Option[(String, String)] =
    if (value.trim.isEmpty) Some(fieldName -> s"${camelCaseToNormal(fieldName)} cannot be empty")
    else None

  def isNonBlankIfDefined(fieldName: String, value: Option[_]): Option[(String, String)] =
    value match {
      case Some(v) if v.toString.trim.isEmpty => Some(fieldName -> s"${camelCaseToNormal(fieldName)} cannot be blank")
      case _ => None
    }

  def isCorrectValue(fieldName: String, value: String, validValues: Set[String]): Option[(String, String)] = {
    if (validValues.contains(value)) None
    else Some(fieldName -> s"invalid ${camelCaseToNormal(fieldName)}")
  }

  def isCorrectValueIfDefined(fieldName: String, valueOpt: Option[String], validValues: Set[String]): Option[(String, String)] = {
    valueOpt match {
      case Some(value) if !validValues.contains(value) =>
        Some(fieldName -> s"invalid ${camelCaseToNormal(fieldName)}, must be one of: ${validValues.mkString(", ")}")
      case _ => None
    }
  }

  def isValidValue(field: String, value: String, regex: Regex): Option[(String, String)] = {
    if (regex.matches(value)) None
    else Some(field -> s"Please enter a valid ${camelCaseToNormal(field)}")
  }

  private def camelCaseToNormal(str: String): String = {
    if (str == null || str.isEmpty) return str
    val withSpaces = str.replaceAll("([A-Z])", " $1").trim
    withSpaces.head.toUpper + withSpaces.tail
  }

}