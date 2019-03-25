# Beermat Appium Tests

Appium tests showing the power of manipulating the SQLite database of an Android sample app.

This sample project may help QA engineers and in general developers in the Android mobile app area.
The main idea consists of saving time during taking screenshots of an Android App to be presented on Google Play.

The idea for this project came up by one of the inspiring newsletters from Jonathan Lipps:<br>
[Appium Pro Edition 3](https://appiumpro.com/editions/3)

A lot of apps locally save items and certain data of their features in a SQLite database.
Having the debug build of the app, one can use SQL statements to manipulate these stored data before taking a screenshot.

### The app and the tests to make the screenshots

The sample app in this project displays a beermat, having a simple notepad for how man drinks you had and the price for them.
Given I wanted to prepare a beermat with 20 beers for 1,99 Eur in the list, I would use a standard Appium test to prepare the app:
- Tap on the price
- Insert new price
- tap 19 times on the plus button

(Find the mentioned app in the app folder inside the Appium project.)

This takes a lot of time, so I'd prefer a shorter way by updating the related database entry according to my desired values.

### The solution

Before taking the screenshot lets execute an adb shell command to get the app database table in the state we want it to be.
Then we just hit the refresh FAB and voila... the view is ready for the screenshot.
No additional UI interaction needed, the app gets the desired data straight from the database.

Preconditions:
- works only with debug builds <br>(you can't just download and extract an Android app release build from your phone)
- Device or emulator must have root rights
- Appium server must run with --relaxed-security flag
- You must know the structure of your app database and adjust your insert or update SQL statement accordingly

Check the updateDataInTable() method in the StartPageTests class

### Side notes:

This sample app is a very clean app at beginner level. Its purpose is to show the advantage of database manipulation in this context.
If you think of bigger apps out there in the market, you'd probably have to do lots of UI interactions, swiping, tapping, etc.
to get app views ready for getting the desired screenshots.

This approach with adb shell sqlite3 commands does not only save time, it gives you more flexibility and opportunities<br>
for getting screenshots that underline your apps coolest and most important features.

Imagine your app had features that required an user account, what were the precondition to get lists with product data which relied on live data.<br>
Imagine these lists had several features which only appeared if the product data would be in a certain condition, like a price drop.<br>
You could let your Appium tests execute all these UI actions which would cause real api requests on production... no good choice.<br>
Or you could mock the app api requests, but who'd do that just to get screenshots?!<br>
Or you could just insert the necessary data in the app's database tables as described above... sounds clean and easy to me.<br>
Well...easy as soon as the initial setup is done once :-)

### How to execute the tests of this project

To run the tests locally:
- download the sample app to the desktop (find it in the app folder)
- start rooted emulator (without Play Store!)
- start Appium server with this flag: --relaxed-security
- run tests under src/test/kotlin as JUNit tests in IntelliJ  with the following environment variable declared in the VM options:<br>
```
-ea -Dapp.path=/Users/YOUR.USER/Desktop/app-debug.apk
```

Optional:
- start rooted emulator or rooted real device with different Android version and prepare the env variable <br>-Dandroid.version=
- check the device name by executing "adb devices" in terminal and prepare the env variable <br>-Ddevice.name=
- tell Appium the endpoint, whether to execute the tests locally or on other test hubs like Saucelabs, Testobject, etc. <br>-Dhub=
- execute tests by terminal command, using the gradle wrapper of this project:<br>
 ./gradlew test

Check all possible capability and hub configurations in these classes:
 - AppiumCapabilitiesFactory
 - Hub

Full example for the  env variables in the JUnit test configuration:
```
-Ddevice.name=emulator-5554 -Dandroid.version=8.1 -Dtest=SearchTests -Dapp.path=/Users/YOUR.USER/Desktop/house-booking.apk -Dhub=local
```

Screenshots:

Find the created screenshots in your user's home directory under appium-screenshots/
<br>e.g.<br>
/Users/MY.USER/appium-screenshots/get screenshot the fast way().png

The created screenshots should like the one saved in this project under app/

### Obstacles

There were two obstacles I had to deal with:
- it is not easy to execute a whole statement in one command line
- Emulators with Google Play Store can not be rooted

E.g. in mac os terminal these two commands work, executed separately:
```
adb shell<br>
sqlite3 /data/data/de.hajo.beermat/databases/beermat.db "UPDATE beermat SET price = 199 WHERE id = 1"
```

But all in one line, it doesn't:
```
adb shell sqlite3 /data/data/de.hajo.beermat/databases/beermat.db "UPDATE beermat SET price = 199 WHERE id = 1"
```

Stackoverflow gave me a hint that the whole sqlite3 command has to be inside apostrophes.<br>
E.g. this command works in terminal:
```
adb shell 'sqlite3 /data/data/de.hajo.beermat/databases/beermat.db "UPDATE beermat SET price = 199 WHERE id = 1"'
```

So I tried to get the executeScript() method (that Appium offers) configured, but it never used the quotes or apostrophes in the way I needed.<br>
Finally it worked after escaping an additional quote around the sql statement.<br>
Here my original java code, note the backslash before UPDATE and after 1:<br>
```
getAppDriver().executeScript("mobile: shell", ImmutableMap.of("command", "sqlite3","args", Arrays.asList("/data/data/de.myapp.android/databases/myapp.db","\"UPDATE [...]\"")))
```

Well it works but in the Appium log the whole command, which is finally executed, still looks weird to me according to the quote chars ( ' and " ):
```
Running '/Users/my.user/Library/Android/sdk/platform-tools/adb -P 5037 -s emulator-5554 shell sqlite3 /data/data/de.myapp.android/databases/myapp.db '"SELECT [...]"''
```

Or like this (this command also works, oddly):
```
Running '/Users/my.user/Library/Android/sdk/platform-tools/adb -P 5037 -s emulator-5554 shell sqlite3 /data/data/de.myapp.android/databases/myapp.db '"UPDATE [...] WHERE id = ' 1 '"''
```

Another thing that surprised me was that the new Android emulator images with Play Store included are not rooted and adb root is not possible:
```
Error: Cannot execute the 'sqlite3' shell command.
Original error: Command '/Users/my.user/Library/Android/sdk/platform-tools/adb -P 5037 -s emulator-5554 shell sqlite3 /data/data/de.myapp.android/databases/myapp.db '"SELECT [...]"'' exited with code 1. 
StdOut: . StdErr: Error: unable to open database "/data/data/de.myapp.android/databases/myapp.db": unable to open database file
```

Had to use images without the Play Store image, just using the Google Api Services. Then at least "adb root" works and leads to "restarting adbd as root".
Or you could start the emulator without Play Store and without the Google Api Services.

### Author

[Hansjörg Keser](https://github.com/hansjoergkeser)

Feel free to ask questions or give feedback.
<br>
<br>
<br>
<br>
<br>
<br>
<br>
<br>