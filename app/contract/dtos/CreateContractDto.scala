package contract.dtos

import play.api.libs.json._

import java.time.LocalDate

case class CreateContractDto(startDate: LocalDate, endDate: Option[LocalDate], contractType: String, employmentType: String, hoursPerWeek: BigDecimal)

object CreateContractDto {
  implicit val reads: Reads[CreateContractDto] = Json.reads[CreateContractDto]
}