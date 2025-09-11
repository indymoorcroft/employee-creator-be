package user

import org.mindrot.jbcrypt.BCrypt
import user.auth.AuthService
import user.dtos.{LoginRequest, RegisterRequest, UserResponse}
import user.models.User
import utils.ApiError

import java.sql.Timestamp
import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class UserService @Inject()(userRepository: UserRepository, jwtService: AuthService)(implicit ec: ExecutionContext) {

//  def findByEmail(email: String): Future[Option[User]] = {
//    userRepository.findByEmail(email)
//  }
//
//  def createUser(email: String, rawPassword: String): Future[User] = {
//    val now = Timestamp.from(Instant.now())
//    val hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt())
//
//    val user = User(
//      id = None,
//      email = email,
//      passwordHash = hashedPassword,
//      createdAt = now,
//      updatedAt = now
//    )
//
//    userRepository.create(user)
//  }

  def register(dto: RegisterRequest): Future[Either[ApiError, UserResponse]] = {
    userRepository.findByEmail(dto.email).flatMap {
      case Some(_) =>
        Future.successful(Left(ApiError.ValidationError(Map("email" -> "Email already in use"))))

      case None =>
        val now = Timestamp.from(Instant.now())
        val hashedPassword = BCrypt.hashpw(dto.password, BCrypt.gensalt())
        val user = User(None, dto.email, hashedPassword, now, now)

        userRepository.create(user).map { saved =>
          Right(UserResponse.fromModel(saved))
        }
    }
  }

  def login(dto: LoginRequest): Future[Either[ApiError, String]] = {
    userRepository.findByEmail(dto.email).map {
      case Some(user) if BCrypt.checkpw(dto.password, user.passwordHash) =>
        val token = jwtService.generateToken(user.id.get, user.email)
        Right(token)
      case _ =>
        Left(ApiError.Unauthorized("Invalid credentials"))
    }
  }
}
