====
    Copyright (c) 2001-2017 Mathew A. Nelson and Robocode contributors
    All rights reserved. This program and the accompanying materials
    are made available under the terms of the Eclipse Public License v1.0
    which accompanies this distribution, and is available at
    https://robocode.sourceforge.io/license/epl-v10.html
====

Author: Flemming N. Larsen:

The Jacobe Code Beautifier is used as code beautifier for Robocode.
You can read about it and download it from here:

http://www.tiobe.com/index.php/content/products/jacobe/Jacobe.html

Jacobe is quite good for Java as it follows the Java code conventions from Oracle:

http://www.oracle.com/technetwork/java/codeconv-138413.html

Jacobe can be integrated with Eclipse by using the Eclipse plugin for Jacobe.
The plugin can be downloaded from the same page used when downloading Jacobe
under the "Plug-ins" section.

You can install the Jacobe plugin into Eclipse simply by unpacking the Jacobe
plugin to the root directory of Eclipse (the plugins directory must be
"overwritten" with the Jacobe plugin).

When the Jacobe plugin has been put into the root directory of Eclipse, you
must restart Eclipse, if you had it running while putting the plugin into the
Eclipse directory. Otherwise you'll not be able to use Jacobe yet.

I attached the robocode.cfg file, which is used for beautifying the Robocode
sources with Jacobe. You should put this file into the directory where
you have installed Jacobe, i.e. just together the sun.cfg file.

In Eclipse (when you have added your plugin, and can see the purple bug in
the toolbar) you select "Preferences.." from the Window menu. On the
Preferences window you expand the Java (in the tree) and click Jacobe.

Browse and select your Jacobe executable. Browse and select the
robocode.cfg file. All checkmarks should be cleared, except for "overwrite
original files". Otherwise backup files are created. Now you are ready
to code beautify following the Robocode Jacobe configuration.

You can also code beautify the sources of Robocode by using the target named
"jacobe" in the build.xml file provided in this directory. Here you'll
need to download and install the Jacobe Ant Task:

http://www.tiobe.com/content/products/jacobe/jacobe-ant-task.zip

You should copy the jacode.jar file inside the jacobe-ant-task.zip file
into this folder, and also add the home folder where the Jacode executable
is located into your PATH environment variable.

You can read more about the Jacobe Ant task here:

http://www.tiobe.com/index.php/content/products/jacobe/jacobe_task_doc.html