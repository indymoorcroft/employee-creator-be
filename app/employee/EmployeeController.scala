package employee

import play.api.libs.json.Json
import play.api.mvc._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EmployeeController @Inject()(cc: ControllerComponents, employeeService: EmployeeService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getAllEmployees: Action[AnyContent] = Action.async {
    employeeService.getAllEmployees().map { employees =>
      Ok(Json.toJson(employees))
    }
  }

}
