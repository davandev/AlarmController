# Android alarm keypad for home automation.

![ScreenShot](https://cloud.githubusercontent.com/assets/756370/14658381/a270953a-0693-11e6-8a3c-236fedf1ac3d.png)


Android application used as an alarm triggering keypad for arming/disarming alarm via a homeautomation system. 
The idea is to use an wall mounted android phone to arm and disarm my fibaro home alarm.

Be aware that the security for storing users and passwords is currently not great.., passwords are currently stored in plain text on local device. 

It currently requires that you have two global variables defined on the Fibaro system. 
- "AlarmType" with predefined values "Alarm" or "Skalskydd"
- "AlarmState" with the predefined values "Disarmed", "Disarming", "Armed", "Arming" or "Breached"

-Wakeup of screen does not work properly, workaround is to make use of "Motion Detector" or "Screen Notifications" from google play.
Notice that this application has only been tested with a Fibaro HC2 system.

