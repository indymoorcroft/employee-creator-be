package contract

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import contract.Table.contracts
import contract.models.Contract

import java.sql.Date

@Singleton
class ContractRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._

  def findById(id: Long): Future[Option[Contract]] = {
    db.run(contracts.filter(_.id === id).result.headOption)
  }

  def findByEmployeeId(id: Long): Future[Seq[Contract]] = {
    db.run(contracts.filter(_.employeeId === id).sortBy(_.updatedAt.desc).result)
  }

  def create(contract: Contract): Future[Contract] = {
    val insertQuery = contracts returning contracts.map(_.id) into ((contract, id) => contract.copy(id = Some(id)))
    db.run(insertQuery += contract)
  }

  def hasOverlap(employeeId: Long, start: Date, end: Option[Date]): Future[Boolean] = {
    val query = contracts.filter { c =>
      c.employeeId === employeeId &&
        c.startDate <= end &&
        c.endDate >= start
    }.exists
    db.run(query.result)
  }

  def update(contract: Contract): Future[Contract] = {
    val query = contracts.filter(_.id === contract.id.get)
      .map(c => (c.startDate, c.endDate, c.contractType, c.employmentType, c.hoursPerWeek, c.updatedAt))
      .update((contract.startDate, contract.endDate, contract.contractType, contract.employmentType, contract.hoursPerWeek, contract.updatedAt))

    db.run(query).map(_ => contract)
  }

  def delete(id: Long): Future[Int] = {
    db.run(contracts.filter(_.id === id).delete)
  }
}