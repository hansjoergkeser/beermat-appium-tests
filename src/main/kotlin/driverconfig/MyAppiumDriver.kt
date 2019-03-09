package driverconfig

import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.WebElement

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 */
object MyAppiumDriver {

    val Driver = AndroidDriver<WebElement>(Hub.getHub(), AppiumCapabilitiesFactory.getDesiredCapabilities())

}