package contract

import slick.jdbc.MySQLProfile.api._
import contract.models._
import java.sql.{Date, Timestamp}

class Contracts(tag: Tag) extends Table[Contract](tag, "contracts") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def employeeId = column[Long]("employee_id")
  def startDate = column[Date]("start_date")
  def endDate = column[Option[Date]]("end_date")
  def contractType = column[String]("contract_type")
  def employmentType = column[String]("employment_type")
  def hoursPerWeek = column[BigDecimal]("hours_per_week")
  def createdAt = column[Timestamp]("created_at")
  def updatedAt = column[Timestamp]("updated_at")

  def * = (id.?, employeeId, startDate, endDate, contractType, employmentType, hoursPerWeek, createdAt, updatedAt) <>
    (Contract.tupled, Contract.unapply)
}

object Table {
  val contracts = TableQuery[Contracts]
}
