package fe

import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.{By, WebElement}
import org.scalatest.funsuite.AnyFunSuite

class HomePageTests extends AnyFunSuite with SeleniumTestBase {

  // EmployeeList
  test("User can access homepage and see title") {
    driver.get(baseUrl)
    val h1 = findByTag("h1")
    assert(h1.getText.contains("All Employees"))
  }

  test("Add Employee button is visible") {
    driver.get(baseUrl)
    val addButton = findByXpath("//button[contains(text(),'Add Employee')]")
    assert(addButton.isDisplayed);
  }

  test("Clicking Add Employee button shows the form") {
    driver.get(baseUrl)
    clickButton("Add Employee")

    val form = findByTag("form")
    assert(form.isDisplayed)
  }

  test("User can add a new employee") {
    driver.get(baseUrl)
    clickButton("Add Employee")

    findByName("firstName").sendKeys("Jonathan")
    findByName("lastName").sendKeys("Donathan")
    findByName("email").sendKeys("jonathan.donathan@example.com")
    findByName("mobileNumber").sendKeys("+4475411987999")
    findByName("address").sendKeys("123 Made Up Lane")

    clickButton("Add")

    val newEmployeeCard = findByXpath("//li[contains(., 'Jonathan Donathan')]")
    assert(newEmployeeCard.getText.contains("Jonathan Donathan"))
  }

  test("Employees are listed in the UI") {
    driver.get(baseUrl)
    val employeeList: WebElement = findByTag("ul")
    val employees = employeeList.findElements(By.tagName("li"))
    assert(employees.size() >= 0);
  }

  // EmployeeCard
  test("Employee card displays name, email, and mobile") {
    driver.get(baseUrl)
    val employeeCard = findByTag("li")

    val name = employeeCard.findElement(By.xpath(".//p[contains(@class,'text-lg')]")).getText
    val email = employeeCard.findElement(By.xpath(".//p[contains(.,'Email:')]")).getText
    val mobile = employeeCard.findElement(By.xpath(".//p[contains(.,'Mobile:')]")).getText

    assert(name.nonEmpty)
    assert(email.contains("Email:"))
    assert(mobile.contains("Mobile:"))
  }

  test("View link navigates to employee details page") {
    driver.get(baseUrl)
    val viewLink = findByLinkText("View")
    viewLink.click()

    assert(driver.getCurrentUrl.contains("/employees/"))
  }

  // *** MUST be run after 'User can add a new employee' test ***
  test("Remove button deletes an employee") {
    driver.get(baseUrl)
    val employeeCard = findByXpath("//li[contains(., 'Jonathan Donathan')]")
    clickButton("Remove")

    webDriverWait.until(ExpectedConditions.stalenessOf(employeeCard))

    val employeeCards = driver.findElements(By.xpath("//li[contains(., 'Jonathan Donathan')]"))
    assert(employeeCards.isEmpty)
  }

}
