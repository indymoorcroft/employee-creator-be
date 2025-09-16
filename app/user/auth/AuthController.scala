package user.auth

import play.api.libs.json._
import play.api.mvc._
import user.UserService
import user.dtos.{LoginRequest, RegisterRequest}
import user.models.AuthToken
import utils.ApiError

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthController @Inject()(
                                cc: ControllerComponents,
                                userService: UserService,
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  implicit val loginRequestFormat: Format[LoginRequest] = Json.format[LoginRequest]
  implicit val authTokenFormat: Format[AuthToken] = Json.format[AuthToken]

  def register: Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[RegisterRequest].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto => userService.register(dto).map {
        case Right(response) => Created(Json.toJson(response))
        case Left(error)     => error.toResult
      }
    )
  }

  def login: Action[JsValue] = Action.async(parse.json) { implicit request =>
    request.body.validate[LoginRequest].fold(
      errors => Future.successful(ApiError.InvalidJson(JsError(errors)).toResult),
      dto =>
        userService.login(dto).map {
          case Right(token) =>
            val expiresAt = System.currentTimeMillis() / 1000 + 3600
            Ok(Json.toJson(AuthToken(token, expiresAt)))
          case Left(error) =>
            error.toResult
        }
    )
  }

}