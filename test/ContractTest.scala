import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.{JsObject, Json}
import play.api.test.Helpers._
import play.api.test._
import utils.CleanDatabase

class ContractTest extends PlaySpec with GuiceOneAppPerSuite with Injecting with CleanDatabase {

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

  val contractPayload: JsObject = Json.obj(
    "startDate" -> "2021-01-01",
    "endDate" -> "2022-12-31",
    "contractType" -> "PERMANENT",
    "employmentType" -> "FULL_TIME",
    "hoursPerWeek" -> 37.5,
  )

  // POST /contracts/:id
  "ContractController POST employees/:id/contracts" should {

    "create a new contract for an employee and return JSON" in {
      val request = FakeRequest(POST, "/employees/1/contracts")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(contractPayload)

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

    "return 404 if employee not found" in {
      val request = FakeRequest(POST, "/employees/9999/contracts")
        .withHeaders("Content-Type" -> "application/json")
        .withJsonBody(contractPayload)

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
}