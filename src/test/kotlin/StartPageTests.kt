import com.google.common.collect.ImmutableMap
import driverconfig.MyAppiumDriver
import io.appium.java_client.AppiumDriver
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.openqa.selenium.OutputType
import org.openqa.selenium.WebElement
import pages.StartPage
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.Arrays.asList
import java.util.concurrent.TimeUnit
import java.util.logging.Logger
import kotlin.system.measureTimeMillis

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
        MyAppiumDriver.Driver.resetApp()
    }

    // don't try this at home... or rather your local pub ;-)
    @Test
    fun `get special price as regular customer and drink 20 beers`() {
        val duration = measureTimeMillis {
            val startPage = StartPage()
            startPage.insertNewPrice("1.99")
            startPage.addBeers(19)
            try {
                driver.hideKeyboard()
            } catch (e: Exception) {
            }
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
        logger.info("Duration in milli seconds: [$duration]")
    }

    @Test
    fun `get screenshot the fast way`() {
        val duration = measureTimeMillis {
            restartEmulatorRooted()
            updateDataInTable("20", "199", "3980")
            StartPage().tapOnUpdateFab()
            // wait a sec, the app has no loading indicator
            Thread.sleep(1000)
        }
        logger.info("Duration in milli seconds: [$duration]")
    }

    /**
     * like executing "adb root" in terminal
     * necessary for emulators with the latest android versions including google api services
     *
     * does not work with images with play store! does not throw an exception.
     */
    private fun restartEmulatorRooted() {
        val command = mutableListOf<String>()
        command.add("root")
        command.add(0, getPathToAdb())

        logger.info("Executing adb root command: $command")
        val pb = ProcessBuilder()
        val adbProcess = pb.command(command).start()
        adbProcess.waitFor()
        try {
            BufferedReader(InputStreamReader(adbProcess.inputStream)).readLine()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getPathToAdb(): String {
        var androidHome = System.getenv("ANDROID_HOME")
            ?: throw RuntimeException("Environment variable ANDROID_HOME is not set, but is necessary to use adb.")

        if (androidHome.endsWith("/")) {
            androidHome = "$androidHome/"
        }
        return "$androidHome/platform-tools/adb"
    }

    /**
     * check the column names by executing in terminal: adb shell 'sqlite3 /data/data/de.hajo.beermat/databases/beermat-database.db ".schema"'
     *
     * @amount: the amount of beers
     * @price: the beer price;
     * note that this column needs an int value, so remove all commas, points, etc. in the parameter string
     * @totalPrice: the total price for all beers;
     * note that this column needs an int value, so remove all commas, points, etc. in the parameter string
     *
     * does not work with images with play store, leading to an error like this:
     * org.openqa.selenium.WebDriverException: An unknown server-side error occurred while processing the command. Original error: Cannot execute the 'sqlite3' shell command.
     * Original error: Command '/Users/MY.USER/Library/Android/sdk/platform-tools/adb -P 5037 -s emulator-5554 shell sqlite3 /data/data/de.hajo.beermat/databases/beermat-database.db
     * '"UPDATE Beermat SET amount = 20, price = 199, total_price = 3980"'' exited with code 1. StdOut: .
     * StdErr: Error: unable to open database "/data/data/de.hajo.beermat/databases/beermat-database.db": unable to open database file
     */
    private fun updateDataInTable(amount: String, price: String, totalPrice: String) {
        val updateTableArgs = asList(
            "/data/data/de.hajo.beermat/databases/beermat-database.db",
            "\"UPDATE Beermat SET amount = $amount, price = $price, total_price = $totalPrice\""
        )
        val manipulateItemPricesCmd = ImmutableMap.of(
            "command", "sqlite3",
            "args", updateTableArgs
        )
        driver.executeScript("mobile: shell", manipulateItemPricesCmd)
    }

    private fun createAndSaveScreenshot(fileName: String) {
        val screenshotByteArray = driver.getScreenshotAs(OutputType.BYTES)
        val targetDir = System.getProperty("user.home") + "/appium-screenshots/"
        File(targetDir).mkdir()
        val userDir = "$targetDir$fileName.png"
        File(userDir).writeBytes(screenshotByteArray)
    }

}