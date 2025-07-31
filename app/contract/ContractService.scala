package contract

import contract.dtos.ContractResponse
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ContractService @Inject()(contractRepository: ContractRepository)(implicit ec: ExecutionContext) {

  def getContractById(id: Long): Future[Either[ApiError, ContractResponse]] = {
    contractRepository.findById(id).map {
      case Some(contract) => Right(ContractResponse.fromModel(contract))
      case None => Left(ApiError.NotFound(s"Contract with id $id not found"))
    }
  }
}