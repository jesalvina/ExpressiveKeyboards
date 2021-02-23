while true; do
	adb shell getevent -t -l | python process.py
	echo "push"
	adb push gesture.txt /storage/sdcard0/
	echo "done"
done