package employee

import employee.dtos.{CreateEmployeeDto, UpdateEmployeeDto}
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeController @Inject()(cc: ControllerComponents, employeeService: EmployeeService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllEmployees: Action[AnyContent] = Action.async { request =>
    val name: Option[String] = request.getQueryString("name")
    val contractType: Option[String] = request.getQueryString("contractType")
    val expiry = request.getQueryString("expiry").contains("true")

    employeeService.getEmployees(name, contractType, expiry).map { employees =>
      Ok(Json.toJson(employees))
    }
  }

  def getEmployeeById(id: Long) : Action[AnyContent] = Action.async {
    employeeService.getEmployeeById(id).map {
      case Right(employee) => Ok(Json.toJson(employee))
      case Left(error) => error.toResult
    }
  }

  def postEmployee: Action[JsValue] = Action.async(parse.json) { request =>
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

  def patchEmployee(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UpdateEmployeeDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => employeeService.updateEmployee(id, dto).map {
        case Right(response) => Ok(Json.toJson(response))
        case Left(error) => error.toResult
      }
    )
  }

  def deleteEmployee(id: Long): Action[AnyContent] = Action.async {
    employeeService.deleteEmployee(id).map {
      case Right(_)     => NoContent
      case Left(error)  => error.toResult
    }
  }
}
