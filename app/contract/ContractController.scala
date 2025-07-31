package contract

import play.api.libs.json.Json
import play.api.mvc._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContractController @Inject()(cc: ControllerComponents, contractService: ContractService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def getContractById(id: Long) : Action[AnyContent] = Action.async {
    contractService.getContractById(id).map {
      case Right(contract) => Ok(Json.toJson(contract))
      case Left(error) => error.toResult
    }
  }
}
