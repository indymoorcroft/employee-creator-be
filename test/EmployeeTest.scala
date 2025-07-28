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

      // Is a valid JSON array
      json.validate[JsArray].isSuccess mustBe true

      // Matches seeded data length
      val employees = json.as[JsArray].value
      employees.length mustBe 1

      // Data is as expected
      val first = employees.head
      (first \ "firstName").as[String] mustBe "May"
      (first \ "lastName").as[String] mustBe "Jupp"
      (first \ "email").as[String] mustBe "may.jupp@example.com"
      (first \ "mobileNumber").as[String] mustBe "0987654321"
      (first \ "address").as[String] mustBe "456 Oak Avenue"
      (first \ "createdAt").asOpt[String] must not be empty
      (first \ "updatedAt").asOpt[String] must not be empty
    }
  }

  "EmployeeController GET /employees/:id" should {
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
  }

  "EmployeeController GET /employees/:id" should {
    "return 400 Bad Request for non-numeric ID" in {
      val request = FakeRequest(GET, "/employees/!!!")
      val result = route(app, request).get

      // Response returns 400 BAD_REQUEST
      status(result) mustBe BAD_REQUEST
    }
  }
}