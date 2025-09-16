package api

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import api.utils.CleanDatabase

class UserTest extends PlaySpec with GuiceOneAppPerSuite with Injecting with CleanDatabase {

  // POST /auth/register
  "AuthController POST /auth/register" should {

    "register a new user successfully" in {
      val request = FakeRequest(POST, "/auth/register")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.obj("email" -> "testuser@example.com", "password"-> "Password123!"))

      val result = route(app, request).get

      // Response returns 201 CREATED
      status(result) mustBe CREATED

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)
      (json \ "email").as[String] mustBe "testuser@example.com"
      (json \ "id").asOpt[Long] must not be empty
    }

    "return 400 Bad Request if email is invalid" in {
      val request = FakeRequest(POST, "/auth/register")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.obj("email" -> "wrong.com", "password"-> "Password123!"))

      val result = route(app, request).get

      // Response returns 400 Bad Request
      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").asOpt[String] must not be empty
      (json \ "validation_errors" \ "email").as[String] mustBe "Please enter a valid Email"
    }

    "return 400 Bad Request if password is invalid" in {
      val request = FakeRequest(POST, "/auth/register")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.obj("email" -> "test@example.com", "password"-> "invalid"))

      val result = route(app, request).get

      // Response returns 400 Bad Request
      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").asOpt[String] must not be empty
      (json \ "validation_errors" \ "password").as[String] mustBe "Please enter a valid Password"
    }

    "return 400 Bad Request if email is already in use" in {
      val registerRequestOne = FakeRequest(POST, "/auth/register")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.obj("email" -> "test@example.com", "password"-> "Password123!"))

      await(route(app, registerRequestOne).get)

      val registerRequestTwo = FakeRequest(POST, "/auth/register")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(Json.obj("email" -> "test@example.com", "password"-> "Password123!"))

      val result = route(app, registerRequestTwo).get

      // Response returns 400 Bad Request
      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").asOpt[String] must not be empty
      (json \ "validation_errors" \ "email").as[String] mustBe "Email already in use"
    }
  }

  // POST /auth/login
  "AuthController POST /auth/login" should {

    "login an existing user successfully" in {
    val registerRequest = FakeRequest(POST, "/auth/register")
      .withHeaders("Content-Type" -> "application/json")
      .withJsonBody(Json.obj("email" -> "testuser@example.com", "password"-> "password123"))

      await(route(app, registerRequest).get)

    val loginRequest = FakeRequest(POST, "/auth/login")
      .withHeaders("Content-Type" -> "application/json")
      .withJsonBody(Json.obj("email" -> "testuser@example.com", "password"-> "password123"))

    val result = route(app, loginRequest).get

    // Response returns 200 OK
    status(result) mustBe OK

    // Response returns JSON
    contentType(result) mustBe Some("application/json")

    val json = contentAsJson(result)
    (json \ "token").as[String] must not be empty
    (json \ "expiresAt").as[Long] must be > 0L
    }
  }

  "rejects login with invalid credentials" in {
    val loginRequest = FakeRequest(POST, "/auth/login")
      .withHeaders("Content-Type" -> "application/json")
      .withJsonBody(Json.obj("email" -> "wrong@example.com", "password"-> "password123"))

    val result = route(app, loginRequest).get

    status(result) mustBe UNAUTHORIZED
  }
}
