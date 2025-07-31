package contract

import models.Contract
import contract.dtos.{ContractResponse, CreateContractDto}
import contract.validation.ContractValidator
import utils.ApiError

import java.sql.Timestamp
import java.time.Instant
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

  def getEmployeeContracts(id: Long): Future[Either[ApiError, Seq[ContractResponse]]] = {
    contractRepository.findByEmployeeId(id).map { contracts =>
      if(contracts.nonEmpty){
        Right(contracts.map(contract => ContractResponse.fromModel(contract)))
      } else {
        Left(ApiError.NotFound("This employee does not have any contracts"))
      }
    }
  }

  def createContract(employeeId: Long, data: CreateContractDto): Future[Either[ApiError, ContractResponse]] = {
    val errors = ContractValidator.validateCreate(data)
    if(errors.nonEmpty){
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      val now = Timestamp.from(Instant.now())

      val contract = Contract(
        id = None,
        employeeId = employeeId,
        startDate = java.sql.Date.valueOf(data.startDate),
        endDate = data.endDate.map(java.sql.Date.valueOf),
        contractType = data.contractType,
        employmentType = data.employmentType,
        hoursPerWeek = data.hoursPerWeek,
        createdAt = now,
        updatedAt = now
      )

      contractRepository.create(contract).map { created =>
        Right(ContractResponse.fromModel(created))
      }
    }
  }
}