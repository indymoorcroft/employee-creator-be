package e2e

import org.openqa.selenium.{By, WebDriver, WebElement}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.scalatest.{BeforeAndAfterAll, Suite}

import java.time.Duration

trait SeleniumTestBase extends BeforeAndAfterAll{ this: Suite =>
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

  def findByLinkText(link: String): WebElement =
    webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.linkText(link)))
}
