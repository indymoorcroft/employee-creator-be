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

  def getAllEmployees(): Future[Seq[EmployeeResponse]] = {
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
      val now = Timestamp.from(Instant.now())

      val preSaved = Employee(
        id = None,
        firstName = data.firstName.trim,
        lastName = data.lastName.trim,
        email = data.email.trim,
        mobileNumber = data.mobileNumber.trim,
        address = data.address.trim,
        createdAt = now,
        updatedAt = now
      )

      employeeRepository.create(preSaved).map(saved => Right(EmployeeResponse.fromModel(saved)))
    }
  }

  def updateEmployeeById(id: Long, data: UpdateEmployeeDto): Future[Either[ApiError, EmployeeResponse]] = {
    val errors = EmployeeValidator.validatePatch(data)

    if(errors.nonEmpty){
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      employeeRepository.findById(id).flatMap {
        case None => Future.successful(Left(ApiError.NotFound(s"Employee with id $id not found")))
        case Some(existing) =>
          val updates = Map(
            "firstName" -> data.firstName.map(_.trim),
            "lastName" -> data.lastName.map(_.trim),
            "email" -> data.email.map(_.trim),
            "mobileNumber" -> data.mobileNumber.map(_.trim),
            "address" -> data.address.map(_.trim)
          ).collect { case (k, Some(v)) => k -> v }

          val updated = existing.copy(
            firstName = updates.getOrElse("firstName", existing.firstName),
            lastName = updates.getOrElse("lastName", existing.lastName),
            email = updates.getOrElse("email", existing.email),
            mobileNumber = updates.getOrElse("mobileNumber", existing.mobileNumber),
            address = updates.getOrElse("address", existing.address),
            updatedAt = Timestamp.from(Instant.now())
          )

          employeeRepository.update(updated).map(e => Right(EmployeeResponse.fromModel(e)))
      }
    }
  }

  def deleteEmployeeById(id: Long): Future[Either[ApiError, Unit]] = {
    employeeRepository.delete(id).map { rowsAffected =>
      if(rowsAffected > 0) Right(())
      else Left(ApiError.NotFound(s"Employee with id $id not found"))
    }
  }
}