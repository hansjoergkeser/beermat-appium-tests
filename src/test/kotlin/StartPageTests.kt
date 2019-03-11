import driverconfig.MyAppiumDriver
import io.appium.java_client.AppiumDriver
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebElement
import pages.StartPage
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Logger


/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 */
class StartPageTests {

    private val logger = Logger.getLogger(StartPageTests::class.toString())

    companion object {

        private lateinit var driver: AppiumDriver<WebElement>

        @BeforeAll
        @JvmStatic
        fun setup() {
            driver = MyAppiumDriver.Driver

            // give the app views some time to get loaded; this setting affects all selenium finding methods
            driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS)
        }

        // let the following test classes create a new session, makes it easier to identify videos on testobject/saucelabs
        @AfterAll
        @JvmStatic
        fun tearDown() {
            MyAppiumDriver.Driver.quit()
        }

    }

    // get test name by junit 5 test info to give the screenshot an unique title
    @AfterEach
    fun takeScreenshot(testInfo: TestInfo) {
        val testName = testInfo.displayName
        logger.info("Taking screenshot after test: [$testName]")
        createAndSaveScreenshot(testName)
    }

    // don't try this at home... or rather your local pub ;-)
    @Test
    fun `get regular customer special price and drink 20 beers`() {
        val startPage = StartPage()
        startPage.insertNewPrice("1.99")
        startPage.addBeers(19)
        driver.hideKeyboard()
        val expectedAmount = 20
        val expectedTotalPrice = 3980
        assertEquals(expectedAmount, startPage.getAmount(), "Did not find the expected amount of [$expectedAmount] \uD83C\uDF7A")
        assertEquals(expectedTotalPrice, startPage.getTotalPrice(), "Did not find the expected total price of [$expectedTotalPrice].")
    }

    private fun createAndSaveScreenshot(fileName: String) {
        val screenshotByteArray = driver.getScreenshotAs(OutputType.BYTES)
        val targetDir = System.getProperty("user.home") + "/appium-screenshots/"
        File(targetDir).mkdir()
        val userDir = "$targetDir$fileName.png"
        File(userDir).writeBytes(screenshotByteArray)
    }

}