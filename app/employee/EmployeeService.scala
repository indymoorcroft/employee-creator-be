package employee

import employee.dtos.{CreateEmployeeDto, EmployeeResponse, UpdateEmployeeDto}
import employee.models.Employee
import employee.validation.EmployeeValidator
import utils.ApiError

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeService @Inject()(employeeRepository: EmployeeRepository)(implicit ec: ExecutionContext) {

  def getEmployees: Future[Seq[EmployeeResponse]] = {
    employeeRepository.findAll().map(_.map(EmployeeResponse.fromModel))
  }

  def getEmployeeById(id: Long): Future[Either[ApiError, EmployeeResponse]] = {
    employeeRepository.findById(id).map {
      case Some(employee) => Right(EmployeeResponse.fromModel(employee))
      case None => Left(ApiError.NotFound(s"Employee with id $id not found"))
    }
  }

  def createEmployee(data: CreateEmployeeDto): Future[Either[ApiError, EmployeeResponse]] = {
    val errors = EmployeeValidator.validateCreate(data)
    if(errors.nonEmpty) {
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {

      val preSaved = Employee(
        id = None,
        firstName = data.firstName.trim,
        lastName = data.lastName.trim,
        email = data.email.trim,
        mobileNumber = data.mobileNumber.trim,
        address = data.address.trim,
        createdAt = now(),
        updatedAt = now()
      )

      employeeRepository.create(preSaved).map(saved => Right(EmployeeResponse.fromModel(saved)))
    }
  }

  def updateEmployee(id: Long, data: UpdateEmployeeDto): Future[Either[ApiError, EmployeeResponse]] = {
    val errors = EmployeeValidator.validatePatch(data)

    if(errors.nonEmpty){
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      employeeRepository.findById(id).flatMap {
        case None => Future.successful(Left(ApiError.NotFound(s"Employee with id $id not found")))
        case Some(existing) =>
          val updated = existing.copy(
            firstName = data.firstName.map(_.trim).getOrElse(existing.firstName),
            lastName = data.lastName.map(_.trim).getOrElse(existing.lastName),
            email = data.email.map(_.trim).getOrElse(existing.email),
            mobileNumber = data.mobileNumber.map(_.trim).getOrElse(existing.mobileNumber),
            address = data.address.map(_.trim).getOrElse(existing.address),
            updatedAt = now()
          )

          employeeRepository.update(updated).map(e => Right(EmployeeResponse.fromModel(e)))
      }
    }
  }

  def deleteEmployee(id: Long): Future[Either[ApiError, Unit]] = {
    employeeRepository.delete(id).map { rowsAffected =>
      if(rowsAffected > 0) Right(())
      else Left(ApiError.NotFound(s"Employee with id $id not found"))
    }
  }

  private def now(): Timestamp = Timestamp.from(Instant.now())
}