Just run ant script in this directory, or folow this:

Code beatifier described by Fnl:

I use Jacobe, which is quite good for Java, and follows the right Java code
convensions from Sun:

http://www.tiobe.com/jacobe.htm

I integrate jacobe in Eclipse, and use the plugin (from the same page in the
"Plug-ins" section).

You simply unpack the Jacobe plugin for Eclipse to the root dir of Eclipse.
Eclipse must be restarted, if you have it running!

I attached the robocode.cfg file I use for Jacobe. You should put this file
into the folder where to install Jacobe beside the other config files.

In Eclipse (when you have added your plugin, and can see the purple bug in
the toolbar) you select "Preferences.." from the Window menu. On the
Preferences window you expand the Java (in the tree) and click Jacobe.

Browse and select to your Jacobe executable. Browse and select the
robocode.cfg file. All checkmark should be cleared, except for "overwrite
original files". (otherwise backup files are created). Now you are ready.

Or look at 
http://www.tiobe.com/downloads/jacobe_task_doc.htm
