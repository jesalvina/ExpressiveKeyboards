README
by Jessalyn ALVINA
30.11.2015

This project is the mockup application of Expressive Keyboards.

The Android application `GestureLogger` has to be complemented with Android Debug Bridge using `adb shell geteven` written in a shell script called `script.sh`. The script runs the `getevent` command, redirect it to a python script `process.py` to get the coordinate of touch, then push the output back to the phone. The Android phone has to be jail-broken and has a gesture keyboard installed (e.g., SwiftKey).

How to prepare:
1.	Open console bash, redirect to `android-sdk-macosx/platform-tools/`
	`> cd android-sdk-macosx/platform-tools/`
	
2.	Connect the device via USB cable

3.	To run the shell script:
	`> ./script.sh`
	
4.	The shell is now capturing all the touch event coordinate.
	The python script `process.py` extract the touch coordinate and send it back to the shell.
	The shell then pushes a text file called "gesture.txt", containing the touch coordinate from a touch down event until a touch-up event, to `/storage/sdcard0/`.
	
5.	`GestureLogger` only reads the file when an expressive output is expected.

6.	To exit the shell script, press `Control+C`
