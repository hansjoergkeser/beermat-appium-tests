import driverconfig.AbstractTestSetup
import driverconfig.MyAppiumDriver.driver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebDriverException
import pages.StartPage
import utils.AdbUtils
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 */
class StartPageTests : AbstractTestSetup() {

    private val logger = Logger.getLogger(StartPageTests::class.toString())

    // don't try this at home... or rather your local pub ;-)
    @Test
    fun `get special price as regular customer and drink 20 beers`() {
        val duration = measureTimeMillis {
            val startPage = StartPage()
            startPage.insertNewPrice("1.99")
            try {
                driver.hideKeyboard()
            } catch (w: WebDriverException) {
                // emulator may use different keyboard
            }
            startPage.addBeers(19)
            val expectedAmount = 20
            val expectedTotalPrice = 3980
            assertEquals(
                expectedAmount,
                startPage.getAmount(),
                "Did not find the expected amount of [$expectedAmount] \uD83C\uDF7A"
            )
            assertEquals(
                expectedTotalPrice,
                startPage.getTotalPrice(),
                "Did not find the expected total price of [$expectedTotalPrice]."
            )

        }
        logger.info("Duration in milliseconds: [$duration]")
    }

    @Test
    fun `get screenshot the fast way`() {
        val duration = measureTimeMillis {
            val adbUtils = AdbUtils()
            adbUtils.restartEmulatorRooted()
            adbUtils.updateDataInTable("20", "199", "3980")
            StartPage().tapOnUpdateFab()
            // wait a sec, the app has no loading indicator
            Thread.sleep(1000)
        }
        logger.info("Duration in milliseconds: [$duration]")
    }

}