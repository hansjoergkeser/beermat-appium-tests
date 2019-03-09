package pages

import io.appium.java_client.android.AndroidDriver
import org.junit.jupiter.api.fail
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement
import java.util.logging.Logger

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 *
 *  Abstract class as parent for all page objects, here the classes in pages and elements packages.
 *  Contains useful selenium methods to identify elements and extract data.
 *
 *  Use hierarchy inspector tools to identify selectors for all the necessary elements.
 *  E.g. appium desktop:
 *  https://github.com/appium/appium-desktop
 *
 *  Start appium desktop inspection with this minimum set of capabilities:
 *
 *  {
 *  "platformName": "Android",
 *  "deviceName": "android emulator",
 *  "app": "/Users/YOUR.USER/Desktop/app-debug.apk"
 *  }
 *
 */
abstract class AbstractPage {

    abstract fun getAppiumDriver(): AndroidDriver<WebElement>

    protected fun findElement(selector: By): WebElement {
        try {
            return getAppiumDriver().findElement(selector)
        } catch (n: NoSuchElementException) {
            fail("Did not find element with the following selector: $selector")
        }
    }

    protected fun getText(selector: By): String {
        try {
            return getAppiumDriver().findElement(selector).text
        } catch (n: NoSuchElementException) {
            fail("Did not find the element to get its text using the following selector: $selector")
        }
    }

    protected fun tapOnElement(selector: By) {
        try {
            getAppiumDriver().findElement(selector).click()
        } catch (n: NoSuchElementException) {
            fail("Did not find element with the following selector: $selector")
        }
    }

}