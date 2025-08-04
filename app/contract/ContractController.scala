package contract

import contract.dtos.{CreateContractDto, UpdateContractDto}
import employee.EmployeeRepository
import play.api.libs.json.{JsError, JsValue, Json}
import play.api.mvc._
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContractController @Inject()(cc: ControllerComponents, contractService: ContractService, employeeRepository: EmployeeRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getContractById(id: Long) : Action[AnyContent] = Action.async {
    contractService.getContractById(id).map {
      case Right(contract) => Ok(Json.toJson(contract))
      case Left(error) => error.toResult
    }
  }

  def patchContractById(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[UpdateContractDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => contractService.updateContractById(id, dto).map {
        case Right(response) => Ok(Json.toJson(response))
        case Left(error) => error.toResult
      }
    )
  }

  def getEmployeeContractById(id: Long): Action[AnyContent] = Action.async {
    employeeRepository.findById(id).flatMap {
      case None => Future.successful(ApiError.NotFound("Employee not found").toResult)
      case Some(_) =>
        contractService.getEmployeeContracts(id).map {
          case Right(contracts) => Ok(Json.toJson(contracts))
          case Left(error) => error.toResult
        }
    }
  }

  def addContractForEmployee(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[CreateContractDto].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      contractData => {
        employeeRepository.findById(id).flatMap {
          case Some(_) =>
            contractService.createContract(id, contractData).map {
              case Right(contract) => Created(Json.toJson(contract))
              case Left(error) => error.toResult
            }
          case None =>
            Future.successful(NotFound(Json.obj("error" -> s"Employee with ID $id not found")))
        }
      }
    )
  }

  def deleteContractById(id: Long): Action[AnyContent] = Action.async {
    contractService.deleteContractById(id).map {
      case Right(_) => NoContent
      case Left(error) => error.toResult
    }
  }
}
