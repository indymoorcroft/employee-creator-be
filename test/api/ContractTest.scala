package api

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test._
import api.utils.CleanDatabase


class ContractTest extends PlaySpec with GuiceOneAppPerSuite with Injecting with CleanDatabase {

  val patchPayload = Json.obj(
    "endDate" -> "2025-08-01"
  )
  val postPayload: JsObject = Json.obj(
    "startDate" -> "2024-01-01",
    "endDate" -> "2025-12-31",
    "contractType" -> "PERMANENT",
    "employmentType" -> "FULL_TIME",
    "hoursPerWeek" -> 37.5,
  )

  // GET /contracts/:id
  "ContractController GET /contracts/:id" should {

    "return correct contract as JSON if found" in {
      val request = FakeRequest(GET, "/contracts/1")
      val result = route(app, request).get

      // Response returns 200 OK
      status(result) mustBe OK

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Returns the correct data
      (json \ "id").as[Long] mustBe 1L
      (json \ "employeeId").as[Long] mustBe 1L
      (json \ "startDate").as[String] mustBe "2023-01-01"
      (json \ "contractType").as[String] mustBe "PERMANENT"
      (json \ "employmentType").as[String] mustBe "FULL_TIME"
      (json \ "hoursPerWeek").as[BigDecimal] mustBe 37.5
      (json \ "createdAt").asOpt[String] must not be empty
      (json \ "updatedAt").asOpt[String] must not be empty
    }

    "return 404 if employee not found" in {
      val request = FakeRequest(GET, "/contracts/9999")
      val result = route(app, request).get

      // Response returns 404 NOT_FOUND
      status(result) mustBe NOT_FOUND

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)
      (json \ "error").as[String] must include("not found")
    }

    "return 400 Bad Request for non-numeric ID" in {
      val request = FakeRequest(GET, "/contracts/ABC")
      val result = route(app, request).get

      // Response returns 400 BAD_REQUEST
      status(result) mustBe BAD_REQUEST

      // Response is HTML
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Bad Request")
    }
  }

  // GET /employees/:id/contracts
  "ContractController GET /employees/:id/contracts" should {

    "return empty array if existing employee with no contracts is found" in {
      val request = FakeRequest(GET, "/employees/3/contracts")
      val result = route(app, request).get

      // Response returns 200 OK
      status(result) mustBe OK

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Is a valid JSON array
      json.validate[JsArray].isSuccess mustBe true

      // Has data
      val employeeContracts = json.as[JsArray].value
      employeeContracts.length mustBe 0
    }

    "return contracts of the correct employee as JSON if found" in {
      val request = FakeRequest(GET, "/employees/1/contracts")
      val result = route(app, request).get

      // Response returns 200 OK
      status(result) mustBe OK

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Is a valid JSON array
      json.validate[JsArray].isSuccess mustBe true

      // Has data
      val employeeContracts = json.as[JsArray].value
      employeeContracts.length must be > 0

      // First piece of data is as expected
      val first = employeeContracts.head

      // Returns the correct data
      (first \ "employeeId").as[Long] mustBe 1L
      (first \ "startDate").as[String] mustBe "2023-01-01"
      (first \ "contractType").as[String] mustBe "PERMANENT"
      (first \ "employmentType").as[String] mustBe "FULL_TIME"
      (first \ "hoursPerWeek").as[BigDecimal] mustBe 37.5
      (first \ "createdAt").asOpt[String] must not be empty
      (first \ "updatedAt").asOpt[String] must not be empty
    }

    "return 404 if employee not found" in {
      val request = FakeRequest(GET, "/employees/9999/contracts")
      val result = route(app, request).get

      // Response returns 404 NOT_FOUND
      status(result) mustBe NOT_FOUND

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)
      (json \ "error").as[String] must include("not found")
    }

    "return 400 Bad Request for non-numeric ID" in {
      val request = FakeRequest(GET, "/employees/ABC/contracts")
      val result = route(app, request).get

      // Response returns 400 BAD_REQUEST
      status(result) mustBe BAD_REQUEST

      // Response is HTML
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Bad Request")
    }
  }

  // POST /employees/contracts/:id
  "ContractController POST /employees/:id/contracts" should {

    "create a new contract for an employee and return JSON" in {
      val request = FakeRequest(POST, "/employees/1/contracts")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(postPayload)

      val result = route(app, request).get

      // Response returns 201 CREATED
      status(result) mustBe CREATED

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Returns the correct data
      (json \ "employeeId").as[Long] mustBe 1L
      (json \ "startDate").as[String] mustBe "2021-01-01"
      (json \ "endDate").as[String] mustBe "2022-12-31"
      (json \ "contractType").as[String] mustBe "PERMANENT"
      (json \ "employmentType").as[String] mustBe "FULL_TIME"
      (json \ "hoursPerWeek").as[BigDecimal] mustBe 37.5
      (json \ "createdAt").asOpt[String] must not be empty
      (json \ "updatedAt").asOpt[String] must not be empty
    }

    "return 400 Bad Request if contract dates overlap with an existing contract" in {
      val request = FakeRequest(POST, "/employees/2/contracts")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(postPayload)

      val result = route(app, request).get

      // Response returns 400 BAD_REQUEST
      status(result) mustBe BAD_REQUEST

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)
      (json \ "error").as[String] must include("Validation failed")
    }

    "return 404 if employee not found" in {
      val request = FakeRequest(POST, "/employees/9999/contracts")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(postPayload)

      val result = route(app, request).get

      // Response returns 404 NOT_FOUND
      status(result) mustBe NOT_FOUND

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)
      (json \ "error").as[String] must include("not found")
    }

    "return 400 Bad Request for non-numeric ID" in {
      val badPayload = Json.obj(
        "contractType" -> "PERMANENT"
      )

      val request = FakeRequest(POST, "/employees/1/contracts")
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

  // PATCH /contracts/:id
  "ContractController PATCH /contracts/:id" should {

    "update existing contract and return JSON" in {

      val request = FakeRequest(PATCH, "/contracts/1")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(patchPayload)

      val result = route(app, request).get

      // Response returns 200 OK
      status(result) mustBe OK

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Contract is updated correctly
      (json \ "id").as[Long] mustBe 1
      (json \ "endDate").as[String] mustBe "2025-08-01"
    }

    "return 404 if contract not found" in {
      val request = FakeRequest(PATCH, "/contracts/9999")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(patchPayload)

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
      val badPayload = Json.obj("contractType" -> "")

      val request = FakeRequest(PATCH, "/contracts/1")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(badPayload)

      val result = route(app, request).get

      // Response returns 400
      status(result) mustBe BAD_REQUEST

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").asOpt[String] must not be empty
    }

    "return 400 Bad Request for non-numeric ID" in {
      val request = FakeRequest(PATCH, "/contracts/ABC")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(patchPayload)

      val result = route(app, request).get

      // Response returns 400
      status(result) mustBe BAD_REQUEST

      // Response is HTML
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Bad Request")
    }
  }

  // DELETE /contract/:id
  "ContractController DELETE /contracts/:id" should {

    "delete a contract if it exists" in {
      val request = FakeRequest(DELETE, "/contracts/2")
      val result = route(app, request).get

      // Response returns 204
      status(result) mustBe NO_CONTENT
    }

    "return 404 if contract does not exist" in {
      val request = FakeRequest(DELETE, "/contracts/9999")
      val result = route(app, request).get

      // Response returns 404
      status(result) mustBe NOT_FOUND

      // Response returns JSON
      contentType(result) mustBe Some("application/json")

      val json = contentAsJson(result)

      // Returns an error
      (json \ "error").as[String] must include("not found")
    }

    "return 400 Bad Request for non-numeric ID" in {
      val request = FakeRequest(DELETE, "/contracts/ABC")
      val result = route(app, request).get

      // Response returns 400
      status(result) mustBe BAD_REQUEST

      // Response is HTML
      contentType(result) mustBe Some("text/html")
      contentAsString(result) must include ("Bad Request")
    }
  }
}