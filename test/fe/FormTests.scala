package fe

import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.scalatest.funsuite.AnyFunSuite

class FormTests extends AnyFunSuite with SeleniumTestBase {

  // Employee Form
  test("Employee Form renders with all input fields") {
    driver.get(baseUrl)
    clickButton("Add Employee")

    val fields = List("firstName", "lastName", "email", "mobileNumber", "address")
    fields.foreach { field =>
      val input = findByName(field)
      assert(input.isDisplayed)
    }
  }

  test("Cancel button closes the employee form") {
    driver.get(baseUrl)
    clickButton("Add Employee")
    clickButton("Cancel")

    val formGone = ExpectedConditions.invisibilityOfElementLocated(By.tagName("form"))
    assert(formGone.apply(driver))
  }

  test("Form error message is displayed on employee form if validation fails") {
    driver.get(employeeUrl)
    clickButton("Edit")

    val emailInput = findByName("email");
    emailInput.clear()
    emailInput.sendKeys("invalid input")
    clickButton("Save")

    val errorMsg = findByXpath("//*[contains(@class,'text-red-600')]")
    assert(errorMsg.isDisplayed)
  }

  // Contract Form
  test("Contract Form renders with all input fields") {
    driver.get(employeeUrl)
    clickButton("Add Contract")

    val fields = List("startDate", "endDate", "contractType", "employmentType", "hoursPerWeek")
    fields.foreach { field =>
      val input = findByName(field)
      assert(input.isDisplayed)
    }
  }

  test("Cancel button closes the contract form") {
    driver.get(employeeUrl)
    clickButton("Add Contract")
    clickButton("Cancel")

    val formGone = ExpectedConditions.invisibilityOfElementLocated(By.tagName("form"))
    assert(formGone.apply(driver))
  }

  test("Form error message is displayed on contract form if validation fails") {
    driver.get(employeeUrl)
    clickButton("Add Contract")

    val hoursPerWeekInput = findByName("hoursPerWeek");
    hoursPerWeekInput.clear()
    hoursPerWeekInput.sendKeys("invalid input")
    clickButton("Add")

    val errorMsg = findByXpath("//*[contains(@class,'text-red-600')]")
    assert(errorMsg.isDisplayed)
  }
}
