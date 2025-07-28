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
}
