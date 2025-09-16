package user.actions

import play.api.mvc._
import user.auth.AuthService

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
class AuthenticatedAction @Inject()(parser: BodyParsers.Default, jwtService: AuthService)(implicit ec: ExecutionContext) extends ActionBuilder[AuthenticatedRequest, AnyContent] {

  override def executionContext: ExecutionContext = ec
  override def parser: BodyParser[AnyContent] = parser

  override def invokeBlock[A](request: Request[A], block: AuthenticatedRequest[A] => Future[Result]): Future[Result] = {
    extractToken(request) match {
      case Some(token) =>
        jwtService.validateToken(token) match {
          case Success(userId) =>
            val authenticatedRequest = AuthenticatedRequest(userId, request)
            block(authenticatedRequest)
          case Failure(_) =>
            Future.successful(Results.Unauthorized("Invalid token"))
        }
      case None =>
        Future.successful(Results.Unauthorized("Missing token"))
    }
  }

  private def extractToken(request: RequestHeader): Option[String] = {
    request.headers.get("Authorization") match {
      case Some(header) if header.startsWith("Bearer ") =>
        Some(header.substring(7))
      case _ => None
    }
  }
}

case class AuthenticatedRequest[A](userId: Long, request: Request[A]) extends WrappedRequest[A](request)
