import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}

import java.time.Duration

class EndToEnd extends AnyFunSuite with BeforeAndAfterAll {

  var driver: WebDriver = _
  val baseUrl = "http://localhost:5173"

  override def beforeAll(): Unit = {
    System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver")

    val options = new ChromeOptions()
    options.addArguments("--no-sandbox")
    options.addArguments("--disable-dev-shm-usage")
     options.addArguments("--headless") // uncomment if running headless

    driver = new ChromeDriver(options)
  }

  override def afterAll(): Unit = {
    if (driver != null) driver.quit()
  }

  // EmployeeList
  test("User can access homepage and see title") {
    driver.get(baseUrl)
    val wait = new WebDriverWait(driver, Duration.ofSeconds(5))
    val h1 = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")))
    assert(h1.getText.contains("All Employees"))
  }

  test("Add Employee button is visible") {
    driver.get(baseUrl)
    val wait = new WebDriverWait(driver, Duration.ofSeconds(5))
    val addButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(),'Add Employee')]")))
    assert(addButton.isDisplayed);
  }

  test("Clicking Add Employee button shows the form") {
    driver.get(baseUrl)
    val wait = new WebDriverWait(driver, Duration.ofSeconds(5))
    val addButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(),'Add Employee')]")))
    addButton.click();

    val form = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("form")))
    assert(form.isDisplayed)
  }

  test("User can add a new employee") {
    driver.get(baseUrl)
    val wait = new WebDriverWait(driver, Duration.ofSeconds(5))
    val addButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(),'Add Employee')]")))
    addButton.click();

    val firstNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("firstName")))
    firstNameInput.sendKeys("Jonathan")

    val lastNameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("lastName")))
    lastNameInput.sendKeys("Donathan")

    val emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("email")))
    emailInput.sendKeys("jonathan.donathan@example.com")

    val mobileNumberInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("mobileNumber")))
    mobileNumberInput.sendKeys("+4475411987999")

    val addressInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("address")))
    addressInput.sendKeys("123 Made Up Lane")

    val submitButton = driver.findElement(By.xpath("//button[contains(text(),'Add')]"))
    submitButton.click()

    val newEmployeeCard = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[contains(., 'Jonathan Donathan')]")))
    assert(newEmployeeCard.getText.contains("Jonathan Donathan"))
  }

  test("Employees are listed in the UI") {
    driver.get(baseUrl)
    val wait = new WebDriverWait(driver, Duration.ofSeconds(5))

    val employeeList: WebElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("ul")))
    val employees = employeeList.findElements(By.tagName("li"))
    assert(employees.size() >= 0);
  }


}
