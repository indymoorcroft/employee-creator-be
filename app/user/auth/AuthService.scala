package user.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import play.api.Configuration

import java.time.Instant
import javax.inject.{Inject, Singleton}
import scala.util.Try

@Singleton
class AuthService @Inject()(config: Configuration) {

  private val secret = config.get[String]("jwt.secret")
  private val algorithm = Algorithm.HMAC256(secret)
  private val issuer = config.get[String]("jwt.issuer")
  private val expiration = config.get[Int]("jwt.expiration")

  def generateToken(userId: Long, email: String): String = {
    JWT.create()
      .withIssuer(issuer)
      .withSubject(userId.toString)
      .withClaim("email", email)
      .withIssuedAt(Instant.now())
      .withExpiresAt(Instant.now().plusSeconds(expiration))
      .sign(algorithm)
  }

  def validateToken(token: String): Try[Long] = {
    Try {
      val verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build()

      val decodedJWT = verifier.verify(token)
      decodedJWT.getSubject.toLong
    }
  }
}
