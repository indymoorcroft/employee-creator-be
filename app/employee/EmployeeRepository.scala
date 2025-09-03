package employee

import contract.Table.contracts
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.jdbc.MySQLProfile.api._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import employee.Table.employees
import employee.models.Employee

import java.sql.Date
import java.time.{LocalDate, YearMonth}

@Singleton
class EmployeeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._

  def findAll(name: Option[String],contractType: Option[String], expiry: Boolean = false): Future[Seq[Employee]] = {

    val expiring = if (expiry) {
      val ym = YearMonth.from(LocalDate.now())
      val startOfMonth = Date.valueOf(ym.atDay(1))
      val endOfMonth = Date.valueOf(ym.atEndOfMonth())

      for {
        (e, c) <- employees join contracts on (_.id === _.employeeId)
        if (c.endDate.isDefined && c.endDate >= startOfMonth && c.startDate <= endOfMonth)
      } yield e
    } else {
      employees
    }

    val nameFilter = name match {
      case Some(name) =>
        val pattern = s"%${name.toLowerCase}%"
        expiring.filter(e => e.firstName.toLowerCase.like(pattern) || e.lastName.toLowerCase.like(pattern)
        )
      case None => expiring
    }

    val query = contractType match {
      case Some("full-time") =>
        for {
          (e, c) <- nameFilter join contracts on (_.id === _.employeeId)
          if c.employmentType === "FULL_TIME"
        } yield e

      case Some("part-time") =>
        for {
          (e, c) <- nameFilter join contracts on (_.id === _.employeeId)
          if c.employmentType === "PART_TIME"
        } yield e

      case _ => nameFilter
    }

    db.run(query.sortBy(_.updatedAt.desc).result)
  }

  def findById(id: Long): Future[Option[Employee]] = {
    db.run(employees.filter(_.id === id).result.headOption)
  }

  def create(employee: Employee): Future[Employee] = {
    val insertQuery = employees returning employees.map(_.id) into ((employee, id) => employee.copy(id = Some(id)))
    db.run(insertQuery += employee)
  }

  def update(employee: Employee): Future[Employee] = {
    val query = employees.filter(_.id === employee.id.get)
      .map(e => (e.firstName, e.lastName, e.email, e.mobileNumber, e.address, e.updatedAt))
      .update((employee.firstName, employee.lastName, employee.email, employee.mobileNumber, employee.address, employee.updatedAt))

    db.run(query).map(_ => employee)
  }

  def delete(id: Long): Future[Int] = {
    db.run(employees.filter(_.id === id).delete)
  }
}