package employee

import employee.dtos.{CreateEmployeeDto, UpdateEmployeeDto}
import play.api.libs.json.{JsError, JsSuccess, Json}
import play.api.mvc._
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeController @Inject()(cc: ControllerComponents, employeeService: EmployeeService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllEmployees: Action[AnyContent] = Action.async {
    employeeService.getAllEmployees().map { employees =>
      Ok(Json.toJson(employees))
    }
  }

  def getEmployeeById(id: Long) : Action[AnyContent] = Action.async {
    employeeService.getEmployeeById(id).map {
      case Right(employee) => Ok(Json.toJson(employee))
      case Left(error) => error.toResult
    }
  }

  def postEmployee = Action.async(parse.json) { request =>
    request.body.validate[CreateEmployeeDto] match {
      case JsSuccess(dto, _) =>
        employeeService.createEmployee(dto).map {
          case Right(response) => Created(Json.toJson(response))
          case Left(error)     => error.toResult
        }

      case e: JsError =>
        Future.successful(ApiError.InvalidJson(e).toResult)
    }
  }

  def patchEmployeeById(id: Long) = Action.async(parse.json) { request =>
    request.body.validate[UpdateEmployeeDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => employeeService.updateEmployeeById(id, dto).map {
        case Right(response) => Ok(Json.toJson(response))
        case Left(error) => error.toResult
      }
    )
  }

  def deleteById(id: Long) = Action.async {
    employeeService.deleteEmployeeById(id).map {
      case Right(_)     => NoContent
      case Left(error)  => error.toResult
    }
  }
}
