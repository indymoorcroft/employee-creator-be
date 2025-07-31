package contract

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import contract.Table.contracts
import contract.models.Contract

@Singleton
class ContractRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._

  def findById(id: Long): Future[Option[Contract]] = {
    db.run(contracts.filter(_.id === id).result.headOption)
  }

  def findByEmployeeId(id: Long): Future[Seq[Contract]] = {
    db.run(contracts.filter(_.employeeId === id).result)
  }

  def create(contract: Contract): Future[Contract] = {
    val insertQuery = contracts returning contracts.map(_.id) into ((contract, id) => contract.copy(id = Some(id)))
    db.run(insertQuery += contract)
  }
}