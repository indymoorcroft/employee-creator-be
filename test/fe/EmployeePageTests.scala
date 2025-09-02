package fe

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatest.funsuite.AnyFunSuite

class EmployeePageTests extends AnyFunSuite with SeleniumTestBase {

  // Header
  test("Employee page shows header with employee name") {
    driver.get(employeeUrl)
    val header = findByTag("h1")
    assert(header.getText.contains("John Doe"))
  }

  // Personal Details
  test("Personal details section is visible") {
    driver.get(employeeUrl)
    val sectionTitle = findByXpath("//h2[contains(text(),'Personal details:')]")
    assert(sectionTitle.isDisplayed)
  }

  test("Edit button is visible") {
    driver.get(employeeUrl)
    val editButton = findByXpath("//button[contains(text(),'Edit')]")
    assert(editButton.isDisplayed)
  }

  test("Clicking the edit button switches to EmployeeForm") {
    driver.get(employeeUrl)
    clickButton("Edit")

    val form = findByTag("form")
    assert(form.isDisplayed)

    val saveButton = findByXpath("//button[contains(text(),'Save')]")
    assert(saveButton.isDisplayed)
  }

  test("EmployeeForm contains pre-filled data") {
    driver.get(employeeUrl)
    clickButton("Edit")

    assert(findByName("firstName").getAttribute("value").contains("John"))
    assert(findByName("lastName").getAttribute("value").contains("Doe"))
    assert(findByName("email").getAttribute("value").contains("john.doe@example.com"))
    assert(findByName("mobileNumber").getAttribute("value").contains("1234567890"))
    assert(findByName("address").getAttribute("value").contains("123 Main Street"))
  }

  test("User can update employee and save") {
    driver.get(employeeUrl)
    clickButton("Edit")

    val emailInput = findByName("email");
    emailInput.clear()
    emailInput.sendKeys("john.doe2@example.com")
    clickButton("Save")

    val emailValue = findByXpath("//p[normalize-space(.)='email']/following-sibling::p")
    assert(emailValue.getText.contains("john.doe2@example.com"))
  }

  // Contract Details
  test("Contract details section is visible") {
    driver.get(employeeUrl)
    val sectionTitle = findByXpath("//h2[contains(text(),'Contract details:')]")
    assert(sectionTitle.isDisplayed)
  }

  test("Contract details section shows 'No contracts found' when empty") {
    driver.get("http://localhost:5173/employees/3")
    val noContracts = findByXpath("//p[contains(text(),'No contracts found')]")
    assert(noContracts.isDisplayed)
  }

  test("Add Contract button is visible and toggles form") {
    driver.get(employeeUrl)
    val addButton = findByXpath("//button[contains(text(),'Add Contract')]")
    assert(addButton.isDisplayed)

    addButton.click()
    val form = findByTag("form")
    assert(form.isDisplayed)
  }

  test("User can add a new contract") {
    driver.get("http://localhost:5173/employees/3")
    clickButton("Add Contract")

    val hoursInput = findByName("hoursPerWeek")
    hoursInput.clear()
    hoursInput.sendKeys("17.5")
    clickButton("Add")

    val hoursPerWeek = findByXpath("//p[normalize-space(.)='hours Per Week']/following-sibling::p")
    assert(hoursPerWeek.getText.contains("17.5"))
  }

  test("User can edit a contract") {
    driver.get(employeeUrl)
    clickButton("Edit", 2)

    val hoursPerWeekInput = findByName("hoursPerWeek");
    hoursPerWeekInput.clear()
    hoursPerWeekInput.sendKeys("40")
    clickButton("Update")

    val hoursPerWeekValue = findByXpath("//p[normalize-space(.)='hours Per Week']/following-sibling::p")
    assert(hoursPerWeekValue.getText.contains("40"))
  }

  test("User can delete a contract") {
    driver.get(employeeUrl)
    clickButton("Delete")

    val noContracts = findByXpath("//p[contains(text(),'No contracts found')]")
    assert(noContracts.isDisplayed)
  }

  // Error
  test("Error state shows error message") {
    driver.get("http://localhost:5173/employees/error")
    val error = findByTag("p")
    assert(error.getText.contains("There was an error"))
  }

}
