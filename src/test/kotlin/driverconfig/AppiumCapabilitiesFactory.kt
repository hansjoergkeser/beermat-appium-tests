package driverconfig

import io.appium.java_client.remote.MobileCapabilityType
import org.openqa.selenium.remote.DesiredCapabilities

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 *
 *  The desired capabilities for app driver initialisation
 *
 *  some values have to be delivered as system environment variables
 *  check the System.getProperty() declarations, e.g.
 *  the device name can be set in terminal like this:
 *
 *  mvn clean test -Ddevice.name=emulator-5554
 */
class AppiumCapabilitiesFactory {

    fun getDesiredCapabilities(): DesiredCapabilities {

        return DesiredCapabilities().apply {
            setCapability(MobileCapabilityType.PLATFORM_NAME, "Android")
            setCapability(MobileCapabilityType.AUTOMATION_NAME, "UiAutomator2")

            // you can check the device name by executing "adb devices" in terminal
            setCapability(
                    MobileCapabilityType.DEVICE_NAME, if (System.getProperty("device.name").isNullOrEmpty())
                "emulator" else System.getProperty("device.name").isNullOrEmpty()
            )

            // set optional platform version capability to filter out other devices/emulators with different os versions
            if (!System.getProperty("android.version").isNullOrEmpty()) {
                setCapability(MobileCapabilityType.PLATFORM_VERSION, System.getProperty("android.version"))
            }

            // check the values by executing: adb shell dumpsys window windows | grep -E 'mCurrentFocus'
            setCapability("appPackage", "de.hajo.beermat")
            setCapability("appActivity", "de.hajo.beermat.MainActivity")
            setCapability("appWaitActivity", "de.hajo.beermat.MainActivity")

            setCapability(MobileCapabilityType.APP, System.getProperty("app.path"))

            // signing not necessary anymore, see https@ //discuss.appium.io/t/why-does-appium-sign-the-apk-are-there-any-benefits/7434/2
            setCapability("noSign", true)

            // use this capability for debugging, it gives you more time until appium server shutdown
            // if no new commands are sent to device/emulator in the declared amount of time
//                setCapability(MobileCapabilityType.NEW_COMMAND_TIMEOUT, 600)
        }

    }

}