# ExpressiveKeyboards

Expressive Keyboard
How to use the mock-up application

Jessalyn ALVINA - 27 Apr 2016


The mock-up application, GestureLogger, is used to give a demo on how to use Expressive Keyboards.
 Figure 1: Prompt when first opening the app
In 27 April 2016, there are two main functions: to define a font and to try typing with dynamic outputs.

How to run the app
A. Android Debug Bridge
GestureLogger relies on Android Debug Bridge to capture the gesture coordinate and send them back to the phone.
To prepare:
Connect the device to the computer via USB cable.
Make sure Android SDK is installed (or you have adb tools).
Copy the file process.py and script-gesturelogger.sh to  "android-sdk-macosx/platform-tools/" or to the folder with adb tool.
Open console bash, redirect to "android-sdk-macosx/platform-tools/" > cd android-sdk-macosx/platform-tools/ Register this path to $PATH Variable.
To run the shell script: > ./script-gesturelogger.sh	
The shell is now capturing all the touch event coordinate.
The python script process.py extracts the touch coordinate and send it back to the shell. The shell then pushes a text file called "gesture.txt", containing the touch coordinate from a touch down event until a touch-up event, to "/storage/sdcard0/".
GestureLogger only reads the file when a rich output is expected.
To exit the shell script, press Control+C

B. GestureLogger: Define a font
Open GestureLogger application
Fill in the Font ID and choose “Define a font”
If a font with such ID has not been defined yet, you start defining from ‘a’ to ‘z’, or continue to define from where it’s left off (e.g. you quit after defining ‘g’ then you can continue defining ‘h’, etc.). You can try typing with the font (only colored, without interpolation) afterwards.
If a font with such ID has been defined once, you have three options:  CREATE a variation of the font by editing the pre-defined font (the original becomes the baseline) to enable interpolation. Here you can edit the position of the control points (don’t add/delete control points) to create the variation. You can try typing with the dynamic font (colored, with interpolation) afterwards.  EDIT the font (baseline). You can edit the position or add/delete control points. You can try typing with the font (only colored, without interpolation) afterwards.  SKIP to use the font as it is (only colored, without interpolation).    
If a font with such ID has been defined twice (you have chosen CREATE in Step 4), you have two options: EDIT the font (variation). You can edit the position control points of the variation (don’t add/delete control points). SKIP to use the font as it is. You can try typing with the font (colored, with interpolation) afterwards.

Existing font ID: 	With interpolation: 2 and 78 (default – if you leave the Font ID blank).
	Without interpolation: 0 and 5.

C. GestureLogger: Try typing with Expressive Keyboard
Open GestureLogger application
Fill in the Font ID (pick from the existing) and choose “Try typing with Expressive Keyboard”
The first page changes the color only. Press “NEXT” to continue to the second page.
The second page uses the dynamic font and color.

Mapping on Text


Mapping on Emoji
Type “emoji” to create a smiley face (note: if it’s not recorded as a word yet, type emoji several times until it is included into the personal dictionary of the keyboard.


Technical Implementation
How to install:
Open the project GestureLogger on Eclipse
Go to Constant class, change variable EXPTYPE=4
Run on Android device
