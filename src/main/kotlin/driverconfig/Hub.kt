package driverconfig

import java.net.URL
import java.util.logging.Logger

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 */
class Hub {

    enum class Endpoint(val url: URL) {
        LOCAL(URL("http://localhost:4723/wd/hub")),
        TESTOBJECT(URL("http://to...")),
        SAUCELABS(URL("http://sl...."))
    }

    companion object {

        fun getHub(): URL {
            if (System.getProperty("hub").isNullOrEmpty()) {
                Logger.getLogger("Hub").info("System.getProperty(hub) was null or empty, setting hub to local.")
                return Endpoint.LOCAL.url
            } else {
                System.getProperty("hub").let {
                    return when (it.toLowerCase()) {
                        "local" -> Endpoint.LOCAL.url
                        "testobject" -> Endpoint.TESTOBJECT.url
                        "saucelabs" -> Endpoint.SAUCELABS.url
                        else -> {
                            Logger.getLogger("Hub").warning(
                                    "Could not process given hub value for appium driver endpoint, setting hub to local.")
                            Endpoint.LOCAL.url
                        }
                    }
                }
            }
        }

    }

}