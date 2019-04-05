package driverconfig

import driverconfig.MyAppiumDriver.driver
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInfo
import org.openqa.selenium.OutputType
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

/**
 *  @author hansjoerg.keser
 *  @since 2019-04-05
 *
 *  The setup for all test classes with JUnit5
 */
abstract class AbstractTestSetup {

	private val logger = Logger.getLogger(AbstractTestSetup::class.toString())

	@BeforeAll
	fun setup() {
		// give the app views some time to get loaded; this setting affects all selenium finding methods
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS)
	}

	// let the following test classes create a new session, makes it easier to identify videos on testobject/saucelabs
	@AfterAll
	fun tearDown() {
		driver.quit()
	}

	// get test name by junit 5 test info to give the screenshot an unique title
	@AfterEach
	fun takeScreenshot(testInfo: TestInfo) {
		val testName = testInfo.displayName
		logger.info("Taking screenshot after test: [$testName]")
		createAndSaveScreenshot(testName)
		driver.resetApp()
	}

	private fun createAndSaveScreenshot(fileName: String) {
		val screenshotByteArray = driver.getScreenshotAs(OutputType.BYTES)
		val targetDir = System.getProperty("user.home") + "/appium-screenshots/"
		File(targetDir).mkdir()
		val userDir = "$targetDir$fileName.png"
		File(userDir).writeBytes(screenshotByteArray)
	}

}