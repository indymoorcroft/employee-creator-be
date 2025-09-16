package user

import org.mindrot.jbcrypt.BCrypt
import user.auth.AuthService
import user.dtos.{LoginRequest, RegisterRequest, UserResponse}
import user.validation.UserValidator
import user.models.User
import utils.ApiError

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserService @Inject()(userRepository: UserRepository, jwtService: AuthService)(implicit ec: ExecutionContext) {

  def register(data: RegisterRequest): Future[Either[ApiError, UserResponse]] = {
    val errors = UserValidator.validateRegistration(data)

    if(errors.nonEmpty){
      Future.successful(Left(ApiError.ValidationError(errors)))
    } else {
      userRepository.findByEmail(data.email).flatMap {
        case Some(_) =>
          Future.successful(Left(ApiError.ValidationError(Map("email" -> "Email already in use"))))

        case None =>
          val now = Timestamp.from(Instant.now())
          val hashedPassword = BCrypt.hashpw(data.password, BCrypt.gensalt())
          val user = User(None, data.email, hashedPassword, now, now)

          userRepository.create(user).map { saved =>
            Right(UserResponse.fromModel(saved))
          }
      }
    }
  }

  def login(data: LoginRequest): Future[Either[ApiError, String]] = {
    userRepository.findByEmail(data.email).map {
      case Some(user) if BCrypt.checkpw(data.password, user.passwordHash) =>
        val token = jwtService.generateToken(user.id.get, user.email)
        Right(token)
      case _ =>
        Left(ApiError.Unauthorized("Email not recognised"))
    }
  }
}
