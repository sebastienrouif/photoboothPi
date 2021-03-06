PiPhotobooth
=============
This project base idea is to be able to create a photobooth offline. Here is what you'll need : 
 - RaspberryPi : main server in charge of managing everything else
 - DSLR : because you want good picture I'll advice a DSLR
 - Tablet or screen : displays countdown and images taken
 - Button (optional) : to avoid user touching the pi or the DSLR use a big button as a remote
 - Lightning : to take good pictures you need a good light.

[Raspberry Pi, Camera and Node.js – Live Streaming with Websockets #IoT](http://thejackalofjavascript.com/rpi-live-streaming)

# Setting up the Raspberry Pi
## Setting up the environment
Follow the steps to flash you sdCard with [raspbian](https://www.raspberrypi.org/downloads/raspbian/)
## Installing the project
Checkout or Download the project on the pi then open a terminal from the photoboothPi/pi folder
1. NPM server

    
    npm install
    
2. Install Nan version of gphoto2

install gphoto2 using https://github.com/gonzalo/gphoto2-updater 

node-gphoto2 doesn't work on Raspberry Pi https://github.com/lwille/node-gphoto2/issues/67 so you'll have to install the Nan branch.
 
 
	mkdir gphoto2Nan
	cd gphoto2Nan
	git clone git@github.com:lwille/node-gphoto2.git && cd node-gphoto2
	git checkout feature/nan
	npm install
	sudo npm link
	# cd to your project directory
	sudo npm link gphoto2

Disable gphoto2 from automatically mounting the camera :
in /usr/lib/gvfs/ rename gvfsd-gphoto2 and gvfs-gphoto2-volume-monitor

3. Run the node server


    cd pi/
    node index.js
    
you can connect to it through localhost http://127.0.0.1:3000 

#Connect the DSLR
Easiest step just plug your DSLR to the Pi via usb.

#Connect the tablet
##Option 1 : via USB
1. Create local local network over USB
share over USB : http://elinux.org/How_to_use_an_Android_tablet_as_a_Raspberry_Pi_console_terminal_and_internet_router

2. Make the Ip address of the Pi static


    nano /etc/network/interfaces
    add this add the end : 
        allow-hotplug usb0
        iface usb0 inet static
            address 192.168.42.10
            netmask 255.255.255.0

3. Enable USB tethering
On the android Tablet navigate to : Settings → Connections → Tethering and Wi-Fi hotspot → USB tethering

4. Test
Use you web browser to connect to http://192.168.42.10:3000 

5. Tip 
you can then use the tablet to connect to the Pi [through VNC](http://elinux.org/Display_a_Raspbian_desktop_on_Android_using_VNC) 

## Option 2 : make your Pi a Wifi hotspot


#Use the Android app
- Download the app from the Pi :  http://192.168.42.10:3000/static/app-debug.apk
- Install and run it ( you might have to enable unknown sources)
- There is a hidden button on the bottom right corner to change the IP of the server
- Tapping the screen should start the countdown, take a picture and display the picture

#Add a button
 Push button
 http://www.arduinoclassroom.com/index.php/arduino-101/chapter-4a 
 https://github.com/androidthings/sample-button

#Make it run on boot


#Versions used when runing :
npm -v 5.3.0
node -v 8.2.1
uname -a linux raspberrypi 4.9.35-v7+ #1014 SMP Fri June 30 14:47:43 BST 2017 armv71 GNU/Linux

