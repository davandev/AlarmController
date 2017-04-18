# Android alarm keypad for home automation (ZenitGateKeeper).

<img src="https://cloud.githubusercontent.com/assets/756370/19986985/20d5ba4c-a219-11e6-8e46-b2338b2a16d9.png" width="350">
<img src="https://cloud.githubusercontent.com/assets/756370/19987009/3b7b3534-a219-11e6-905e-066f43ab8238.png" width="350">
<img src="https://cloud.githubusercontent.com/assets/756370/14658397/c731e7ca-0693-11e6-8584-482df136bd69.png" width="350">
<img src="https://cloud.githubusercontent.com/assets/756370/14658623/9314a17e-0695-11e6-81a8-83a59a2198fd.png" width="350">

Android application used as an alarm triggering keypad for arming/disarming alarm via a homeautomation system. 
The idea is to use an wall mounted android phone to arm and disarm my Fibaro home alarm.

The application is currently in early development phase, while I think it is still usable for daily use..
It currently supports:
- Arming/Disarming directly via the Fibaro system
- Arming/Disarming via an external server
- Sending Telegram messages when disarmed.
- Turn on screen when a request is received from Fibaro system, typically triggered when a motion or door sensor is breached.
- Execute Fibaro scene when low/high battery level occur. Used to turn on/off a wallsocket to charge the device.
- Take a picture from front camera when disarmed.


Be aware that the security for storing users and passwords is currently not great.., passwords are currently stored in plain text on local device, the settings should be kept safe unless the device is rooted.

- Default pin for unlocking settings menu is "1234"

Notice that this application has only been tested with a Fibaro HC2 system, but in theory it should be able to work with other systems as well. It is tested on a phone running android 4.4.x

