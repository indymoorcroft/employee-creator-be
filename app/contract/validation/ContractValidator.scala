package contract.validation

import contract.dtos.CreateContractDto
import utils.Validator

object ContractValidator extends Validator {
  def validateCreate(dto: CreateContractDto): Map[String, String] = {
    List(
      isNotEmpty("startDate", dto.startDate.toString),
      isNotEmpty("endDate", dto.endDate.toString),
      isCorrectValue("contractType", dto.contractType, Set("PERMANENT", "CONTRACT")),
      isCorrectValue("employmentType", dto.employmentType, Set("FULL_TIME", "PART_TIME")),
      isNotEmpty("hoursPerWeek", dto.hoursPerWeek.toString())
    ).flatten.toMap
  }
}
