package pages

import driverconfig.MyAppiumDriver
import io.appium.java_client.android.AndroidDriver
import org.openqa.selenium.By.id
import org.openqa.selenium.WebElement

/**
 *  @author hansjoerg.keser
 *  @since 2019-03-09
 */
class StartPage : AbstractPage() {

    override fun getAppiumDriver(): AndroidDriver<WebElement> {
        return MyAppiumDriver.Driver
    }

    private val itemName = id("tv_beer")
    private val amount = id("tv_beer_count")
    private val addBeerButton = id("button_add")
    private val removeBeerButton = id("button_reduce")
    private val price = id("et_price")
    private val totalPrice = id("tv_total_price_of_line")
    private val fab = id("fab")

    fun addBeer() {
        tapOnElement(addBeerButton)
    }

    fun addBeers(amount: Int) {
        for (i in 1..amount) {
            addBeer()
        }
    }

    fun insertNewPrice(newPrice: String) {
        tapOnElement(price)
        findElement(price).clear()
        findElement(price).sendKeys(newPrice)
    }

    fun getAmount(): Int {
        return getText(amount).toInt()
    }

    fun getTotalPrice(): Int {
        return getText(totalPrice).replace("[^0-9]".toRegex(), "").toInt()
    }

    fun tapOnUpdateFab() {
        tapOnElement(fab)
    }

}