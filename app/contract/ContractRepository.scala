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
}