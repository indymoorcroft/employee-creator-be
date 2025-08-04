package contract.dtos

import play.api.libs.json._

import java.time.LocalDate

case class UpdateContractDto(startDate: Option[LocalDate], endDate: Option[LocalDate], contractType: Option[String], employmentType: Option[String], hoursPerWeek: Option[BigDecimal])

object UpdateContractDto {
  implicit val reads: Reads[UpdateContractDto] = Json.reads[UpdateContractDto]
}