package seed

import slick.jdbc.JdbcProfile
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import employee.Table.employees
import employee.models.Employee
import contract.Table.contracts
import contract.models.Contract

import java.sql.{Date, Timestamp}
import java.time.{Instant, LocalDate}

@Singleton
class DataSeeder @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  def seed(): Future[Unit] = {
    val now = Timestamp.from(Instant.now())

    val initialEmployees = Seq(
      Employee(None, "John", "Doe", "john.doe@example.com", "1234567890", "123 Main Street", now, now),
      Employee(None, "May", "Jupp", "may.jupp@example.com", "0987654321", "456 Oak Avenue", now, now),
      Employee(None, "Paul", "Crest", "paul.crest@example.com", "11029384756", "789 Real Road", now, now)
    )

    val setup = for {
      employeesExist <- employees.exists.result
      contractsExist <- contracts.exists.result

      employeeIds <- if(!employeesExist){
        val insertQuery = employees returning employees.map(_.id) into ((emp, id) => emp.copy(id = Some(id)))
        insertQuery ++= initialEmployees
      } else {
        employees.result
      }

      empMap: Map[String, Long] = employeeIds.map(emp => emp.email -> emp.id.get).toMap

      _ <- if (!contractsExist) {
        val initialContracts = Seq(
          Contract(None, empMap("john.doe@example.com"), Date.valueOf(LocalDate.of(2023, 1, 1)), None, "PERMANENT", "FULL_TIME", BigDecimal(37.5), now, now),
          Contract(None, empMap("may.jupp@example.com"), Date.valueOf(LocalDate.of(2024, 5, 1)), Some(Date.valueOf(LocalDate.of(2025, 4, 30))), "CONTRACT", "PART_TIME", BigDecimal(20.0), now, now)
        )
        contracts ++= initialContracts
      } else {
        DBIO.successful(())
      }
    } yield ()

    db.run(setup.transactionally)
  }
}