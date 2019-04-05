package driverconfig

import java.util.logging.Logger

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 */
class Hub {

    enum class Endpoint(val urlString: String) {
        LOCAL("http://localhost:4723/wd/hub"),
        TESTOBJECT("http://to..."),
        SAUCELABS("http://sl....")
    }

    companion object {

        fun getHub(): String {
			System.getProperty("hub", "local").let {
                return when (it.toLowerCase()) {
                    "local" -> Endpoint.LOCAL.urlString
                    "testobject" -> Endpoint.TESTOBJECT.urlString
                    "saucelabs" -> Endpoint.SAUCELABS.urlString
                    else -> {
                        Logger.getLogger("Hub").warning(
                                "Could not process given hub value for appium driver endpoint, setting hub to local."
                        )
                        Endpoint.LOCAL.urlString
                    }
                }
            }

        }

    }

}