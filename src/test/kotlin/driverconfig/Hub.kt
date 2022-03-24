package driverconfig

import java.net.URL
import java.util.logging.Logger

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 */
class Hub {

	enum class Endpoint(val urlString: String) {
		LOCAL("http://localhost:4723/wd/hub"),
		TESTOBJECT("http://to..."),
		SAUCELABS("https://" + "ENTER_CREDENTIALS_HERE" + "@ondemand.eu-central-1.saucelabs.com:443/wd/hub")
	}

	companion object {
		val HUB = "hub";
		val LOCAL = "local";

		fun getHub(): Endpoint {

			System.getProperty(HUB, LOCAL).let {
				Logger.getLogger(HUB).info("Chosen hub: $it")

				return when (it.toLowerCase()) {
					LOCAL -> Endpoint.LOCAL
					"testobject" -> Endpoint.TESTOBJECT
					"saucelabs" -> Endpoint.SAUCELABS
					else -> {
						Logger.getLogger(HUB).warning(
								"Could not process given hub value for appium driver endpoint, setting hub to local."
						)
						Endpoint.LOCAL
					}
				}
			}

		}

	}

}

// extension function to be used instead of URL(Hub.getHub())
fun Hub.Endpoint.toUrl() = URL(this.urlString)