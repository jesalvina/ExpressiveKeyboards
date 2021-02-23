import sys
import os

global gesture
global time
global x
global y
gesture = []
time = 0
x = 0
y = 0

while True:
	line = sys.stdin.readline()
	#print "THE LINE",line,
	if "ABS_MT_POSITION_X" in line:
		strings = line.replace("[","").replace("]","").split(" ")
		tmp = [n for n in strings if n]
		time = tmp[0]
		x = int(tmp[4],16)
	elif "ABS_MT_POSITION_Y" in line:
		strings = line.replace("[","").replace("]","").split(" ")
		tmp = [n for n in strings if n]
		y = int(tmp[4],16)
	if time != 0 and x!=0 and y!=0:
		print time, x, y
		text_file = open("gesture.txt", "w")
		text_file.write( time + "," + str(x) + "," + str(y))
		text_file.close()
		break;
os.system("ps ax | grep 'adb shell' | grep -v grep | awk '{print $1;}' | xargs kill")