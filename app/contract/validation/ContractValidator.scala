package contract.validation

import contract.dtos.{CreateContractDto, UpdateContractDto}
import utils.Validator

import java.time.LocalDate

object ContractValidator extends Validator {

  val validContractTypes = Set("PERMANENT", "CONTRACT")
  val validEmploymentTypes = Set("FULL_TIME", "PART_TIME")

  def validateCreate(dto: CreateContractDto): Map[String, String] = {
    List(
      isNotEmpty("startDate", dto.startDate.toString),
      isNotEmpty("endDate", dto.endDate.toString),
      isCorrectValue("contractType", dto.contractType, validContractTypes),
      isCorrectValue("employmentType", dto.employmentType, validEmploymentTypes),
      isNotEmpty("hoursPerWeek", dto.hoursPerWeek.toString),
      validateDateRange(dto.startDate, dto.endDate)
    ).flatten.toMap
  }

  def validatePatch(dto: UpdateContractDto): Map[String, String] = {
    List(
    isNonBlankIfDefined("startDate", dto.startDate),
    isNonBlankIfDefined("endDate", dto.endDate),
    isCorrectValueIfDefined("contractType", dto.contractType, validContractTypes),
      isCorrectValueIfDefined("employmentType", dto.employmentType, validEmploymentTypes),
      isNonBlankIfDefined("hoursPerWeek", dto.hoursPerWeek),
      validateDateRange(dto.startDate, dto.endDate)
    ).flatten.toMap
  }

  def validateDateRange(startDate: LocalDate, endDate: Option[LocalDate]): Option[(String, String)] = {
    endDate match {
      case (Some(end)) if end.isBefore(startDate) => Some("dateRange" -> "endDate must be after startDate")
      case _ => None
    }
  }

  def validateDateRange(startDate: Option[LocalDate], endDate: Option[LocalDate]): Option[(String, String)] = {
    (startDate, endDate) match {
      case (Some(start), Some(end)) if end.isBefore(start) => Some("dateRange" -> "endDate must be after startDate")
      case _ => None
    }
  }
}
