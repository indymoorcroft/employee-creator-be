package contract.dtos

import contract.models.Contract
import play.api.libs.json._

import java.time.{LocalDate, LocalDateTime}

case class ContractResponse(id: Long, employeeId: Long, startDate: LocalDate, endDate: Option[LocalDate], contractType: String, employmentType: String, hoursPerWeek: BigDecimal,createdAt: LocalDateTime, updatedAt: LocalDateTime)

object ContractResponse {
  implicit val format: OFormat[ContractResponse] = Json.format[ContractResponse]

  def fromModel(model: Contract): ContractResponse = {
    ContractResponse(id = model.id.getOrElse(0L), employeeId = model.employeeId, startDate = model.startDate.toLocalDate, endDate = model.endDate.map(_.toLocalDate), contractType = model.contractType, employmentType = model.employmentType, hoursPerWeek = model.hoursPerWeek, createdAt = model.createdAt.toLocalDateTime, updatedAt = model.updatedAt.toLocalDateTime)
  }
}
