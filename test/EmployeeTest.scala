import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._

class EmployeeTest extends PlaySpec with GuiceOneAppPerTest with Injecting {

  // GET /employees
  "EmployeeController GET /employees" should {

    "return seeded employees as JSON" in {
      val request = FakeRequest(GET, "/employees")
      val result = route(app, request).get

      // Response returns 200 OK
      status(result) mustBe OK

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Is a valid JSON array
      json.validate[JsArray].isSuccess mustBe true

      // Matches seeded data length
      val employees = json.as[JsArray].value
      employees.length mustBe 2

      // First piece of data is as expected
      val first = employees.head
      (first \ "firstName").as[String] mustBe "John"
      (first \ "lastName").as[String] mustBe "Doe"
      (first \ "email").as[String] mustBe "john.doe@example.com"
      (first \ "mobileNumber").as[String] mustBe "1234567890"
      (first \ "address").as[String] mustBe "123 Main Street"
      (first \ "createdAt").asOpt[String] must not be empty
      (first \ "updatedAt").asOpt[String] must not be empty
    }
  }

  // GET /employees/:id
  "EmployeeController GET /employees/:id" should {

    "return correct employee as JSON if found" in {
      val request = FakeRequest(GET, "/employees/2")
      val result = route(app, request).get

      // Response returns 200 OK
      status(result) mustBe OK

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Returns the correct data
      (json \ "id").as[Long] mustBe 2L
      (json \ "firstName").as[String] mustBe "May"
      (json \ "lastName").as[String] mustBe "Jupp"
      (json \ "email").as[String] mustBe "may.jupp@example.com"
      (json \ "mobileNumber").as[String] mustBe "0987654321"
      (json \ "address").as[String] mustBe "456 Oak Avenue"
      (json \ "createdAt").asOpt[String] must not be empty
      (json \ "updatedAt").asOpt[String] must not be empty
    }

    "return 404 if employee not found" in {
      val request = FakeRequest(GET, "/employees/9999")
      val result = route(app, request).get

      // Response returns 404 NOT_FOUND
      status(result) mustBe NOT_FOUND

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)
      (json \ "error").as[String] must include("not found")
    }

    "return 400 Bad Request for non-numeric ID" in {
      val request = FakeRequest(GET, "/employees/!!!")
      val result = route(app, request).get

      // Response returns 400 BAD_REQUEST
      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").asOpt[String] must not be empty
    }
  }

  "EmployeeController POST /employees" should {

    "create a new employee and return JSON" in {
      val payload = Json.obj(
        "firstName" -> "Izzy",
        "lastName" -> "Reel",
        "email" -> "izzy.reel@example.com",
        "mobileNumber" -> "5432167890",
        "address" -> "789 Elm Street"
      )

      val request = FakeRequest(POST, "/employees")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(payload)

      val result = route(app, request).get

      // Response returns 201 CREATED
      status(result) mustBe CREATED

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Returns the correct data
      (json \ "firstName").as[String] mustBe "Izzy"
      (json \ "lastName").as[String] mustBe "Reel"
      (json \ "email").as[String] mustBe "izzy.reel@example.com"
      (json \ "mobileNumber").as[String] mustBe "5432167890"
      (json \ "address").as[String] mustBe "789 Elm Street"
      (json \ "createdAt").asOpt[String] must not be empty
      (json \ "updatedAt").asOpt[String] must not be empty
    }

    "return 400 Bad Request if payload is invalid" in {
      val badPayload = Json.obj(
        "firstName" -> "Missy",
        "lastName" -> "Linfo"
      )

      val request = FakeRequest(POST, "/employees")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(badPayload)

      val result = route(app, request).get

      // Response returns 400 BAD_REQUEST
      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").asOpt[String] must not be empty
    }
  }

  "EmployeeController PATCH /employees" should {

    "update an existing employee and return JSON" in {
      val payload = Json.obj(
        "firstName" -> "Jon Jon",
        "email" -> "jonjon.doe@example.com",
      )

      val request = FakeRequest(PATCH, "/employees/1")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(payload)

      val result = route(app, request).get

      // Response returns 200 OK
      status(result) mustBe OK

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Employee is updated correctly
      (json \ "id").as[Long] mustBe 1
      (json \ "firstName").as[String] mustBe "Jon Jon"
      (json \ "email").as[String] mustBe "jonjon.doe@example.com"
    }

    "return 404 if employee is not found" in {
      val payload = Json.obj("firstName" -> "Ghost")

      val request = FakeRequest(PATCH, "/employees/9999")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(payload)

      val result = route(app, request).get

      // Response returns 404 NOT_FOUND
      status(result) mustBe NOT_FOUND

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").as[String] must include("not found")
    }

    "return 400 Bad Request if payload is invalid" in {
      val badPayload = Json.obj("email" -> "")

      val request = FakeRequest(PATCH, "/employees/1")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(badPayload)

      val result = route(app, request).get

      // Response returns 400
      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").asOpt[String] must not be empty
    }

    "return 400 Bad Request if ID is invalid" in {
      val badPayload = Json.obj("firstName" -> "Test")

      val request = FakeRequest(PATCH, "/employees/ABC")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(badPayload)

      val result = route(app, request).get

      // Response returns 404 NOT_FOUND
      status(result) mustBe BAD_REQUEST

      // Response is HTML
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Bad Request")
    }
  }
}