package seed

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import employee.Table.employees
import employee.models.Employee

import java.sql.Timestamp
import java.time.Instant

@Singleton
class DataSeeder @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  def seed(): Future[Unit] = {
    val now = Timestamp.from(Instant.now())

    val initialCategories = Seq(
      Employee(None, "John", "Doe", "john.doe@example.com", "1234567890", "123 Main Street", now, now),
      Employee(None, "May", "Jupp", "may.jupp@example.com", "0987654321", "456 Oak Avenue", now, now)
    )


    val insertIfEmpty = for {
      exists <- employees.exists.result
      _ <- if (!exists) employees ++= initialCategories else DBIO.successful(())
    } yield ()

    db.run(insertIfEmpty.transactionally)
  }
}