import com.google.common.collect.ImmutableMap
import driverconfig.AbstractTestSetup
import driverconfig.MyAppiumDriver.driver
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.openqa.selenium.WebDriverException
import pages.StartPage
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Arrays.asList
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
     * check all the table names and structure by executing in terminal:
     * adb shell 'sqlite3 /data/data/de.hajo.beermat/databases/beermat-database.db ".schema"'
     *
     * or
     * check specific table, e.g. favourites:
     * adb shell 'sqlite3 /data/data/de.hajo.beermat/databases/beermat-database.db "PRAGMA table_info(favorites)"'
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

}