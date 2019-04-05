package utils

import com.google.common.collect.ImmutableMap
import driverconfig.MyAppiumDriver.driver
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Arrays
import java.util.logging.Logger

/**
 *  @author hansjoerg.keser
 *  @since 2019-04-05
 */
class AdbUtils {

	private val logger = Logger.getLogger(AdbUtils::class.toString())

	/**
	 * like executing "adb root" in terminal
	 * necessary for emulators with the latest android versions including google api services
	 *
	 * does not work with images with play store! does not throw an exception.
	 */
	fun restartEmulatorRooted() {
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
	fun updateDataInTable(amount: String, price: String, totalPrice: String) {
		val updateTableArgs = Arrays.asList(
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