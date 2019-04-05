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
		SAUCELABS("http://sl....")
	}

	companion object {

		fun getHub(): Endpoint {
			System.getProperty("hub", "local").let {
				return when (it.toLowerCase()) {
					"local" -> Endpoint.LOCAL
					"testobject" -> Endpoint.TESTOBJECT
					"saucelabs" -> Endpoint.SAUCELABS
					else -> {
						Logger.getLogger("Hub").warning(
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