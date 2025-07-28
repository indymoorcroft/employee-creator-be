package employee

import employee.dtos.EmployeeResponse
import utils.ApiError

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
}