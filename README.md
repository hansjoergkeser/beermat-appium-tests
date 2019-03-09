# Beermat Appium Tests

Appium tests showing the power of manipulating the sqlite database of an Android sample app.

This sample project may help QA engineers and in general developers in the Android mobile app area.
The main idea consists of saving time during taking screenshots of an Android App to be presented on the Google Play Store.

The idea for this project came up by one of the inspiring newsletters from Jonathan Lipps:
https://appiumpro.com/editions/3

A lot of apps locally save items and certain data of their features in a sqlite3 database.
Having the debug build of the app, one can use SQL statements to manipulate these stored data before taking a screenshot.

------

# The app and the tests to make the screenshots

The sample app in this project displays a beermat, having a simple notepad for how man drinks you had and the price for them.
Given I wanted to prepare a beermat with 20 beers for 1,99 Eur in the list, I would use a standard Appium test to prepare the app:
- Tap on the price
- Insert new price
- tap 19 times on the plus button

(Find the mentioned app in the app folder inside the Appium project.)

This takes a lot of time, so I'd prefer a shorter way by updating the related database entry according to my desired values.
TBD

------
# How to execute the tests

To run the tests locally:
- download the sample app to the desktop (find it in the app folder)
- start rooted emulator (without Play Store!)
- start Appium server with this flag: --relaxed-security
- run tests under src/test/kotlin as JUNit tests in IntelliJ  with the following environment variable declared in the VM options:<br>
-ea -Dapp.path=/Users/YOUR.USER/Desktop/app-debug.apk

Optional:
- start rooted emulator or rooted real device with different Android version and prepare the env variable <br>-Dandroid.version=
- check the device name by executing "adb devices" in terminal and prepare the env variable <br>-Ddevice.name=
- tell Appium the endpoint, whether to execute the tests locally or on other test hubs like Saucelabs, Testobject, etc. <br>-Dhub=
- execute tests by terminal command, using the gradle wrapper of this project:<br>
 ./gradlew test

Check all possible capability and hub configurations in these classes:
 - AppiumCapabilitiesFactory
 - Hub

Full example for the  env variables in the JUnit test configuration:<br>
-Ddevice.name=emulator-5554 -Dandroid.version=8.1 -Dtest=SearchTests -Dapp.path=/Users/YOUR.USER/Desktop/house-booking.apk -Dhub=local

------

# Obstacles

There were two obstacles I had to deal with:
- it is not easy to execute a whole statement in one command line
- Emulators with Google Play Store can not be rooted

E.g. in mac os terminal these two commands work, executed separately:<br>
adb shell<br>
sqlite3 /data/data/de.hajo.beermat/databases/beermat.db "UPDATE beermat SET price = 199 WHERE id = 1"

But all in one line, it doesn't:<br>
adb shell sqlite3 /data/data/de.hajo.beermat/databases/beermat.db "UPDATE beermat SET price = 199 WHERE id = 1"

Stackoverflow gave me a hint that the whole sqlite3 command has to be inside apostrophes.<br>
E.g. this command works in terminal:<br>
adb shell 'sqlite3 /data/data/de.hajo.beermat/databases/beermat.db "UPDATE beermat SET price = 199 WHERE id = 1"'

Tried to get the executeScript() method configured, but it never used the quotes or apostrophes I needed.<br>
Finally it worked after escaping an additional quote around the sql statement.<br>
Here my original java code, note the backslash before UPDATE and after 1:<br>
getAppDriver().executeScript("mobile: shell", ImmutableMap.of("command", "sqlite3","args", Arrays.asList("/data/data/de.myapp.android/databases/myapp.db","\"UPDATE [...]\"")))

Well it works but in the Appium log the whole command, which is finally executed,
<br>still looks weird according to the quote chars ( ' and " ):<br>
Running '/Users/my.user/Library/Android/sdk/platform-tools/adb -P 5037 -s emulator-5554 shell sqlite3 /data/data/de.myapp.android/databases/myapp.db '"SELECT [...]"''
<br>or like this (this command also works oddly):<br>
Running '/Users/my.user/Library/Android/sdk/platform-tools/adb -P 5037 -s emulator-5554 shell sqlite3 /data/data/de.myapp.android/databases/myapp.db '"UPDATE [...] WHERE id = ' 1 '"''

Another thing that surprised me was that the new Android emulator images with Play Store included are not rooted and adb root is not possible:<br>
Error: Cannot execute the 'sqlite3' shell command.<br>
Original error: Command '/Users/my.user/Library/Android/sdk/platform-tools/adb -P 5037 -s emulator-5554 shell sqlite3 /data/data/de.myapp.android/databases/myapp.db '"SELECT [...]"'' exited with code 1. 
<br>StdOut: . StdErr: Error: unable to open database "/data/data/de.myapp.android/databases/myapp.db": unable to open database file

Had to use images without the Play Store image, just using the Google Api Services. Then at least "adb root" works and leads to "restarting adbd as root".
Or one could start the emulator without Play Store and without the Google Api Services.

------

Feel free to ask questions or give feedback.<br>
<br>
<br>
<br>