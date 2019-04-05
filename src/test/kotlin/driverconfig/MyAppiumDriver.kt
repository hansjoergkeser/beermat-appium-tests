package driverconfig

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 *
 *  the appium driver as singleton
 */
object MyAppiumDriver {

	val driver = AndroidDriver<WebElement>(Hub.getHub().toUrl(), AppiumCapabilitiesFactory().getDesiredCapabilities())

}