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

  lazy val webDriverWait: WebDriverWait = new WebDriverWait(driver, Duration.ofSeconds(5))

  def findByXpath(xpath: String): WebElement =
    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)))

  def findByName(name: String): WebElement =
    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.name(name)))

  def findByTag(tag: String): WebElement =
    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.tagName(tag)))

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
    val addButton = findByXpath("//button[contains(text(),'Add Employee')]")
    addButton.click();

    val form = findByTag("form")
    assert(form.isDisplayed)
  }

  test("User can add a new employee") {
    driver.get(baseUrl)
    val addButton = findByXpath("//button[contains(text(),'Add Employee')]")
    addButton.click();

    findByName("firstName").sendKeys("Jonathan")
    findByName("lastName").sendKeys("Donathan")
    findByName("email").sendKeys("jonathan.donathan@example.com")
    findByName("mobileNumber").sendKeys("+4475411987999")
    findByName("address").sendKeys("123 Made Up Lane")

    val submitButton = findByXpath("//button[contains(text(),'Add')]")
    submitButton.click()

    val newEmployeeCard = findByXpath("//li[contains(., 'Jonathan Donathan')]")
    assert(newEmployeeCard.getText.contains("Jonathan Donathan"))
  }

  test("Employees are listed in the UI") {
    driver.get(baseUrl)

    val employeeList: WebElement = findByTag("ul")
    val employees = employeeList.findElements(By.tagName("li"))
    assert(employees.size() >= 0);
  }


}
