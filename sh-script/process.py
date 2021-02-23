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
	#print line
	if "ABS_MT_POSITION_X" in line:
		strings = line.replace("[","").replace("]","").split(" ")
		tmp = [n for n in strings if n]
		time = tmp[0]
		x = int(tmp[4],16)
	elif "ABS_MT_POSITION_Y" in line:
		strings = line.replace("[","").replace("]","").split(" ")
		tmp = [n for n in strings if n]
		y = int(tmp[4],16)
	elif "ABS_MT_TRACKING_ID   ffffffff" in line:
		line = sys.stdin.readline()
		print "stop"
		break;
	if time != 0 and x!=0 and y!=0:
		print time, x, y
		gesture.append((time,x,y))
		time=0
		x=0
		y=0
text_file = open("gesture.txt", "w")
for row in gesture:
	for i in range (0,len(row)):
		if i == len(row)-1:
			text_file.write(str(row[i]))
			text_file.write("\n")
		else:
			text_file.write(str(row[i]))
			text_file.write(",")
text_file.close()
os.system("ps ax | grep 'adb shell' | grep -v grep | awk '{print $1;}' | xargs kill")