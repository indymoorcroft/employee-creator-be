package employee

import employee.dtos.{CreateEmployeeDto, EmployeeResponse}
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
}