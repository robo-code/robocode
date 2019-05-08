## Version 1.9.3.6 (08-05-2019)

### Bugfixes
* [Bug-404][]: Confusion between development/non-development versions of bots
	* Rollback of previous attempt to fix issues with the RobocodeEngine, which could not read robots in "developer mode" (marked with a asterix character). Hence the old bug [Bug-398][] has been reintroduced.
* [Bug-406][]: DNS interaction is not blocked by Robocode's security manager
* Fixed issue where compiled robots could not be found. Added -encoding UTF-8 to the compiler options, and corrected -version option into -verbose.

### Changes
* Fix by Bumfo, which makes Robocode faster at detecting robots in the robot folder, which is crucial for the RoboRumble, when installing or updating a huge amount of robots.

## Version 1.9.3.5 (02-03-2019)

### Bugfixes
* [Bug-402][]: roborumble.sh etc. has incorrect class path
	* This was causing codesize utility not being able to work with roborumble. And unfortunately, this error was silenced so the user wouldn't notice that at all.
* [Bug-403][]: MouseEvent returning wrong position''
* [Bug-405][]: Fix #405 unnecessary FileNotFound exception
* Rumble: skip bots that fails to download when calc code size
* Credits to Xor and Bumfo for all these fixes. :-)

### Changes
* Added support for Kotlin. Just put the kotlin-stdlib-1.xxx.jar into the /libs folder to allow robots coded in Kotlin.

## Version 1.9.3.4 (05-01-2019)

### Bugfixes
* [Bug-401][]: RoboRumble client: Biased Melee prioritized pairings
	* All credits goes to Xor for this fix. :-)
* An error was thrown every time Robocode checked for a new version. The reason being that the Robocode web page has been moved.
* Undo/Redo with the Robocode Editor got broken with Java 9+.

### Changes
* Upgraded the Codesize tool to version 1.2, which support newer versions of Java (9 experimental) and Java 8 lambdas.
* The Robocode site is now using HTTPS instead of HTTP, and have been moved permanently to https://robocode.sourceforge.io (".io" instead of ".net")
* Changed the Maven build files to use the Maven Wrapper (mvnw).

## Version 1.9.3.3 (10-09-2018)

### Bug fixes
* Fixed issue with the RobocodeEngine, which could not read robots in "developer mode" (marked with a asterix '*' character)
* [Bug-395][]: Roborumble client duplicates battle results on network error.
* [Bug-397][]: Robocode UI cannot remember battle settings upon restart.
* [Bug-399][]: RANDOMSEED option does not support Java 8.
	* Thanks goes to Xor for all 3 fixes above. :-)
* [Bug-400][]: Problem to compile Robot.
    * The compiler options have been cut down to include only the -verbose option per default.

## Version 1.9.3.2 (04-04-2018) The Java 10 support release

### Bug fixes
* Fixed problems with shell and Mac OS commands files, which contained ^M (Microsoft windows) characters.
* Fixed issue with sample.SittingDuck, which got a security violation due to new security restrictions introduced with Java 10.

### Changes
* Upgraded Eclipse Compiler for Java (ECJ) to newest version 3.14.0.v20180305-0940.
	* The older version caused problems with compiling robots on Java 9 or newer.

## Version 1.9.3.1 (17-03-2018)

### Bug fixes
* [Bug-393][]: More frequent roborumble server checks.
     * The check for new robots was changed from 2 hours to 10 minutes.
     * Thanks goes to Julian Kent (aka Skilgannon) for this fix.
* [Bug-394][]: HiDPI scaling causes visual glitches.
* Fixed issues with creating and packaging robot teams.

## Version 1.9.3.0 (17-10-2017) The Java 9 support release

### Bug fixes
* [Bug-386][]: Can't run Robocode 1.9.2.6 on Mac OS.
	* Java 9 issue, which was fixed by bug fix for Bug-391. Thanks goes to MultiplyByZer0
* [Bug-387][]: Not printing in console when a bot is force stopped.
	* Thanks goes to MultiplyByZer0 and Julian Kent (aka Skilgannon) for the fix. :-)
* [Bug-388][]: UI exception in team battles.
* [Bug-389][]: Third-party team JARs broken with Java 9
* [Bug-390][]: Bad CPU constant calculation on Java 9.
* [Bug-391][]: Fix for "Illegal reflective access".
* [Bug-392][]: Bullets of the same bot collide at low bullet powers and high gun-cooling rate.

## Version 1.9.2.6 (01-01-2017)

### Bug fixes
* [Bug-381][]: Improve feedback after entering empty package name during robot creation.
* [Bug-382][]: Unable to run robocode.bat -- AccessControlException.
* [Bug-383][]: Java 8 lamba expressions cause ClassNotFoundException.

### Changes
* Upgraded Eclipse Compiler for Java (ECJ) to version 4.6.1

## Version 1.9.2.5 (30-Dec-2015)

### Bug fixes
* [Bug-378][]: robocode.robocodeGL.system.GLRenderer ClassNotFoundException.
* [Bug-380][]: Yet another historical bot related bug.
* Robot Editor: Source files were set to modified (starred) as soon as they were loaded. The modification state remained, even when no changes were made to the source file or when undoing all changes. Therefore the editor would always bring up a dialog with a warning, that the source file needed to be saved before closing the editor.

### Change
* [Req-160][]: Fixed width font in console. Some systems did not used monospaced font in console windows.

## Version 1.9.2.4 (20-Nov-2014)

### Bug fixes
* [Req-159][]: Fix overkilled garbage collection on static fields.
	* Robocode garbage collected practically any static field on a robot to prevent memory leaks (see [Bug-244][]).
* [Bug-375][]: Wrong width and height returned for .NET robots.
	* The .NET API return 40 instead of 36 with the Width and Height properties.

### Changes
* Upgraded Eclipse Compiler for Java (ECJ) to version 4.4.
* Upgraded jni4net to version 0.8.7.0.

### Other
* Got rid of various issues (e.g. license headers) with the source files of Robocode in order to create a Debian distribution of Robocode.
	* Thanks goes to Markus Koschany for all the help and support to make this possible.

## Version 1.9.2.3 (15-Sep-2014)
* [Bug-374][]: Wrong size report for minirumble in v1.9.2.2.

## Version 1.9.2.2 (03-Sep-2014)
* [Bug-373][]: Wrong robot size calculation in version 1.9.0 - 1.9.2.

## Version 1.9.2.1 (21-May-2014)

### Bug fixes
* [Bug-371][]: High cpu usage on editor.
* [Bug-372][]: Cannot load battle file when ``-DNOSECURITY=true`` is enabled - ``java.lang.IllegalAccessError``.

## Version 1.9.2.0 (23-Apr-2014)

### New feature
* Initial positions and headings added to the Control API.
	* A new constructor has been added to the ``BattleSpecification`` for the ``RobocodeEngine`` so that it is possible to set the initial position and heading of each participant robot. This new constructor takes an additional parameter named ``initialSetups`` that is an array of ``RobotSetup`` instances.
	* The ``RobotSetup`` is a new class introduced with this version that contains the initial position and heading for an individual robot.

### Bug fixes
* [Bug-370][]: Robot Packager cannot find robot .properties file in development path.
* Fixed issue with the Robot Editor, where the caret position was not updated fast enough when typing very fast.

## Version 1.9.1.0 (22-Mar-2014)

### Bug fixes
* [Bug-366][]: Receiving enemy's real name on HitByBulletEvent.
	* The previous fix was not correct for the owner and victim for the HitByBulletEvent and HitBulletEvent.
* [Bug-368][]: Issues with sentries
	* All listed issues have been fixed.
	* A bullet from a sentry robot will not be able to hit another bullet within the "safe zone" any more, but will still be able to hit another bullet within the sentry border area.
	* When sentry robots are present on the battle field, the initial random positions will be located within the safe zone. This avoids robots from being punished by sentry robots from the start of the battle, as the robot did not enter the sentry border by itself.
* [Bug-369][]: RoboRumble: NoClassDefFoundError for CodeSizeCalculator.
* The missing property ``robocode.battle.sentryBorderSize`` has now been added to the .battle file format.
* Fixed the twinduel.sh file which was using a invalid file name for the configuration file.
* Added desktop files for FreeBSD.

### New features
* Due to one of the ideas in [Bug-368][], a new method ``getNumSentries()`` for Java and a ``NumSentries`` read-only property for .NET was added to the ``Robot`` and ``RobotStatus`` classes, and to the ``IBasicRobotPeer`` interface as well.

### Changes
* Upgraded Eclipse Compiler for Java (ECJ) to version 4.3.1.

## Version 1.9.0.0 (16-Feb-2014) The Border Sentry Robot Release

### Bug fixes
* [Bug-364][]: Robot Packager does not include the robot data dir in the .jar file.
	* The Robot packager has now been extended with an option to include data files too when packaging a robot.
* [Bug-366][]: Receiving enemy's real name on HitByBulletEvent.
* Fixed various issues with the Robot Editor, e.g. letters written in the wrong error when typing text fast.
* Fixed issue when loading a robot jar file with no package (default package), which was found by Daniel Yeung, who also had a fix for it. :-)

### New features
* *BorderSentry interface*: Added a new robot type (the BorderSentry) that allows you to implement border sentry robots, which are robots that guards the walls of the battlefield against "wall-crawlers" and "corner" robots.
	* The Border Sentry robot is dedicated to Liam Noonan from Limerick Institute of Technology and the Robocode Ireland event in general. :-)
	* The inspiration for this type of robot comes from the TV series named [Robot Wars](http://www.ukgameshows.com/ukgs/Robot_Wars).
	* Note that border sentry robots...
		* have 400 additional energy points meaning that a border sentry robot starts with 500 energy points!
		* only appear at the borders of the battlefield, when a new round is started defined by the "border sentry size", which is a game rules that can be adjusted similar to other game rules. The default border sentry size is 100 units.
		* can only make damage to other robot types within the *sentry border*, but not to robots in the "safe zone", which is the area inside the sentry border.
		* can receive damage from other robots. But robots will not gain energy points from the border sentry robots (due to Mathew Nelson's excellent point regarding using sentry robots as "Energy Batteries" ;-) ).
		* does not receive scores in the Ranking Panel nor Battle Results, even though you can see its score, like ramming damage, bullet damage etc. The goal for the border sentry robot is to be a referee, but not a player that can win or loose the battle.
	* A new method names ``getSentryBorderSize()`` has been added to the Robot classes that returns the size / attack range from the edge of the borders, where BorderSentry robots are able to hit other robot types.
	* A new method named ``isSentryRobot()`` has been added to the ``ScannedRobotEvent`` event class.
	* When there is only one regular robot left on the battle field with one or more border sentry robots, the round is	automatically ended.
	* A new sample robot has been added named ``samplesentry.BorderGuard``. Try it out against your robot(s) or e.g. ``sample.Corners`` and ``sample.Walls``.
	* An additional option has been added to the View Options in the Preferences to enable and disable the visual appearance of the sentry border, which is painted in transparent red (danger zone).
* [Req-156][]: Codesize added to properties file.
	* The Robot Packager now adds a new property named ``robot.codesize`` which will be set to the value calculated by the built-in Codesize tool when compiling the robot and/or team. The Codesize value is the effective code size measured in bytes which is used by e.g. the RoboRumble to categorize robots into MicroBot, MiniBot and MegaBot etc.
* Improved the names of the menu items on the Robot menu.

## Version 1.8.3.0 (04-Oct-2013) The Editor Theme Release

### Bug fixes
* [Bug-363][]: No Last Survivor Bonus being given.
* The ``getWidth()`` and ``getHeight()`` methods on the Robot class have always returned 40, even though a robot is 36x36 pixels in size.
	* Hence, ``getWidth()`` and ``getHeight()`` have been changed to return the correct width and height, which is 36.
* Fixed Robot Editor issues:
	* ``NullPointerException`` occurring with a new source file that was not saved before being compiled.
	* Issue where the caret position was set to the end of a source file when reading it into the editor.

### Changes
* [Req-121][]: Issues with editor font.
	* A new Editor Theme Configurator has been added the allows changing the font, text colors and styles for the Robocode Editor. 
	* Two built-in themes have been provided for Robocode:
		* Robocode White Theme
		* Robocode Black Theme
	* When selecting a theme, Robocode will automatically use this theme the next time Robocode is started.
	* When changing a theme, a new custom theme can be created and saved. Note that it is possible to overwrite existing themes.
	* Editor themes are stored within the /theme/editor directory as theme property files, which makes it possible to modify these in a normal text editor, but also share the themes.
	* The Editor Theme Configurator is available from the Robot Editor menu under View -> Change Editor Theme.
* The line numbers area in the Robocode Editor has been improved as well:
	* The background and text colors for the line numbers can be changed.
	* Line numbers are now right aligned + extra space has been added to the right.
* The Find & Replace dialog has got a better layout.

## Version 1.8.2.0 (22-Jun-2013)

### Bug fixes
* [Bug-357][]: Tab characters are inserted in the last line of a robot source file when opening it.
	* This bug was only partially fixed with version 1.8.1.0.
* [Bug-358][]: Robot in default package cannot write to files. Should at least get a warning.
* [Bug-361][]: Problem in the text editor related with the .java file modification.
* [Bug-362][]: Rumble client does not remove participants in wrong codesize group.
	* Thanks goes to Julian Kent ("Skilgannon") for providing a solution for this. :-)

### Changes
* Improved the UI regarding Battle Rules:
	* All Battle Rules have been assembled into one single Rules tab.
	* Number of Round has been moved from the New Battle dialog to the Rules tab.
	* Improved the visualization of the battle field size.
	* Added input validation to text fields.
* [Req-64][]: Change default battle settings like e.g. "Number of Rounds".
	* When the battle rules are changed in the user interface, Robocode will remember these as the user's default settings.
	* A new Restore Defaults button has been added to get the game default settings back.
* Source code editor is now always maximized when opening and robot file or creating a new one.

## Version 1.8.1.0 (24-Mar-2013)

### Bug fixes
* [Bug-335][]/[Bug-336][]: Skipped turns ... issues.
* [Bug-349][]: Instances of RobocodeEngine don't seem to be independent - memory leak and performance decrease.
* [Bug-350][]: Bullet id from battle record XML file is sometimes -1 causing a NumberFormatException.
* [Bug-351][]: Robot.onBattleEnded(BattleEndedEvent) provides wrong scores.
* [Bug-352][]: Results from BattleCompletedEvent.getIndexedResults() are always sorted.
* [Bug-353][]: RobocodeEngine.setVisible() can cause a NullPointerException.
* [Bug-354][]: Replaying an XML record can cause an ArrayIndexOutOfBoundsException.
* [Bug-355][]: Priority battles not accepted for mini/micro/nano rumbles.
* [Bug-356][]: Update Roborumble URLs from Darkcanuck to LiteRumble.
* [Bug-357][]: Tab characters are inserted in the last line of a robot source file when opening it.

## Version 1.8.0.0 (30-Jan-2013)

### Bug fix
* [Bug-346][]: Cannot extract sources from robot packages.
* [Bug-348][]: .NET: UnauthorizedAccessException in AppDomainShell.Dispose().

### Changes
* Robocode has been updated to Java 6, and hence this will be the new minimum requirement for running Robocode from this version.
* The icon for Robocode was upgraded to a 256x256 pixel resolution.
* Added the new properties ``-DlogMessagesEnabled=true|false`` and ``-DlogErrorsEnabled=true|false`` for enabling and disabling log messages and log errors.
	* The robocode.control.RobocodeEngine has got two similar methods: ``setLogMessagesEnabled(boolean)`` and ``setLogErrorsEnabled(boolean)``

## Version 1.7.4.4 (21-Nov-2012)

### Bug fix
* [Bug-347][]: /bin/sh^M bad interpreter.
	* All .sh and .command files contained the Windows ^M characters so these files could not be executed.

## Version 1.7.4.3 (17-Nov-2012)

### Bug Fixes
* [Bug-344][]: BattleAdaptor missing in ``robocode.control.events``.
* [Bug-345][]: Graphics still being rendered when minimized.
* [Bug-333][]: .NET runs release dll not debug dll so can't debug.
	* Implemented a work-around so that the local robot repository is rebuild when removing a development path.

### Changes
* Robocode sources are now put on [GitHub](https://github.com/robo-code/robocode).
* Upgraded the .NET plug-in to use Visual Studio Express 2010.
* The versions.txt has been changed to versions.md:
	* The versions.txt was converted into [Markdown][] syntax in order to make it easier to convert into e.g. HTML, and because GitHub will automatically translate it into HTML when viewing this file in a browser.
		* Thus, this file has been renamed from 'versions.txt' into 'versions.md'.
		* The web page with the new versions.md can be viewed [here](https://github.com/robo-code/robocode/blob/master/versions.md).
	* In addition, links to all reported bugs and feature requests have been inserted into versions.md so it is easy to browse to the reports and requests to find more details.

## Version 1.7.4.2 (16-Aug-2012)

### Bug Fixes
* [Bug-338][]: "Accept-Encoding: gzip" not in Roborumble HTTP Headers.
* [Bug-339][]: All Text Missing.
	* Reverting fix for [Bug-332][] ("Use OpenGL backend under linux"), which caused too much trouble.
* [Bug-340][]: Robocode crash on window resize (linux-opengl).
	* Reverting fix for [Bug-332][] ("Use OpenGL backend under linux"), which caused too much trouble.
* [Bug-341][]: InteractiveRobots gets error "After the event was added...".
* [Bug-342][]: New bots not given priority.

### Changes
* The built-in [RoboRumble][] client is now able to decompress "gzip" and "deflate" content with HTTP connections with RoboRumble servers that supports it.

## Version 1.7.4.1 (02-Jul-2012)

### Bug Fixes
* [Bug-337][]: Hangups with New Editor in 1.7.4.0.
	* Notice, that the editor can still be quite slow with large source files. Work is still in progress to speed it up.
	* Several other minor bug fixes with the new editor, like e.g. undoing and redoing tab indentation.

## Version 1.7.4.0 (07-Jun-2012)

### Bug Fixes
* [Bug-332][]: Use OpenGL backend under linux.
	* The ``-Dsun.java2d.opengl=True`` property has been set up per default in the "robocode.sh" file in order to enable OpenGL hardware acceleration per default for Linux and Solaris.
* [Bug-334][]: Snapshot API never shows bullets in "HIT_WALL" status.
* [Bug-328][]: Issue with the robocode.dll + \# chars in the path for a dll.
	* This applies to the .NET plug-in.

### New Features
* [Req-147][]: The snapshot API is ambiguous for bullets shot by teams.
	* Two new methods have been added:
		* [IRobotSnapshot.getRobotIndex()][] that returns a unique id between all robots participating in a battle.
		* [IRobotSnapshot.getTeamIndex()][] that returns a unique id between all participating robots or -1 if a robot is not a member of a team.
	* The existing [IRobotSnapshot.getContestantIndex()][] will return the team id if it is not -1, otherwise the robot id is returned.

### Changes
* The Robocode Editor (source code editor) has been completely replaced by a new and improved editor. Expect some bugs!

## Version 1.7.3.6 (29-Apr-2012)

### Bug Fixes
* [Bug-331][]: RoboRumble client has infinite timeout.
	* Default *connection timeout*, *read timeout* and *session timeout* have been set to 10 seconds.

### New Features
* A new config file has been added for [RoboRumble][] in the roborumble directory in the robocode directory named "roborumble.properties".
	* With this config file it is possible to configure the *connection timeout*, *read timeout* and *session timeout* used when downloading robots and uploading results.
* [Req-144][]: Mac ... start with icon and name.

### Changes
* Upgraded Eclipse Compiler for Java (ECJ) to version 3.7.2.

## Version 1.7.3.5 (11-Mar-2012)

### Bug Fixes
* [Bug-326][]: Package of team fails to load in team battles.
* Fixed minor issue where the Robot Packager tells that a NanoBot can be 250 bytes and a MiniBot 750 bytes, when these must be lesser than 250 and 750 bytes.

## Version 1.7.3.4 (04-Dec-2011)

### Bug Fixes
* [Bug-323][]: Robocode can't find the ECJ (Eclipse Compiler for Java)
* [Bug-319][]: Package name allows bad chars.
	* This issue was not fixed entirely. Corrected text regarding use of lower-case letters in package name.
* Bug: Changes to the rendering options did not take effect immediately for the battle view.

### New Features
* [Req-134][]: Calculate codesize after compile in editor.

### Changes
* Upgraded Eclipse Compiler for Java (ECJ) to version 3.7.1.

## Version 1.7.3.3 (05-Nov-2011)

### Bug Fixes
* [Bug-311][]: ``out.write(int)`` uses up allocated printing quickly.
* [Bug-315][]: Unable to change drawing color in .NET (C\#).
* [Bug-318][]: Installer throws ``NumberFormatException`` on Linux 3.0.
* [Bug-319][]: Package name allows bad chars.
* [Bug-320][]: "About" window colors are awful.

### Changes
* The layout of the About Box has been completely redesigned.
* If running on Java 1.6 or newer, OS/system based font antialiasing will be enabled.
* Upgraded to Maven 2.2.1 used for building Robocode.
	* Cleaned up the build, improved the speed, and got rid of issue with building and testing every twice.
* Upgraded Eclipse Compiler for Java (ECJ) to version 3.7.
* Upgraded jni4net (Java <-> .NET bridge) to version 0.8.6.0.

## Version 1.7.3.2 (23-Aug-2011)

### Bug Fixes
* [Bug-313][]: Robocode .NET does not work on Java 7.
* [Bug-312][]: Enabling Paint Freezes Robocode.
* Fixed problem with XML serialization of just killed robot.
* Fixed bug in temporary record cleanup.

### New Features
* Implemented deserialization of XML with short tags.
	* The command line argument ``-replay`` now supports zipped XML.

### Changes
* Various optimizations to let [RoboRumble][] start up and run faster.
	* E.g. sound module and image loading is disabled when running the rumble.
* Imroved XML deserialization.
* [Patch-1][]: Improved priority battle handling.
	* Patch by Jerome Lavigne ("Darkcanuck") - Improved priority battle handling in [RoboRumble][].
	* This change will help improve the efficiency of the [RoboRumble][] system and allow battles to be distributed more evenly + fix some inconsistent console output.
	* Improves [RoboRumble][] client-side processing of priority battles received from the rumble server. Specifically:
		1. Priority battles will be discarded for bots that the client knows are no longer in the participants list (the client is aware of bot removals before the server is).
		2. Duplicate priority battles received from the server will be discarded (the server does not maintain state of which battles are sent to specific clients, so duplicates can be common).
* Upgraded to jni4net 0.8.5.1 (fixes [Bug-313][] above).

## Version 1.7.3.1 (28-Jul-2011)

### Bug Fixes
* [Bug-302][]: Hide enemy name implementation bug.
* [Bug-303][]: ``bullet.equals`` semantic has been change in 1.7.3.0 version.
* [Bug-304][]: ``setColor(null)`` causes NPE.
* [Bug-305][]: TeamRumble priority battles bug.
* [Bug-306][]: Rumble sh scripts for launching do not handle spaces in path.
* [Bug-307][]: Console output cannot handle non-ascii names.
* [Bug-308][]: ``ConcurrentModificationException`` in ``URLJarCollector``.
* [Bug-309][]: robot in development generates * into filename.
* [Bug-310][]: Interface Robot skips turns at end of round.
* Bug: ``BulletHitEvent.getBullet().getVictim()`` always returned null.

### New Features
* [Req-135][]: Twin Duel configuration files.
	* Twin Duel configuration files for the [RoboRumble][] client are now included.

### Changes
* The Robot Editor is now saving and loading source files in UTF-8 (Unicode).
* Upgraded to use jni4net v0.8.4.

## Version 1.7.3.0 (20-May-2011)

### Bug Fixes
* [Bug-301][]: ``getTurnRateRadians`` incorrect for negative velocity.
* Fixed issues with unit-testing and building Robocode on Linux.

## Version 1.7.3.0 Beta (26-Mar-2011)

### Bug Fixes
* [Bug-222][]: Some of sound not working.
	* The gunshot sound was not working.
* [Bug-297][]: x,y coords between ``BulletHitEvent`` & ``HitByBulletEvent`` differ.
* [Bug-299][]: Custom events no longer firing after clearing event queue.
* Fixed typo in the documentation with valid range of values for the battlefield width and height.
	* Thanks goes to Tam�s Balog for pointing this out.

### New Features
* Added "Enable auto recording" option to the Common Options, which automatically generates a zipped XML record for every battle.
	* Works only when "Enable replay recording" is enabled (is automatically set when enabling auto recording).
* [Req-124][]: Ability to save the properties file for robots in dev. path.
* [Req-129][]: ``Rules.getBulletSpeed``.
	* It is about keeping the bullet power within 0.1 - 3.0, even when input is lesser or greater than this valid range.
* [Req-128][]: In battle name hiding.
	* A general solution has been provided. A new game rule, "hide enemy names", can be enabled or disabled.

### Changes
* Upgraded to use jni4net v0.8.3.

## Version 1.7.2.2 (04-Nov-2010)

### Bug Fixes
* Fixed "3 * PI / 4 means West" in the Robot API, which should be "3 * PI / 2 means West".
	* Thanks goes to Gottl Johannes for pointing this out.

## Version 1.7.2.2 Beta (02-Oct-2010)

### Bug Fixes
* [Bug-290][]: Development Options remove wrong item.
* [Bug-291][]: JavaDoc missing ``_Robot`` and similar
* [Bug-292][]: Robot PrintStream doesn't handle write in a portable fashion.
* [Bug-293][]: Wrong headings with the JuniorRobot.
	* Thanks goes to Mr. Kschan for finding and providing a fix for this bug. :-)

### New sample robots
* Tuan Anh Nguyen provided a modified version of the interactive sample robot named ``Interactive_v2``, which use absolute movement (up, left, down, right) on the screen that can be easier to control than the original ``Interactive`` sample robot.
	* Both Interactive robots now use both the arrow keys and the W, A, S, D key for moving the robot.
* Sample robots have been written for the \#F language which are put in the "/samples/SampleFs" directory.
	* These \#F sample robots are available with the .NET plug-in and comes along with Visual Studio project files.

### New Features
* Implemented Robocode .NET Control API plug-in (robocode.control.dll) with documentation.
* [Req-115][]: Installed package should contain readme file.
	* Added ReadMe.txt (using Markdown syntax) and ReadMe.html.
	* The ReadMe is available from the Help menu.
* [Req-118][]: Enable/disable development paths.
	* Check boxes for each of the entries in has been added to the Preferences -> Development Options.
* [Req-119][]: Provide JuniorRobot template for inexperienced users

### Changes
* Robocode has been upgraded to use jni4net v0.8 with CLR 4.0 support.
* It is not necessary to specify ``-Dsun.io.useCanonCaches=false`` anymore with the startup scripts for Robocode and [RoboRumble][] as this property is now automatically set internally when running Robocode under Windows.
	* Hence, this property has been removed from all .bat, .sh, and .command files for Robocode.
* Robocode will not notify about Beta versions anymore per default.
	* However, a new option in the Common Options (under Preferences) makes it possible to enable/disable notifications about new Beta versions.
* The tabs for the "Common" and "Development Options" in the Preferences window are now the first tabs.

## Version 1.7.2.1 (05-Aug-2010)

### Bug Fixes
* [Bug-283][]: Possible for robot to kill other robot threads.
* [Bug-287][]: Zipped robots data files are not extracted.
* [Bug-284][]: Robot Packager doesn't package source file in Eclipse proj.
* [Bug-215][]: Missed onRobotDeath events.
	* Thanks goes to ForNeVeR for finding and solving this bug.
* [Bug-282][]: Cannot see robot with no package in New Battle dialog.
* [Bug-285][]: Robot Packaging Wizard doesn't save value for Next & Back.
* [Bug-286][]: ``ClassNotFoundException at RobotClassLoader.java:271``.
* [Bug-288][]: "skipped" turns at start with ``-Ddebug=true``.
* [Bug-289][]: Exclude filters not working.
* Bug: Sometimes the compiler window was hanging for several seconds, even though the compiler had finished compiling.

### Changes
* The Robot Packing Wizard will now only allow word characters (letters, digits, dots, *but not spaces*) with the version field.
* Added ``robocode.annotation.SafeStatic``, which can be used to suppress warnings in the robot console for a robot that uses a static Robot reference on a field.
	* Note that this means that your robot will need to clean the static field, when a new round is started.
	* This annotation was suggested by Nat Pavasant:

			@SafeStatic
			private static AdvancedRobot robot;
* Added syntax highlightning on Java annotations for the Robot Editor.

## Version 1.7.2.1 Beta (19-Jun-2010)

### Bug Fixes
* [Bug-231][]: Lockup on start if too many bots in robots dir (cont'd).
	* Additional fixes were made.
* Fixed some issues with the robot repository when file URLs contains spaces, where robots were sometimes disappearing from the repository.

### Changes
* [Req-99][]: Move away from Jikes towards ECJ.
	* Jikes 1.22 has been replaced with the Eclipse Compiler for Java (ECJ) 3.5.2.

## Version 1.7.2.0 (28-May-2010) The ".NET robots are now supported" release

### Bug Fixes
* Bug: Robots were disabled when skipping 30 skipping non-consecutive turns (or 120 turns when performing I/O operations).
	* Now, Robocode is only disabling robots when the robot skips 30 consecutive turns, which is the original behavior, and which has been broken since version 1.6.1.4.
* [Bug-275][]: Duplicate version numbers prevents uploading.
* [Bug-276][]: ``tzu.TheArtOfWar 1.2`` gets ``NullPointerExceptions``.
* [Bug-278][]: Attempting to install robocode over an existing install NPEs.
* [Bug-281][]: The robocode.command is missing the execute permissions bit.
* [Bug-277][]: Problems with ``Graphics2D.fill/draw(Shape)``.
* [Bug-280][]: NPE when uploading results.
* [Bug-274][]: "Ignoring" messages in rumble are duplicated.
* [Bug-269][]: Minor visual bug - Currently selected robot gets covered.
* Bug: A security exception was thrown with development robots trying to access their data directory.

### Changes
* Robocode will now only install batch files relevant to the hosting system, e.g., .bat files are not installed under Unix/Linux/Mac OS X, and .sh files are not installed under Windows.
* Furthermore, .command files are only installed under Mac OS X, and has now the execution permission bit set (due to [Bug-281][]).
* Robocode will now write out a warning in the robot console when a robot is detected that uses static references to a robot, i.e. static fields that are declared as e.g. a ``Robot``, ``AdvancedRobot``, ``TeamRobot`` etc. (typically references to the robot itself).
	* A static reference to a robot can cause unwanted behavior with the robot accessing these. Hence, it is strongly recommended that static robot references in a robot class or within its helper classes are changed into non-static references and that the robot is recompiled afterwards.
	* In every round, Robocode is creating new instances of the individual robot, meaning that static references to former ``Robot`` objects points to "dead" ``Robot`` objects that are not used anymore in the game, and which cannot be garbage collected during the battle when there is static references to these.

## Version 1.7.2.0 Beta 3 (29-Apr-2010) The ".NET robots are now supported" release
Thanks goes to Alex Schultz for keep finding bugs, but also helping out solving these. :-)

### Bug Fixes
* [Bug-271][]: Battle engine consumes more CPU power over time.
* [Bug-267][]: Strange issue first time running roborumble in 1.7.2.0 Beta2.
* [Bug-272][]: ``isTeammate()`` sometimes returns false with teammates.
* [Bug-263][]: Cannot extract downloaded robot for editing in Robot Editor.
* [Bug-270][]: Strange thread exceptions with kid.DeltaSquad in 1.7.
* [Bug-228][]: ``Krabb.sliNk.GarmTeam 0.9v`` locks up in new beta.
* [Bug-265][]: Occasionally losing the bit of text in the robot console.
* [Bug-255][]: ``java.lang.Error: Interrupted attempt to aquire read lock``.
* Bug: Teams with versions could not be found in a development path.
* Bug: Files in ``/robots/.data`` or ``/robots/.robotcache`` directory were put into an underscore sub-directory.
* Bug: The "Kill Robot" button in the robot console windows was often disabled.

### Changes
* The robot console now uses a rolling buffer so when the number of lines in the robot console exceeds the max number of lines (500), the oldest lines are removed, and ``^^^ TEXT TRUNCATED ^^^`` is written in the top.
	* Previously half of the text was truncated when the max number of lines was reached.

## Version 1.7.2.0 Beta 2 (14-Mar-2010) The ".NET robots are now supported" release

### Bug Fixes
* [Bug-254][]: Roborumble doesn't upload with ``EXECUTE=NOT``.
* [Bug-257][]: Team RoboRumble uploading is broken.
* [Bug-262][]: TeamRumble: Cannot find robot in nested .jar files.
* [Bug-244][]: Robot static data isn't being GCed after battle.
	* Additional fixes were made to solve this issue.
* [Bug-258][]: ``isTeammate()`` called on null gives ``NullPointerException``.
* [Bug-260][]: ``ArrayIndexOutOfBoundsException`` when starting team battle.
* [Bug-250][]: Installer installs ``AutoExtract$1.class``.
* [Bug-252][]: ``yk.JahRoslav 1.1`` throws ``WinException``.
* [Bug-259][]: ``jlm.javaDisturbance`` loses substantial score in 1.7.2 Beta.
* [Bug-261][]: (.NET) condition tested on concurrently modified collection.

### License Change
* We have changed license from Common Public License (CPL) v1.0 into Eclipse Puplic License (EPL) v1.0.

## Version 1.7.2.0 Beta (15-Feb-2010) The ".NET robots are now supported" release

### Bug Fixes
* [Bug-244][]: Robot static data isn't being GCed after battle.
* [Bug-245][]: Removing directories from "development options" doesn't work.
* [Bug-247][]: Version ordering is somewhat strange with letters.
* [Bug-243][]: Robot console text sometimes disappears.
* [Bug-240][]: ``morbid.MorbidPriest_1.0`` fails to load.
* Bug in ``RobotClassLoader`` causing ``ClassNotFoundException`` for some robots during robot class load.
* Fixed a ``NullPointerException`` occuring when Robocode is (re)loading the robot repository after a developer path has been removed.

### New Features
* .NET Robocode plug-in.
	* It is now possible to code robots in .NET and let them engage in Robocode battles. :-)
* [Req-101][]: ``onRoundEnded()``.
	* It is now possible for robots to get notified when a round has ended.
	* The ``onRoundEnded()`` event handler receives a new ``RoundEndedEvent`` that contains information about the number of turns in the round and total turns in the whole battle when the event occurred.
	* The ``robocode.control.RoundEndedEvent`` in the Control API has been extended with a new method named ``getTotalTurns()`` similar to the new ``robocode.RoundEndedEvent`` for the Robot API.
* [Req-114][]: RateControlRobot vs. TeamRobot.
	* The ``RateControlRobot`` is now a ``TeamRobot`` meaning that it can participate in team battles.
* [Req-113][]: Skipped turn events.
	* Added ``getSkippedTurn()`` method on the ``SkippedTurnEvent`` class, which returns the turn that was skipped.
	* The ``SkippedTurnEvent.getTime()`` returns the time when the ``SkippedTurnEvent`` was processed by the robot, which is always later when the robot is skipping turns.
	* The message ``SYSTEM: you skipped turn`` has been improved, so it will tell which turn that was skipped like ``SYSTEM: john.Doh skipped turn 43``.
* [Req-80][]: Screenshot of battleview.
	* Press Ctrl+T on Windows and Linux, and Command+T for Mac OS in order to take a screenshot of the battle view.
	* The screenshot will be saved in the Robocode folder named "screenshots", and the filename will be a timestamp for when the screenshot was taken.
* [Req-89][]: Launch Robocode from .br (battle record) files.
	* Robocode is now able to launch from from .battle (battle specification) and .br (battle record) files in Windows.
* [Req-86][]: Rankings should be visible when Robocode is minimized.
	* A new View Option has been implemented to preventing speedup when minimized.
	* This new View Option can now be set in the Preferences by putting a check mark into "Prevent speedup when minimized".
* [Req-93][]: Rename ``/robots/.robotcache`` to ``/robots/.data``.

### Changes
* Browser support has been improved for Mac OS, Unix and Linux, which in most cases did not work properly.
   * In previous versions of Robocode, the browser.sh was used to start up the user's preferred browser. The browser.sh file is not being used anymore. Hence, this file can safely be removed from your Robocode installation dir.
   * Now, Robocode will start up a browser under Mac OS, Unix and Linux with no use of scripts.
* The Development Options dialog in the Preferences has been improved:
   * Changed from using single interval selection to multi selection interval.
   * Adding an existing path is ignored.
   * The list of path is automatically sorted.

## Version 1.7.1.6 (06-Jan-2010)

### Bug Fixes
* [Bug-237][]: OS X 10.6: Cannot run Robocode from robocode.sh.
	* It must be run from the new robocode.command file instead.
* [Bug-238][]: OS X 10.6: The editor cannot see the JD
	* It must be run from the new robocode.command file instead.
* The ``onDeathEvent(DeathEvent)`` method was called too late, when a new round was about to start. Not when the robot has died.
* [Bug-231][]: Lockup on start if too many bots in robots dir (cont'd)
	* Additional fix was made to locate multiple robots under the same package.
* Changed the robot painting so that everything that goes into the painting buffer is always painted, and remaining painting operations exceeding the buffer capacity are always dropped.
	* Previously, only the last painting operations exceeding the painting buffer were executed.

### New Features
* The amount of used memory and total memory is now shown in the title bar of Robocode.
	 * This is useful to see how much memory your robots are consuming.

### Changes
* If the robot paints too much between actions, an improved error message is written out in the robot console.
	* But from this version of Robocode this message is only written out once.
* A ``SecurityException`` is now thrown if a robot exceeds its max. data file quota, meaning that it uses more than 200.000 bytes its data files in total.

## Version 1.7.1.6 Beta (10-Dec-2009)

### Bug Fixes
* [Bug-236][]: Robot Editor doesn't accept packagename with dot (.) in it.
* [Bug-234][]: Source is not included.
	* Additional fix was made for this issue.
* [Bug-231][]: Lockup on start if too many bots in robots dir (cont'd)

### Requests
* [Req-92][]: Scrollable properties.
	* All console windows including the Robot Console and Properties are now scrollable, and all have the same look.

### Changes
* It is now possible to add paths to robot project inside an Eclipse workspace under the Development Options in the Preferences.
	* Robocode will read the .classpath file in the project in order to locate both class files, properties files, and java files.
	* This means that the Robot Packager is now able to include your source files when you use Eclipse, and it supports multiple source paths.
	* **Note:** Robocode does not support linked sources or include and exclude filters.
* Changed the messages given when creating a new robot with the Robot Editor regarding robot name and package name.

## Version 1.7.1.5 (11-Nov-2009)

### Bug Fixes
* ``RobocodeFileOutputStream.getName()`` always returned ``null`` instead of the filename.
* Robots listed in e.g. the New Battle window was sorted like: 1.1.1, 1.1.10y, 1.1.2, 1.1.10 (alpha-numerical).
	* But version numbers with an ending letter like in "1.1.10y" like in the versions just listed, were not placed in the correct order.
	* Notice that 1.1.10y is between 1.1.1 and 1.1.2. This bug has been fixed so the versions in the example now will be sorted correctly like: 1.1.1, 1.1.2, 1.1.10, 1.1.10y.

## Version 1.7.1.5 Beta (14-Oct-2009)

### Bug Fixes
* [Bug-232][]: ``Graphics2D.getTransform()`` throws NPE.
* [Bug-233][]: "Teleport"
	* occurred when robot's distance remaining was very large.
* [Bug-234][]: Source is not included.
	* ``robot.java.source.included`` was not set in the robot.properties file.

## Version 1.7.1.4 (25-Sep-2009)

### Bug Fixes
* [Bug-226][]: ``java.io.FileNotFoundException`` in ``RobotFileSystemManager.init``.
* [Bug-227][]: Can't load ``Katana 1.0`` or ``DrussGT 1.3.1wilo``.
* [Bug-230][]: Lockup on start if too many bots in robots dir.
* [Bug-229][]: ``IllegalArgumentException`` on painting in some robots?
* Fixed ``NullPointerException`` that could occur with the ``-battle`` command-line option.

### Changes

#### Banning
* The previous 1.7.x.x versions have been very strict so that robots that could not be loaded, started, skipped too many turns etc. would be disallowed to participate in battles.
	* With the bug fix for [Bug-227][] above this policy has been changed so robots are only "banned" if they cause a security violation or they could not be loaded or started (meaning that they will not be allowed to run).
	* In addition, ALL security violations are always written out in both the main console and robot's console. A message will be written out in the main console like ``xxx has caused a security violation. This robot has been banned and will not be allowed to participate in battles``.

#### Painting
* With the bug fix for [Bug-229][] a change was made so a robot will now receive this message in its console window, if it is painting too much between actions:

		SYSTEM: This robot is painting too much between actions.  Max. capacity has been reached.
* Notice that a robot is not allowed to perform an unlimited amount of paint operations for two reasons:
	1. It takes up a lot of memory as the painting operations are recorded in a buffer before being processed, and potentially this buffer must be recorded to a file (for replays).
		* A robot is allowed to use up to a maximum of 64 KiB per paint action. An average painting operation like e.g. ``fillRect(x, y, width, height)`` takes up 15 bytes, meaning that more than 4000 painting operations should be possible per paint action, which is a lot.
	2. It takes a lot of CPU cycles to process the painting buffer to the display making the painting slow if the buffer is too large.
* It is possible to remove the limit for the robots painting buffer by using the existing command-line option: ``-Ddebug=true``.

## Version 1.7.1.4 Beta (26-Aug-2009)
This version is dedicated for the [RoboRumble][] community where many issues seen with the RoboRumble client have been solved.

Thank you all for reporting as many known issues as possible, and also help out solving these - especially with the issue seen with the robot movement that had a big impact on the scores and rankings! :-)

A big thanks goes to Patrick Cupka ("Voidious"), Julian Kent ("Skilgannon"), "Positive" and Nat Pavasant ("Nat") for their combined work with developing and testing the new and improved robot movement regarding acceleration and deceleration rules ([Bug-214][]). :-)

### Bug Fixes
* [Bug-214][]: Accel/decel rules introduced in 1.7.1.3 causes trouble.
* [Bug-215][]: Missed onRobotDeath events.
* [Bug-212][]: Team jar files reported as corrupted.
* [Bug-208][]: Does not extract .properties files into bot data dirs.
	* The previous fix for this issue did not work properly as the file sizes were truncated to 0 bytes.
* [Bug-216][]: Sometimes too few results for robots are displayed.
* [Bug-213][]: ``NullPointerException`` when setting classpath directory.
* [Bug-209][]: [Codesize] Invalid entry point in codesize-1.1.jar.
	* Previous fix was not working properly.
* [Bug-218][]: Robocode enters infinite loop with the Restart button.
* Bug: The RateControlRobot (Beta) returned rates in radians instead of degrees with the methods: ``getTurnRate()``, ``getGunRotationRate()``, and ``getRadarRotationRate()``.

### Changes for RoboRumble
* Updated the PARTICIPANTSURL in the roborumble.txt, meleerumble.txt, and teamrumble.txt.

## Version 1.7.1.3 (08-Jul-2009)

### Bug Fixes
* [Bug-210][]: Bullet and Ram Damage Bonuses are wrong.
* [Bug-208][]: Does not extract .properties files into bot data dirs.
* [Bug-207][]: Access denied ``javax.swing`` ``-DNOSECURITY=true``.
* [Bug-209][]: [Codesize] Invalid entry point in codesize-1.1.jar.
* Bug: Sometimes the "Show results when battle(s) ends" in the Common Options was disabled when running the RobocodeEngine, even though the setting had been enabled earlier.
* A ``NullPointerException`` occurred when closing the Preferences window, when no sound device is present in the system.

### Changes
* The default font on the ``Graphics`` context when using ``onPaint(Graphics2D)`` or ``getGraphics()`` has been changed to the "Dialog" font.

## Version 1.7.1.3 Beta (08-Jun-2009)

### Bug Fixes
* ``AdvancedRobot.setMaxTurnRate()`` did not work properly since version 1.5.4.
* [Bug-205][]: Wrong survival scores sent by rumble client.
* [Bug-206][]: Funny behaviors with robot graphics/painting.

### New RateControlRobot (Beta)
* Joshua Galecki has provided a new robot type, the ``RateControlRobot``, which is an extension of the ``AdvancedRobot``.
* The ``RateControlRobot`` class has been created in an attempt to allow more realistic robots.
	* That is, many real/physical robots are given commands in terms of rates ("move forward one meter per second"). Hence, the ``RateControlRobot`` helps simulating a real robot.
* With this release, we ask the community for feedback and we will announce it as stable in one of next major releases. So please report issues or change requests etc. for this new robot type.

### Changes for RoboRumble
* Added validation of each of the participant lines of the participant list.
	* If a participant line is invalid due to e.g. wrong format/syntax or bad URL, then an error message is written out and the participant is ignored.
* The format of the lines in the participant lines is the same as usual.
	* Accepted lines must follow this format: ``<robot+version>,(<http-url>|<repository-id>)``, where <robot+version> must match the regular expression ``[\\w\\.]+[ ][\\w\\.-]+``, the <http-url> must be a HTTP URL pointing at the robot .jar file, and the <repository-id> must be a number. The <http-url> and <repository-id> are mutual exclusive.
	* Example of accepted lines:

			johndoe.SomeRobot 1.0,http://somewhere.com/SomeRobot\_1\_0.jar
			johndoe.SomeRobot 1.0,321

* Removed the info message "Trying to download <botname>" from the console output.

### Other changes
* The Development Options has become more usable, as Ruben Moreno Montoliu has added a list of paths and buttons for adding and removing directories, full resizable.
* Updated the Javadoc with a note for the ``setAdjustXX()`` robot methods, which are ignored when turning with infinite values.

## Version 1.7.1.2 (25-May-2009)

### Bug Fixes
* [Bug-199][]: FontMetrics StackOverflowError.
* [Bug-200][]: ``Graphics2D.setFont()`` has no effect.
* [Bug-201][]: ``setMaxVelocity(lower than current) + reverse`` direction bug.
* [Bug-196][]: Wrong file path used for development bots.
	* Refixed.
* [Bug-202][]: Installer says to run robocode.jar.
* [Bug-204][]: Nanobot rumble not sending melee or team parameters.
	* Thanks goes to Jerome Lavigne ("Darkcanuck") for this fix.
* Fixed three bugs introduced with the Beta version found using new test units:
	* One bug where the speed would immediately drop to max. velocity when calling ``setMaxVelocity()`` - disregarding the acceleration and deceleration rules.
	* Another bug where the turn rate was a bit wrong when moving back and turning left in the same time.
	* A third bug where the robot continued to move backwards when it should have stopped due to ``setAhead(0)`` or ``setBack(0)``.

## Version 1.7.1.2 Beta (08-May-2009)

### Bug Fixes
* [Bug-139][]: Bug in RobotPeer.updateMovement?
	* Robocode was not moving 100% according to it's own rules. Now it is.
* [Bug-192][]: Fair Play!
	* With two identical robots in a battle against each other, the first robots was much more likely to win, which was not fair.
* [Bug-195][]: Client tries to remove all participants.
	* When RoboRumble, TeamRumble, and/or MeleeRumble downloaded the participants list and did not receive a ``HTTP_OK`` or an empty list, the participants list was emptied. This caused problems with the RoboRumble server.
* [Bug-196][]: Wrong file path used for development bots.
	Added option ``-DALWAYSUSECACHEFORDATA`` for anyone who liked it better.
* [Bug-197][]: Melee rumble doesn't use "smart battles".
	* Thanks goes to Jerome Lavigne ("Darkcanuck") for this fix.
	* Smart battles are also known as "priority battles".
* Fixed several ``ArrayOutOfBoundsExceptions`` occurring when starting a battle with fewer robots than a battle just played with more robots.

### Changes for RoboRumble
* The participants URL for RoboRumble, MeleeRumble, and TeamRumble have been updated to use the participant lists from the new RoboWiki:
	* [RoboRumble](http://robowiki.net/wiki/RoboRumble/Participants).
	* [MeleeRumble](http://robowiki.net/wiki/RoboRumble/Participants/Melee).
	* [TeamRumble](http://robowiki.net/wiki/RoboRumble/Participants/Teams).

### Other changes
* Updated the Help menu to point at the new RoboWiki regarding the Online Help and FAQ.
* RobocodeEngine control class: Added additional ``runBattle()`` method where it is possible to specify the initial positions of the robots when starting a battle.
* The about box was updated, where contributors are now written in bold and in a green color.

## Version 1.7.1.1 (10-Apr-2009)
With this release we have focused on eliminating as many problems seen with [RoboRumble][] as possible. Hence, this version should hopefully prove stable as the new RoboRumble client.

### Bug Fixes for RoboRumble
* [Bug-188][]: Meleerumble using 2 bots instead of 10.
	* Now the ``MELEEBOTS`` (number of robots that participate in a melee battle) works as expected.
* [Bug-193][]: TeamRumble uploading result for Robot instead of team.
* [Bug-187][]: Not enough java memory allocated in launch scripts.
	* The launch scripts for RoboRumble and TeamRumble were changed so it is allowed to use up to 512 MB heap memory, and MeleeRumble is allowed to use up to 1024 MB.
* [Bug-191][]: ``EOFException`` during repository rebuild.
	* Exception is now reported in a simplified way.
* [Bug-194][]: JarJar multi-registration.
	* RoboRumble was not reusing the same RobocodeEngine instance.

### Other bug fixes
* [Bug-169][]: ``pe.SandboxDT_3.02`` stopped working in 1.6.2 and later version.
	* We had broken semantics of ``getTeammates()`` to return empty array when there is no team.
* [Bug-189][]: API - cannot subclass Event in 1.7.1.
* [Bug-190][]: Errors with some robot classes when rebuilding database.
* [Bug-185][]: Webpage button lay over robot description.
* [Bug-186][]: Rounds number do not saved between run.

### JuniorRobot changes
* Bug - The event variables ``scannedXX`` and ``hitXX`` on the JuniorRobot was not reset to -1 (or -99) when nothing was scanned or hit.
* [Req-90][]: Prevent JuniorRobot to be unresponsive.
* **Change:** JuniorRobot will now automatically scan for enemies when it is not moving.

## Version 1.7.1 (24-Mar-2009)

### Bug Fixes
* [Bug-176][]: Editor UNDO does delete the line when no undo left.
* [Bug-180][]: Editor: Find (set cursor position).
* [Bug-182][]: roborumble.sh and teamrumble.sh are broken.
* [Bug-183][]: ``NullPointerException`` in ``BattlesRunner.runBattlesImpl``.
* [Bug-184][]: Custom event priority broken.

### Changes
* The New Battle dialog is now larger per default.
* The layout of the View Options and Sound Options tab in the Preferences has been improved.
	* An error dialog is shown when the input field for the desired TPS is out of range.

## Version 1.7.1 Beta 2 (11-Mar-2009)

### Bug Fixes
* [Bug-178][]: Typing to find bot no longer works.
* [Bug-181][]: API: Typo in Documentation ``onBulletMissed(BulletMissedEvent)``.
* [Bug-179][]: Event.setTime() method should not be hidden.
	* Put back ``setTime()`` method of ``Event``, the security is now handled other way instead of hiding the field.
* Fixed the problem with loading team robots from nested .jar files.
* On Ubuntu (Linux) the "Default window size" did not work properly.

### Internal Changes
* On SUN's JVM enabled caching of .jar files opened thru ``URLConnection``.
	* Solved problem with closing such files by implementing ``URLJarCollector``.

## Version 1.7.1 Beta (01-Mar-2009)
With this release we give BIG thanks to Nat Pavasant who have put in great effort in testing and finding as many bugs in Robocode as possible. He has been a great help with testing especially [RoboRumble][] functionality.

Currently, there is one known issue, which will be fixed with the next Beta or in the final release of version 1.7.1. This is critical for e.g. the TeamRumble, so please notice the known issue below.

### Known issue
* With this version Robocode cannot handle team packages containing robot .jar files, i.e. robot packages.

### Bug Fixes
* [Bug-166][]: Bots referencing ``robocode.robocodeGL`` broken.
* [Bug-168][]: Bots inconviently stop working if they go over time limit.
* [Bug-172][]: Robot console fails to display some deaths/wins.
* [Bug-174][]: Robot console is sometimes empty.
* [Bug-173][]: Robot packager can be activated once per running.
* [Bug-175][]: Development robots cause problems with data files.
* [Bug-171][]: A battleview size exceed 800x600 filled with black.
* [Bug-165][]: ``NullPointerException`` when using ``-battle`` option from cmd-line.
* [Bug-170][]: Robot Colors don't stick between rounds.
* [Bug-177][]: Open battle menu dialog is not loading robots.
* Bug: The command line usage of Robocode was not printed out at all when using the ``-?`` or ``-help`` option.

### New Features
* Added ``getCurrentWorkingDir()`` + ``getRobotsDir()`` to the ``robocode.control.RobocodeEngine``.
* [Req-86][]: Rankings should be visible when Robocode is minimized.
	* This feature has been added and it is possible to control the behavior from Common Options in the Preferences with the "Don't hide Rankings when main window is minimized", which is enabled per default.
* [Req-88][]: Command Line option for saving a battle record file.
	* Two new command line options have been added for Robocode, i.e. ``-record <filename>`` and ``-recordXML <filename>``.
		* The ``-record option`` records the battle as a binary and zipped battle record. Here I propose that the specified filename should is something like record.br.
		* The ``-recordXML`` does the same as the -record option, but saves the battle record as an XML file. Here I suggest that you use a filename like record.br.xml.
* [Req-49][]: Speedup time required for rebuilding robot database.

### RoboRumble Changes
* Changed the link for [http://rumble.fervir.com/rumble](http://rumble.fervir.com/rumble) into [http://darkcanuck.net/rumble](http://darkcanuck.net/rumble) as the RoboRumble server fervir is down and with unstable ranking.
	* The current ranking at Darkcanuck's server is alive. Almost all clients are point to Darkcanuck's now. Hence, it is updated now.
* [Req-82][]: Launch upload result in separate thread.
	* Results from the [RoboRumble][] client is now uploaded in a seperate thread.
* [Req-84][]: Added append option in copy method (``FileTransfer`` class).
	
### Internal Changes
* The robot repository has been updated. It is no longer extracting .jar files.
	* It also remembers last timestamp on file so detection of the changes is really fast. Only data files are extracted now.
	* Data files for non-packed robots were moved to .robotcache as well.
* VersionManager is now able to detect if the Robocode version was upgraded since last run.
* Blocking security issue with relative path in ``getDataFile()`` method.
* Improved security in RobotClassLoader.
* Reimplemented RobotPackager.
* ``BulletSnapshot`` has now method ``getBulletId()`` which gives identity of bullet for UI.

## Version 1.7.0.2 (16-Feb-2009)

### Bug Fix
* The fix for [Bug-164][] (Compiler Classpath Suggestion) backfired, as the entire text field in the window with the compiler properties would now be quoted.
	* Hence, the compiler would not be able to compile, and it was not possible to change the compiler preferences from the GUI.

## Version 1.7.0.1 (14-Feb-2009)

### Bug Fixes
* [Bug-163][]: Spaces or native names in name of robocode directory.
	* Some users experienced problems with starting Robocode. Hence, all startup batch and shell files for Windows, Linux, Mac OS X etc. have been corrected.
* [Bug-164][]: Compiler Classpath Suggestion.
	* When running Robocode in a folder with spaces, the compiler classpath was not quoted. Hence, the compiler would not work.

## Version 1.7 (11-Feb-2009)

### Bug Fixes
* [Bug-160][]: Battle Results screen displaying old results.
	* The results where not updated on the Battle Results windows between battles.
* [Bug-161][]: Robot disabled by any other than losing energy can recover.
	* Robots disabled due to bad behaviour could regain energy by hitting another robot by a bullet after it had been disabled.
		* Now this is only possible when the robot has disabled itself by using all its energy, and will be able recover energy by hitting an enemy, which is allowed.
* [Bug-162][]: Team battle.
	* Robots like ``abc.Shadow 3.83``, ``davidalves.Phoenix 1.02``, and ``kawigi.micro.Shiz 1.1``, ``kawigi.micro.ShizPair 1.1`` caused a ``NullPointerException`` and would not show up on the battle window.

## Version 1.7 Beta 2 (26-Jan-2009)

### Bug Fixes
* [Bug-158][]: Ubuntu throws ``NullPointerException`` in main.
	* This bug occurred when trying to startup Robocode.
* [Bug-159][]: Installation fail on windows if directory contain space.

### Changes
* Improved the version checking for new Robocode versions available for download.

## Version 1.7 Beta (19-Jan-2009)
* This version represents a completely new infrastructure of Robocode making it easier to extend and maintain for the future. It does not contain New Features compared to the previous version of Robocode, but focus on working the same way as the previous versions despite of all the restructurings.

### Bug Fixes
* [Bug-156][]: Spammy output on robot console windows.

### Internal Changes
* Robocode was modularized using the dependency injection framework PicoContainer.
	* This work includes introduction of many interfaces to existing components.
* Introduced Maven2 as build setup where the whole directory layout is based on the standard Maven structure.
* Rewritten the ``RobotClassLoader`` so it is able to load .jar file in the future.
* Rewritten security layer to use ``AccessController`` and ``CodeSource`` for detection of trusted code.
* Redesigned ``RobocodeProperties`` to ``SettingsManager`` and introduced ``ISettingsListener``.
* Implemented ``RbSerializer`` to be able to implement IPC (Inter-Process Communication) in future.
	* This should make it possible to extend Robocode for more platforms like e.g. Microsoft .NET.
* Redesigned ``RepositoryManager`` and nearby to hide implementation details.
* Moved all implementation of Robocode to the package ``net.sf.robocode``.
	* The old package ``robocode`` is now "used only" for API to outer world in order to keep backwards compability.

## Version 1.6.2 (04-Jan-2009)

### Bug Fixes
* [Bug-154][]: Robot name missing when replaying XML record.
* [Bug-153][]: Junior Robot ``turnAheadRight`` bug.
	* When a JuniorRobot was calling ``turnAheadRight()``, ``turnAheadLeft()``, ``turnBackRight()``, or ``turnBackLeft()``, the following exception occurred causing the robot to be terminated:

			java.lang.ClassCastException: robocode.peer.proxies.StandardRobotProxy
* [Bug-151][]: Exception when changing between ``Robot`` to ``AdvancedRobot``.
	* When a robot was changed from a ``Robot`` into an ``AdvancedRobot`` and recompiled, the game would cast a ``ClassCastException`` if a battle was started or restarted including that particular robot.
	* However, this bug did not occur with new battles where the list of robots was refreshed (by pressing Ctrl+R).

## Version 1.6.2 Beta 4 (22-Dec-2008)

### Bug Fixes
* Fixed open battle dialog, which was not loading.
* Fixed recorder NPE.
* Fixed serialization problems of scan arc (``Arc2D``).
* Fixed problem with reloading robot repository on each next battle dialog.

## Version 1.6.2 Beta 3 (18-Dec-2008)

### Changes
* Published new battle events and battle snapshots in the Control API, i.e. in the ``robocode.control`` package.
* Added missing Javadocs (HTML documentation) to public Robocode API classes.

### Bug Fixes
* The current scoring (not the total scoring) was calculated wrong from version 1.6.1.
	* The current Ramming Kill Bonus was set to current bullet kill bonus, which gave wrong ranking in the Ranking Panel.
* [Bug-148][]: Wrong bullet power.
	* Wrong bullet power could be reported back from ``Bullet.getPower()``, which could be ``> Rules.MAX_BULLET_POWER`` or ``< Rules.MIN_BULLET_POWER``.
* [Bug-147][]: gunHeat is negative.
	* gunHeat could be negative, which should never occur.
* [Bug-149][]: Replay exception.
	* An ``ArrayIndexOutOfBoundsException`` occurred in some situations.
* Fixed problem with ``RobocodeEngine.setVisible(true)``, where the ``RobocodeEngine`` would hang forever.

## Version 1.6.2 Beta 2 (07-Dec-2008)

### Bug Fixes
* Fixed problem with Bullet identity.
* Battle cleanup concurrency issue.
* Fixed problem with robots without package.
* AWT AppContext cleanup.
* Fixed versions comparison problem.

### Changes
* Mostly cleanup of code and documentation.
* Improved compatibility with RobocodeJGAP.
* Development robot version names were visible in the robot repository.

## Version 1.6.2 Beta (25-Nov-2008)

### Bug Fixes
* [Req-78][]: Visual debugging without cpu penalty.
	* Now Robocode gives a robot unlimited time when painting is enabled on UI (in the robot dialog) for that robot.
* Robocode will not load non-valid robots from repository anymore.
* Fixed placement of the robot dialogs (aka. robot console windows).
* Exception on robot's ``Condition`` doesn't break further processing now.
* [Bug-146][]: Spammy output when running roborumble.
	* Output from RoboRumble was spammy when outputting log after the first battle.
* ``java.lang.IllegalArgumentException: Line unsupported: interface Clip supporting format`` could occur when starting Robocode.
* [Bug-81][]: Replay recording does not record paintings.

### New Features
* [Req-24][]: Recording of battles. (fully implemented)
	* Battle recording and replay: Saving to binary and xml file.
* [Req-63][]: Replay should store debug graphics.
	* Robot painting and debug properties are now being recorded, but robot painting is not exported to xml.
	* Recording is slowing down the game and eats memory and disk space. Hence, you need to enable it in options, as it is disabled per default.
* New command line option ``-replay`` for replaying a battle record.
* New dialog for battle console and battle turn snapshots.
* Robot dialog: New tab-page with robot properties.
* The robot API has been extended with a new ``setDebugProperty()`` method.
	* See the ``sample.PaintingRobot`` robot for example of usage.
* [Req-31][]: Redirecting Robot output, running without GUI.
	* The ``robocode.control`` package has been improved so that the ``RobocodeEngine`` is able to return much more detailed information about was is going on inside the battle for each turn, round, and battle thru a new ``IBattleListener`` interface.
	* With the new ``RobocodeEngine`` it is possible to a detailed snapshot of the game state, e.g. the states of all robots and bullets, and also get the messages sent to the robot console etc.
* [Req-77][]: Better "lifebar" display.
	* Robot buttons on the Battle View is now showing the amount of energy and score as two coloured bars within the robot button.

### Changes
* Redesigned ``RobotPeer`` and ``Battle``.
	* Now we send messages between threads instead of synchronizing individual properties.
	* Code here is much more readable now.
* Most synchronization is now interlocked.
* Event manager, priority and dispatch of events refactored.
* The methods ``setPriority()`` and ``setTime()`` on the ``Event`` classes are no longer part of the Robot API.
	* These methods were used internally by the game.
	* Events are final classes now.
* More unit tests were added.
* ``Graphics2DProxy`` optimized.
* Interactive robots are better detected to not waste time of non-interactive robots.
* Team messages are deserialized on receiver robot thread.
* Got rid of robot loader thread.
	* Now Robocode is loading on robot's thread, where we got more security.
* Introduced new interfaces for code components.
* Robot threads have ``NORM_PRIORITY - 1``, AWT UI have ``NORM_PRIORITY + 2``.
* [Req-75][]: Add client version to POST data sent to server.
	* We now upload the [RoboRumble][] client version when uploading results to the RoboRumble server.
* Robots which are "wrong behaving" are removed from the robot repository and are not given another chance to run.
	* This goes for skipped turns, for unstoppable robots and for robots with class loading troubles.
* Changed the formatting of this versions.txt file in order to improve readability.

## Version 1.6.1.4 (14-Nov-2008)

### Bug Fixes
* Loosing robots were not receiving ``onBattleEnded(BattleEndedEvent)`` events.
* A new security issue fix for robots that were able to execute code thru the Event Dispatch Thread (EDT).
	* **Robots that try to access the EDT will be disabled!**
* [Bug-144][]: drawArc does not work as expected.
	* Both ``drawArc()`` and ``fillArc()`` are now using the Robocode coordinate and angle system.
* [Bug-143][]: Blank console window when compiling.
	* Some systems still had this issue, so a new fix has been applied.

### New feature
* [Req-65][]: Score % display.
	* The results and current rankings (during a battle) is now showing the score as percentage(s) in parenthesis right beside the score points like e.g. '7875 (12%)' for the total score in the results and '21 / 2900  (7 / 14%)' with the current rankings.
	* Thanks goes to Endre Palatinus, Eniko Nagy, Attila Csizofszki and Laszlo Vigh for this contribution!

### Changes
* The command-line option ``EXPERIMENTAL`` (= ``true`` or ``false``) allowing access to the robot interface peer is now working for the ``RobocodeEngine`` class also.

## Version 1.6.1.3 (24-Oct-2008)

### Bug Fixes
* [Bug-143][]: Blank console window when compiling.
   * This bug was introduced in 1.6.1.2.
   * When trying to compile a robot that would give a compiler error, the output console window for the compiler could be blank on Windows system and/or hang.
   * Now the compiler error is output correctly as in previous versions.
* [Bug-137][]: Roborumble ``ITERATE`` broken.
	* When running RoboRumble with ``ITERATE=YES``, ``DOWNLOAD=YES``, and ``RUNONLY=SERVER``, the ratings were only read once, not per iteration.
	* This bug fix removes a very old bug and the need of using a batch file as workaround in order to do the loop with updated ratings.
* [Bug-141][]: The ``-DROBOTPATH=<path>`` option does not work.
* [Bug-142][]: Broken .sh files.
	* An misplaced colon character was included in the teamrumble.sh file.
* Fixed issue with first time access to a robot's data directory after startup, where the robot was not allowed to write to it's own file.

### Changes
* [Bug-130][]: Various usability issues.
	* The list of available robots in the 'New Battle' dialog is now automatically refreshed before it is being shown, when a new robot has been compiled or a robot has been removed.
	* The 'Save' and 'Save As' in the File menu of the Robot Editor is now enabled and disabled depending if there is anything to save or not.

## Version 1.6.1.2 (12-Sep-2008)

### Bug Fixes
* RoboRumble: Sometimes results were given to the wrong robots, which gave a problem with the robot rankings in the RoboRumble, TeamRumble and MeleeRumble.
	* Thanks goes to Joachim Hofer ("Qohnil") for fixing this issue! :-)
* RoboRumble: Robots that read their data file got the following error message:

		Preventing unknown thread <robot name> from access: (java.io.FilePermission...
* ``ArrayOutOfBoundsException`` could occur when accessing the ``Graphics2D`` object returned by the ``getGraphics()`` method on the ``Robot```classes.
* The ``draw(Shape)`` method on the ``Graphics2D`` object returned by the ``getGraphics()`` method could not draw lines.
* The ``onMousePressed()`` event was called twice instead of only one time per mouse press.

## Version 1.6.1.1 (28-Aug-2008)

### Bug Fixes
* Issues with the scoring.
	* Sometimes the robots were ranked incorrectly compared to their total scores given in the battle results.
* When disabling the security (``-DNOSECURITY=true``) it would not be possible to run any battles as the following error would occur:

		RobocodeFileOutputStream.threadManager cannot be null!
* [Bug-135][]: ``-battle`` broken.
	* When using a battle file, the battles were not displayed one the GUI.
* [Bug-134][]: Robot problem after Options->Clean robot cache.
	* Robots that tried to access their data file, like e.g. ``sample.SittingDuck`` got a ``AccessControlException``.
* [Bug-131][]: Sometimes the compiler window hangs.
	* This bug only occurred when the compiler gave compilation errors.
* ``IllegalArgumentException`` occurred when calling ``setStroke()`` or ``setComposite()`` on the ``Graphics2D`` object returned by the new ``getGraphics()`` method on the Robot.

### Changes
* The intro battle will only be shown if a battle file has not been specified using the ``-battle`` command-line argument and Robocode is being run for the first time since installation.
	* Previously, the intro battle was always shown even though a battle file had been specified.

## Version 1.6.1 (17-Aug-2008)

### Bug Fixes
* [Bug-125][]: TimeoutExceptions occur when debugging in Eclipse.

### Changes

#### New Methods
* Added ``getGraphics()`` to ``Robot`` and ``IBasicRobotPeer``.
	* A robot is now able to paint itself at any time, and not only using the ``onPaint()`` event handler.
* Added ``getStatusEvents()`` to ``AdvancedRobot`` and ``IAdvancedRobotPeer``.
	* A robot is now able to handle status events in the middle of an event handler.

#### New Event
* [Req-66][]: "onBattleIEnd(*)" Event.
	* The the ``onBattleEnded()`` event handler is provided through the new ``IBasicEvents2`` class.
	* The new event ``BattleEndedEvent`` has been added.
		* When this event occur the new event handler ``onBattleEnded()`` will be called on the robot, i.e. when the battle is ended.
	* When reading the ``BattleEndedEvent`` it is possible to read out the results of the battle of the individual robot or team.
		* In addition, it is possible to check if the battle was aborted by the game or user.
		* The battle results will only be available if the battle is not aborted (where the results does not count).

#### Paint Events
* Paint events are now put in the robot event queue, meaning that the robots will pay CPU time when their ``onPaint()`` event handler is called.
* Added the ``PaintEvent``, which makes it possible to set the event priority of paint events using the ``AdvancedRobot.setEventPriority()``.

#### Mouse and Keyboard Events
* Mouse and keyboard events have added as new pulic classes and put into the robot event queue.
	* The robots will pay CPU time when their ``onMouseXX()`` and ``onKeyXX()`` event handlers are called.

#### Package Name
* [Req-70][]: Longer package name allowed.
	* The max. length of a robot's full package name has been extended from 16 to 32 characters.
	* The Robocode Repository is able to handle this (verified with Dan Lynn).

#### New TPS Slider
* The TPS slider has been redesigned to be more exponential, so it covers battle in slow speed (1-30 TPS), higher speed (30 - 120 TPS), and fast speed (120 - 1000 TPS).
* If you set the slider to max. TPS the game will run as fast as possible.
	* This feature already existed in earlier versions.
* If you set the slider to minimum (0 TPS) the game will pause.
	* This is a new feature.

#### The FPS (Frames Per Second)
* The max. FPS is now fixed to be max. 50 FPS allowing the TPS (turns per second) to be even faster.

#### New Title Bar
* The title on the Robocode window is now showing the current turn in a battle, and is updated every half second.
* The layout of the information shown in the title has been improved a bit.

#### Dialogs
* The About box, New Battle Dialog, Preferences Dialog, Ranking Dialog, and the Compiler Preferences Dialog are now modal.
* It is now possible to close the New Battle and Results Dialog by pressing the Esc key.

#### Ranking Panel
* The menu item for the Ranking Panel is now hidden when replaying a battle.
	* The Ranking Panel is now being hidden if it is visible, when a battle is being replayed.
	* The reason for hiding the Ranking Panel is that the replay feature does not support displaying the current rankings during the replay, i.e. the current scores are not recorded.

#### Command-line Options
* Added the new ``-DPARALLEL`` option (set to ``true`` or ``false``), which allows robots to run in parallel intended to take advantage of more CPU's available in the system.
	* This option may speedup battles with multiple CPU time consuming robots. However, the time measuring is not per robot.
* Added the new ``-DRANDOMSEED`` option (set to a random seed value), which enables the new repeatable and deterministic random generator.
	* The benefit of using this option to make it easier to test and debug your robots, as your robots will appear in the exactly same positions, when you rerun a battle.
* The ``-DPARALLEL`` and ``-DRANDOMSEED`` option has no effect when running RoboRumble, MeleeRumble, and TeamRumble.

#### New Random Generator
* Added a new deterministic and repeatable random generator is used by the game and overrides the random generator used throughout the whole virtual Java machine.
	* Methods like e.g. ``Math.random()`` will be using the exactly the same deterministic random generator enforced by Robocode.
* The new random generator will only be deterministic, if the ``-DRANDOMSEED`` option is enabled, i.e. when ``-DRANDOMSEED=12345678`` (or another value). Otherwise the the battles will run truly randomized.

#### System Logging
* The system log output has now been split, so logged errors are sent to ``System.err`` and logged messages are sent to ``System.out``.
	* This makes it possible to filter out messages from errors when reading out the logs from Robocode.
* The system log also includes a full stack trace when errors are logged, making it easier to determine where an error has occurred.

#### Various Usability Issues
* [Bug-130][]: Various usability issues.
	* Added "Enable all" and "Disable all" button in the View Options of the Preferences.
	* When the TPS slider is set to 0, the game is paused, and the Pause/Resume button set to paused mode.
		* When the TPS is 0 and the Pause/Resume button is pressed, the game will resume at 1 TPS, and the TPS slider will now move to 1.
	* In the New Battle window, the focus is now kept in the list of available robots when one of the two 'Add' buttons has been pressed.
		* Previously the focus was lost, and you had to reselect robots in the list of available robots in order to add more robots.
	* Multiple robots can now be added to a battle by using the arrow keys with e.g. the Alt+A.
	* Rearranged the order of menu items here and there, and also improved the name of some menus.

## Version 1.6.0.1 (03-Jun-2008)

### Bug Fixes
* [Req-74][]: Option for enabling/disabling robot timeouts.
	* It was not possible anymore to debug robots from Eclipse as ``TimeoutExceptions`` occurred when trying to resume from a breakpoint.
	* Now, Robocode must be started by adding the option ``-Ddebug=true`` to the VM arguments when debugging Robocode from Eclipse (or any other IDE).
	* The documentation (Wiki) about how to debug robots using Eclipse using the ``-Ddebug=true`` option will be updated.
* Fixed missing internal robot proxy layer introduced in Robocode 1.6.0.

## Version 1.6.0 (01-May-2008)

### Bug Fixes
* The CPU constant was not calculated the first time Robocode was started up.
* [Bug-123][]: Compiler fails to build due to CR/LF in scripts.
	* Removed ^M characters from the buildJikes.sh file so that the Jikes compiler can be built under *nix based system like Linux and Mac OS X.
* Fixed a security issue where robots could access the internals of the Robocode game thru the AWT Event Queue.
	* Robots that try to access the AWT Event Queue will now be disabled due to "bad behaviour".

### New Robot Interfaces
* A new package named ``robocode.robotinterfaces`` has been introduced, which contains pure Java robot interfaces for basic, advanced, interactive, junior and team robots.
	* See the Robocode Javadocs (HTML) documentation for more details about these new interfaces [here](https://robocode.sourceforge.io/docs/robocode/robocode/robotinterfaces/package-summary.html).
* The main purpose of the new robot interfaces is to make it possible for robot developers to create new robot types with their own API, but also to create robots using other programming languages, which requires used of interfaces instead of classes.
* The robot interfaces obeys the rules within Robocode, so it is not possible to create new game rules. However, it is possible to create new robot types with other methods names etc. (new API) based on the new robot interfaces. Also note that is is possible to create your own robots based directly on the robot interfaces.
* See the new '/robots/sampleex' directory for some examples of how to use these new interfaces in Java.
* The introduction of the new robot interfaces required a great deal of changes and cleanup of the internal structures of Robocode, but for the better (future).
* A new command line option has been made for Robocode named ``EXPERIMENTAL``, which must be set to ``true`` in order to allow the robots to access the internal robot peer with the robot interfaces for performing robot operations.  
	* When this flag is not set, you'll get a SecurityException in your robot if it is inherited from a robot interface.
	* This option must be set in the robocode.bat or robocode.sh like ``-DEXPERIMENTAL=true``.
	* **Note** that this experimental option might be removed in the future so that robots are always allowed to access the robot peer from the new robot interfaces.
* Most work with the robot interfaces was performed by Pavel Savara that has joined the development of Robocode, who have done a tremendous job with the new robot interfaces.

### Other Changes
* Added a "Make all bullets white" option to the Rendering Options.
* When this option is enabled, all bullets on the battle field will be painted in white.
	* Use this options when you need to see all bullets on the battle field, i.e. when bullet colors are almost invisible. That is, some robots might try make the bullets "invisible" to cheat the observer.
	* Thanks goes to [Robocode Ireland](http://robocode.ie/) for holding a turnament showing that this option was crusial when battles are shown on a big screen for a large audience.
* Lots of the Javadoc (HTML) documentation for the Robocode APIs were updated.

## Version 1.5.4 (15-Feb-2008)

### Bug Fixes
* [Req-62][]: Fix for massive turn skipping when cpu constant < granularity.
	* The CPU constant was way too little compared to version 1.4.9.
	* This is a critical bug when Robocode is used for competitions like the [RoboRumble][].
	* Now the CPU calculation has been improved by using a heavy math benchmark that have been adopted from Robert D. Maupin ("Chase-san").
* The method for determining if a robot has exceeded it's CPU time limit has been improved to use nano second precision (using ``System.nanoTime()``), to get rid of an issue with millisecond granularity that is too coarse.
	* This method was created by Julian Kent ("Skilgannon").

## Version 1.5.3 (30-Jan-2008)

### Bug Fixes
* [Bug-122][]: Not all shortcut keys work on MacOS.
* Some of the mnemonics on the menus on the Help menu did not work correctly.
* ``NullPointerException`` occurred when clicking a robot button on the right side of the battle view, when no battle was running.

### Changes
* Keyboard shortcuts have been replaced to comply with OSes where the function keys (F1 - F12) are not available or have a specific purpose, and thus should not be overridden.
	* The F5 shortcut key for refreshing the list of available robots in the New Battle, Robot Packager, Robot Extractor, and Team Creator window has been changed to 'modifier key' + R, i.e. Ctrl+R on Windows and Linux, and Command+R for Mac OS.
	* The F6 shortcut key for 'Compile' has been changed to 'modifier key' + B, i.e. Ctrl+B on Windows and Linux, and Command+B for Mac OS.
	* The F3 shortcut key for 'Find Next' has been changed to 'modifier key' + G, i.e. Ctrl+G on Windows and Linux, and Command+G for Mac OS.
* When a robot or team is being packaged an UUID is added in the .properties and/or .team files in the newly generated robot or team archive file (robot/team .jar file).
	* The UUID is a unique identifier for the robot or team, which is generated every time a robot or team package is being created or overwritten.
* This feature has been made to support New Features provided in Robocode Repository, which is currently being updated.

## Version 1.5.2 (08-Jan-2008)

### Bug Fix
* On some systems Robocode would not start up when trying to run robocode.bat or robocode.sh.

## Version 1.5.1 (12-Dec-2007)

### Bug Fix
* Fixed security flaw with the Event Dispatch Thread, where robots could use the ``SwingUtilities.invokeLater()`` for running any code they should like to.
	* Thanks goes to John Cleland who reported this security flaw and provided some very good examples of robots that could do some nasty cheats/hacks.

## Version 1.5 (05-Dec-2007)

### Bug Fixes
* [Bug-115][]: AWTException with RoboRumble on OS X.
* [Bug-97][]: Exception when packaging robots.

### Changes
* [Req-58][]: HitRobotEvent - damage.
	* Redundant ``HitRobotEvents`` are no longer occurring when Robocode is performing collision detection between two robots.
		* Previously, if a collision occurred between a stationary robot (i.e. not moving) and another robot that was moving, then two ``HitRobotEvents`` would first be sent to each robot based on the stationary robot - even though no damage was done. Next, two ``HitRobotEvents`` would be sent to each robot based on the robot that was moving, which **was** causing damage.
		* Now ``HitRobotEvents`` will only occur when damage is done to each robot. No ``HitRobotEvents`` will be ever be sent when no damage is done. That is, when a stationary robot is "colliding" with another robot.
* [Req-57][]: Events processed in chronological order.
	* The events in the robot event queue are now sorted in chronological order so that events that occurred before newer events gets processed first.
		* Previously, the event queue was ordered based on the event priorities so that the events with the highest priorities were processed first.
			* This could cause some problems with robots with skipped turns, as their event queue would potentially contain events from different time frames.
		* Now it is perfectly safe for robots to assume that events occurring before other event are processed first.
		* Events occurring in the same time frame is still sorted based on their priorities so that the events with higher priorities are processed before events with lower priorities.
* [Req-53][]: More control over the event queue.
	* The priority of the ``DeathEvent`` was changed from the reserved priority 100 to -1 in order to allow robots to process all pending events before they die.
		* Previously, robots were not able to process all events when it died as the ``DeathEvent`` was having the highest possible priority.
		* Now, when the ``DeathEvent`` has the lowest priority, meaning that this event will be the last event left on the robot's event queue before it dies. That is, all events in the event queue have already been processed when reaching the ``DeathEvent``.
* [Req-60][]: Enhanced CPU constant calculation.
	* The CPU constant is now measured in nanoseconds rather than milliseconds.
	* Using New Features introduced of Java 5.0 that provides more precise timing and also offer better granularity of timings.
* [Req-64][]: Change default battle settings like e.g. "Number of Rounds".
	* The "Number of Rounds" value on the New Battle Dialog is now saved and restored when the game is restarted, i.e. Robocode remember Number of Rounds you used the last time.
* Improved the output of the command line usage of Robocode when called from the command line with the ``-?`` or ``-help`` option.

### New Features
* [Req-54][]: All input received from events.
	* The Robot class has got a new ``onStatus(StatusEvent e)`` method.
		* This event handler is automatically called for each turn of a battle, which contain a complete snapshot of the current robot state at that specific time/turn.
		* This new method makes it possible to map a specific robot class field value to a specific time.
* [Req-56][]: Robots Cache Cleaner.
	* Added the Robot Cache Cleaner tool created by Aaron Rotenberg ("AaronR").
	* This tool is used for cleaning the cache files of the robots, which is very useful with the [RoboRumble][] client, where most problems with the robot repository can be solved by cleaning the robot cache files.
	* This tool is activation by selecting "Clean Robot Cache" in the Options menu or by running the tool from a command line (standing in the robocode home directory):

			java -cp ./libs/robocode.jar;./libs/cachecleaner.jar ar.robocode.cachecleaner.CacheCleaner

## Version 1.4.9 (07-Nov-2007)

### Bug Fixes
* [Bug-120][]: The renderering is slower on Vista than XP?
* [Bug-116][]: Exclude filter removes bots from the RoboRumble.
	* [RoboRumble][] participants excluded with the ``EXCLUDE`` filter were removed from the ratings, which is not the intension.
	* In addition, if trailing white-spaces occurred with the comma-separated list for the ``EXCLUDE`` filter, the filter did not filter out participants correctly.
	* With the release of 1.4.8 this bug was claimed to be fixed, but unfortunately the bug fix was missing in the build of the 1.4.8 release.
* Corrected bug seen with the ``JuniorRobot``, when first calling ``turnAheadLeft(100,90)`` and then ``turnRight(90)`` right after this call, where the robot turn quickly to the left, but slowly to the right.
* The calculation of the possible frame rate (FPS) was calculated incorrectly.
	* This caused Robocode to run with lower FPS (when rendering battles on the GUI) compared to what is possible with the available hardware.
	* With this bug fix, Robocode will render the battles even faster than before in most cases.

### Changes
* When a new CPU constant is being calculated it will now take the system time granularity (OS dependent) into account.
	* Previously, if the CPU constant was less than the time millis granularity, then skipped turns could occur on robots when the CPU constant < system time millis granularity.

## Version 1.4.8 (25-Oct-2007)

### Bug Fixes
* [Bug-114][]: Wait Interrupted Message.
	* With Robocode 1.4.7 a minor bug was introduced so that the robot console printed out "Wait interrupted" when a round was completed.
* [Bug-117][]: Sound card is being dodgey/not detected by OS causes error.
* [Bug-118][]: Battles fail when executing with Eclipse debugger.
	* When debugging robots or the Robocode game itself within Eclipse on Windows, the Java VM was crashing with an "Access Violation".

## Version 1.4.7 (09-Oct-2007)

### Bug Fixes
* [Bug-112][]: Forcing stop: no score will be generated.
	* Some robots did not receive any score even though they won the battle.
	* The cause of this bug was due to Robocode that did not always detect if the robot's thread(s) had been properly terminated.
	* Thanks goes to Eric Simonton, David Alves, and Aaron Rotenberg ("AaronR") for help solving this issue!
* Teams located in the '/robots/.robotcache' directory were still put into the robot.database file.
	* Thus, teams located in the .robotcache directory were incorrectly shown in the New Battle dialog.
* [Bug-110][]: Partial match recording replay.
	* "Ghost" rounds: When stopping a battle while recording was enabled and then replaying the recorded battle, Robocode would show the last rounds of the battle even though no recording occurred for these rounds.
* [Bug-113][]: ``-battle`` option runs at full speed.
	* When using the ``-battle`` option Robocode would run at full speed, i.e. the TPS set to maximum, even though the GUI was enabled with a predefined TPS.
	* Now, the TPS is only set to maximum when the ``-nodisplay`` option is being used.
* [Bug-111][]: Wasted time at end of each round.
	* Robocode was wasting time on trying to wake up robots that were dead.
	* Robocode was blocked for the amount of milliseconds defined by the CPU constant when a robot was killed in a battle, as Robocode was waiting for the dead robot to wake up for exactly this amount of time.
* [Bug-85][]: No window position in-bounding.
	* When starting Robocode, saved window locations (x and y coordinate) of a window might not fit into any of the available screens/displays (e.g. virtual desktop).
	* Robocode will now center windows into the current screen displayed.

### Robocode Changes
* The Stop button is now only enabled when a battle is running and will will be disabled after the battle has ended.

### RoboRumble Changes
* The configurations files roborumble.txt, meleerumble.txt, and teamrumble.txt have been improved:
	* All properties are now documented and have been grouped more logically.
	* The ``BATTLESPERBOT`` property has been raised to 2000 for the RoboRumble and MeleeRumble.
* [Req-55][]: Exclude Filter for RoboRumble.
	* An exclude filter has now been added, which makes it possible to exclude participants that causes trouble somehow.
		* The exclude filter is controlled using the new ``EXCLUDE`` property, which takes a comma-separate list of participants where the wildcards * and ? can be used
		* Excluded participants are not added to the participants file, and will not be downloaded or take part in battles.

## Version 1.4.6 (25-Sep-2007)

### Bug Fixes
* [Bug-107][]: Bullet hit locations reported inaccurately.
	* The coordinates of a ``Bullet`` from a bullet event like ``HitByBulletEvent()`` was not correct as the coordinates of the bullets would follow the bullet explosion on the robot it has hit.
	* Now the coordinates of the ``Bullet`` will not change when it hits a robot or another bullet, even though the coordinates of the bullet explosion will change internally (for painting the explosion).
		* This means that the coordinates of a bullet received from a bullet event will actually be on the real bullet line.
	* The initial explosion painting on a robot has also changed so it shows exactly where the bullet has hit the robot.
* When Robocode cleaned up the robot database a ``NullPointerException`` could occur if the robot database was pointing to a missing file.
* [Bug-108][]: It is possible to restart a battle without any robots.
	 * The Restart button was enabled when no battles had previously been started.
* [Bug-106][]: Incorrect repaint of paused battlefield.
	* The areas of the battle field was repainted with the black background together with the Robocode logo when the game was paused and the battle window was repainted. Hence, it was not possible to see the current state of the game on the battle field.
* [Bug-98][]: When minimized doesn't show actual tps.
	* When the Robocode window was minimized the actual TPS and FPS were not shown.
* When installing new versions of Robocode on top of an existing Robocode installation, the About window did not have the right height.

### Changes
* The color of each bullet is now independent on the current color set with the ``setBulletColor()`` method.
	* Previously, all bullets were instantly changing their colors when ``setBulletColor()`` was called.
	* Now, the color of the bullet will stick to the bullet color set when the bullet was fired.
* Improved the "Check for new version", so that is able to differ between release type as alpha, beta, and final release types.

## Version 1.4.5 (17-Sep-2007) The "Fair Play" release

### Bug Fixes
* [Bug-104][]: Reproducable scoring bug.
	* Unfair play: Two robots with the same code (but different names) would get different scores instead of a 50-50 split.
	* Robots listed before other robots in a battle would gain a minor benefit compared to the other robots. This was in particular the case if they killed each other at the same time. Then the robot listed first would get a "half turn" advantage over the other robot.
	* Now, the ordering does not matter anymore, as when ever the robots are checked one at a time in sequence, then they will be checked in random order.
* [Bug-103][]: ConcurrentModificationException.
	* ``ConcurrentModificationException`` could still occur when called one of the ``getXXEvent`` methods with an ``AdvancedRobot``.
	* Now all ``getXXEvent`` methods like e.g. ``getAllEvents()`` are all synchronized directly with the internal event queue of the robot before reading out the events.
* [Bug-105][]: ``testingCondition`` flag not reset.
	* Test Condition flag of a robot was not reset between rounds.
	* If the robot thread was disabled while testing a condition for a custom event all following rounds will trigger an exception:

			robocode.exception.RobotException: You cannot take action inside Condition.test(). You should handleonCustomEvent instead.

* [Bug-101][]: "Keyboard lockup" with interactive robots.
	* The sample robot named ``sample.Interactive`` has been changed for Robocode 1.4.5 so it continues moving forward or back when the UP or DOWN arrow key is being pressed down. Previously, the robot would only move 50 pixel when pressing down the UP or down arrow key, which was not intuitive compared to the behaviour with traditional first person shooter games. Thus, this looked like a bug.
* [Bug-87][]: Round indicator incorrect.
	* Again, the title of Robocode was incorrectly showing round N+1 of N when a battle was ended.
* Memory leaks occurring during a round due to missing cleanup of bullets have been removed.
	* Note that _ALL_ bullets were actually cleaned up, when ending the battle (containing one or several rounds).
	* One good side-effect of this bug fix is that the game is speeded up, especially when running in minimized mode, as the game does not have to perform unnecessary calculations on bullets that is not visible on the battle field anymore.

### Changes
* The sample robot named ``sample.Interactive`` has been changed so it continues moving forward or back when the UP or DOWN arrow key is being pressed down.
	* Previously, the robot would only move 50 pixel when pressing down the UP or down arrow key, which was not intuitive compared to the behaviour with traditional first person shooter games. Thus, this looked like a bug.

## Version 1.4.4 (09-Sep-2007)

### Bug Fixes
* With version 1.4.3 a bug was introduced so that ``battleAborted()`` was called in the ``robocode.control.RobocodeListener`` when the battle was not aborted, i.e. when a battle completes successfully.
	* This bug caused Robocode clients as e.g. RoboRumble to hang!
* Removed Windows end-of-line characters from the .sh files for RoboRumble.

### Change
* Robocode now throws a ``NullPointerException`` if the condition parameter has been set to null when calling ``addCustomEvent()`` or ``removeCustomEvent()`` on an ``AdvancedRobot``.

## Version 1.4.3 (07-Sep-2007)

### Bug Fixes
* [Bug-95][]: OutOfMemory: Robots are Being Left on the Stack.
	* Major bug fixes was done by Nathaniel Troutman to get rid of large memory leaks, especially when creating and destroying ``robocode.control.RobocodeEngine`` instances many times.
	* Most of the memory leaks were caused by circular references between internal classes/objects in Robocode. Now, these such circular references are cleaned up.
* The configuration files for RoboRumble was completely missing under the /roborumble folder, i.e. the meleerumble.txt, roborumble.txt, and teamrumble.txt.
* [Bug-94][]: Inconsistent Behavoir of ``RobocodeEngine.setVisible()``.
	* This fix was done by Nathaniel Troutman.
	* When invoking the RobocodeEngine to directly run a battle(s) and calling ``RobocodeEngine.setVisible(true)``, and then later call ``RobocodeEngine.setVisible(false)`` the results dialog would still show up at the end of a battle.
* [Bug-96][]: Initializing Label even when no display.
	* Did another fix where a dummy AWT (GUI) component was created even though the GUI was disabled causing problems when trying to run e.g. RoboRumble remotely without the GUI enabled.
* [Req-52][]: The 'New Battle' window sometimes is spamed with .robotcache.
	* Sometimes the "New Batle" window would show robot classes that reside in the .robotcache folder under the /robots folder. This occurred when the robot database was (re)builded, e.g. if the robot.database file was missing.
* [Bug-99][]: Clicking on a bottom area results in ``ClassCastException``.
	* When running battles including the MyFirstJunior and the pressing the mouse button outside of the battle field a ``ClassCastException`` would occur.
* [Bug-100][]: Double-clicking "restart".
	* When double-clicking the Restart button for the battle window the UI could lock up completely trying to play all battles, and it would not be possible to stop the battle.

## Version 1.4.2 (26-Aug-2007)

### Bug Fixes
* [Bug-92][]: RoboRumble tries to connect with GUI.
	* RoboRumble was invoking AWT (GUI) stuff when running, which caused problems on systems without support graphical display or running [RoboRumble][] remotely behind a firewall.
* [Bug-102][]: Bots can hold memory after being destroyed.
   * When running ``robocode.control.RobocodeEngine`` it caused memory leaks each time a new instance of the ``RobocodeEngine`` was created, even though the object was completely destroyed.
* [Bug-93][]: ``onPaint(Graphics2D g)`` called prematurely.
	* The ``onPaint()`` method was invoked just before the robot got the chance of updating it's internal world model.
	* Now the battle view is updated as the first thing right after the robots have updated their internal model.
* [Bug-71][]: RobocodeEngine becomes slower the more battles that are run.
	* The Robocode engine was halted with spurious exceptions when an exception occurred inside an ``onPaint()`` method in a robot, i.e. when the robot itself causes an exception inside ``onPaint()``.
	* Now, whenever an exception occurs inside the ``onPaint()`` method of a robot, the exception is now being catched by Robocode and printed out in the robot console.
	* Due to this bug exception handlers have now been added to all ``onKeyXX`` and ``onMouseXX`` events, where the exceptions are now printed out to the robot console.
* A ``ConcurrentModificationException`` bug did still occur with the internal ``EventQueue`` of a robot.

## Version 1.4.1 (19-Aug-2007)

### Bug Fix
* [Bug-91][]: ``ConcurrentModificationException``.
	* A couple of ``ConcurrentModificationException`` bugs were introduced with version 1.4, which are now fixed.
	* Thank goes to Helge Rhodin ("Krabb") for help with solving the bug!

## Version 1.4 (14-Aug-2007) The "Junior Robot" release

### Bug Fixes
* [Bug-88][]: Scorch layer.
	* I changed the behaviour of the scorch marks + plus a lot more.
* Static fields on robots were not cleaned up anymore after each battle has ended.
* [Bug-90][]: Limit per round, not turn.
	* When printing to ``out`` in ``onScannedRobot()`` event before a ``scan()`` call, the the logging to out would stop with an system error that to much is printed out.

### Changes

#### Added JuniorRobot
* The ``JuniorRobot`` class is simpler to use than the ``Robot`` class and has a simplified model, in purpose of teaching programming skills to inexperienced in programming students.
	* This new robot type has been designed by Nutch Poovarawan / Cubic Creative team.
* Added ``sample.MyFirstJuniorRobot``.
	* This robot is very similar to ``MyFirstRobot``, but tracks it's scanned enemy better.

#### GUI: Changed Menu Shortcut Key
* Robocode forced the use of the Ctrl key to be used as menu shortcut key.
	* Now Robocode ask the Java VM what menu shortcut key to use.
* This change means that Mac OS X users should now use the Command key instead of the control key.
	* Thanks goes to Nutch Poovarawan for the tip of how to do this! :-)

#### Improved Battle View a bit
* A red border is now painted around the battlefield when the battleview's height and/or width is larger than the battlefield.
* Explosions are painted outside the battlefield, when the battleview is larger than the battlefield.
* The text for the Robot names and scores are now "clipped" to the width and height of the battleview instead of the battlefield.

#### Added "Recalculate CPU constant" to the Options Menu
* [Req-50][]: Recalculate CPU Constant.
	* This makes it possible to force recalculation of the CPU constant.

#### RoboRumble Changes
* Redundant RoboRumble config files are now removed from the /config folder.
* Changed ``UPLOAD=NOT`` to ``UPLOAD=YES`` as default, i.e. the results are now automatically uploaded to the RoboRumble server.

## Version 1.3.5 (04-Jul-2007) The "Fast renderings" release

### Bug Fixes
* [Bug-87][]: Round indicator incorrect.
	* The title was displaying "Playing round N+1 of N" when the battle has ended.
* [Bug-86][]: Using UI removes focus from interactive bots.
	* Key events are now received even though the battle view does not have the focus.

### Changes

#### Faster Rendering
* The battle rendering is now 30-50% faster due to image buffering (but uses more memory).
* A new "Buffer images" option under the Rendering Options can be enabled/disabled on the fly while playing a battle.
	* By default, "Buffer images" is enabled which makes the rendering faster, but which also uses additional memory.
* Due to the faster renderings, explosion debris is now enabled by default.

#### Controlling Robocode
* The container classes in the robocode.control package are now Serializable, which makes it easy to load and store, battle specifications, results etc., but also to send these over the network.

## Version 1.3.4 (27-Jun-2007) The "Interactive" release

### Bug Fixes
* [Bug-84][]: Preferences page problem on machine w/out sound card.
	* ``NullPointerException`` occurred when trying to open the Sound Options page from the Preferences when no sound card (or actually audio mixer) is present in the system.

### New Features
* The Robot class has now been extended with keyboard and mouse events/methods:
	* ``onKeyTyped(KeyEvent)``
	* ``onKeyPressed(KeyEvent)``
	* ``onKeyReleased(KeyEvent)``
	* ``onMouseMoved(MouseEvent)``
	* ``onMouseClicked(MouseEvent)``
	* ``onMousePressed(MouseEvent)``
	* ``onMouseReleased(MouseEvent)``
	* ``onMouseEntered(MouseEvent)``
	* ``onMouseExited(MouseEvent)``
	* ``onMouseDragged(MouseEvent)``
	* ``onMouseWheelMoved(MouseEvent)``

	These New Features adds a new dimension to the game, i.e. you could make robots that must be controlled entirely manual, semi-automized robots, or press various key for changing between various robot strategies, e.g. when debugging.

	Thus, it is now possible to create robots where multiple human players battle against each other, or compete against existing legacy robots.

### New Sample Robot
* A new sample robot named "Interactive" has been added to demonstrate how to control a robot using keyboard and mouse events only.
* This robot is controlled by the arrow keys and/or the mouse wheel, and let the gun point towards the current coordinate of the mouse pointer used for aiming. Mouse buttons are used for firing bullets with various fire power.

### Minor Changes
* The background Robocode logo has been changed into green and the Robocode slogan "Build the best, destroy the rest" was added.

## Version 1.3.3 (22-Jun-2007)

### Bug Fixes
* [Bug-82][]: Undo comment does not change font color of code.
	* Wrong colors when undoing and redoing multiline comment in the Robot Editor
* When a battle was stopped a new battle could start before the previous battle was closed down.
* When restarting a battle while it was paused caused strange behaviour with new battles, and the "Next Turn" button stopped working.
* [Bug-83][]: Ranking Panel Does not update number of competitors.
	* In some situations the Rankings Panel did not show the results for all robots.
	* This could be seen if first playing a battle with only 2 robots, and then start a new battle with more robots. In this case, only the rankings for the top 2 robots were shown.

### Changes
* The Rankings Panel and Results Dialog are now automatically packed to fit the table containing the rankings/results.

## Version 1.3.2 (09-Jun-2007)

### Bug Fixes
* The ``sample.SittingDuck`` would not start when no GUI is enabled.
* The Look and Feel is not set if the GUI is disabled.
* The robocode.sh ignored command line arguments (e.g. under Linux and Mac).
* [Bug-80][]: Results file is empty with the command line.
   * When setting the ``-result`` parameter from the command line the results file was empty.

### Changes
* When specifying the ``-battle`` parameter the .battle extension and battle directory can be omitted.
	* Hence you can write ``-battle sample`` instead of ``-battle battles/sample.battle``.
	* If a specified battle file does not exist Robocode will now exit with an error.
* If you specify the ``-results`` parameter the last results will now always be printed out, i.e. with and without the GUI enabled. Otherwise, if the GUI is not enabled (by setting the ``-nodisplay`` parameter) then the last results will be printed out to system out.

## Version 1.3.1 (30-May-2007)

### Bug Fixes
* When loading a battle, the robots specified in the battle file were not selected on the battle dialog.
* When the intro battle has finished the battle settings are now reset to the default battle settings.
	* This fixes the issue were the fixed robot starting positions are still used in new battles and where the "Number of Rounds" was set to 1 instead of 10.
* [Bug-79][]: Output displayed in bursts.
	* The output in the robot console windows were written out in bulks instead of immediately.
* Bugs fixed in RoboRumble which could cause a ``IllegalThreadStateException``.

### Changes
* Robocode will now print out an error message and just proceed if problems arise with trying to set the Look and Feel (LAF) to the system LAF.
* [Req-44][]: Restart tweak.
	* When stopping or restarting a battle, the battle will now stop immediately instead of continuing for a while showing robot explosions when the robots are being terminated due to the stop.
* [Req-45][]: Reset compiler cancel button.
	* Added confirm dialog when trying to reset the compiler preferences from the Compiler -> Options -> Reset Compiler in the Robocode Editor in order to prevent the compiler preferences to be reset by accident.

### New Features
* Added link to Java 5.0 Documentation in the Help menu.

## Version 1.3 (17-May-2007) Now featuring the RoboRumble client

### Bug Fixes
* [Bug-78][]: Robots are disabled with no timer or countdown.
* [Bug-74][]: Java FilePermission error during startup.
	* The Event Dispatch Thread was denied access by the Robocode Security Manager.
* [Bug-75][]: ``getTeammates()`` problem.
	* Changed back the ``TeamRobot.getTeammates()`` to return ``null`` if no teammates are available.
	* This rollback was done in order to keep compatibility with robots developed for older versions of Robocode.
* [Bug-72][]: The game won't play sounds on 2nd launch.
	* No sounds were played (when enabled) when Robocode was launched the second time.
* [Bug-73][]: Sound don't work.
* [Bug-76][]: Unique error.
	* Lots of synchronizations issues and potential ``ConcurrentModificationExceptions`` have been fixed.
* [Bug-70][]: Version 1.2.6A incompatible with Roboleague.
	* The bug causes this exception:

			Exception in thread "Thread-4" java.lang.NoSuchMethodError: robocode.control.RobotResults.getRamDamageBonus()
* [Bug-69][]: Robot causes Null Pointer Exception.
	* Fixed ``NullPointerException`` occurred when a robot is forced to stop.
* When using ``robocode.control.RobocodeEngine`` it was not possible to play team battles. Instead an ``ArrayOutOfBoundsException`` occurred.
* ``robocode.control.RobotResults.getRamDamage()`` incorrectly returned a double instead of an integer.
	 * This bug caused problems with running Robocode on RoboLeague.
* The "Enable replay recording" got set if it was not set after running Robocode without the robocode.properties file the first time.
* ``NullPointerException`` could occur when using ``robocode.control.RobocodeEngine`` and the GUI was not enabled.
* The text field for the filename in the robot packager was way too high.
* In RoboRumble, the codesize of some robots were incorrectly calculated to be 0 bytes, and hence these robots was not able to participate in [RoboRumble][] battles.
* This was due to the codesize tool, which could not analyze .jar files inside .jars.

### Changes

#### RoboRumble Client is now built-in
* [Req-39][]: Support for RR@Home
	* RoboRumble@Home client, originally developed by Albert Prez, is now built-in.
	* [RoboRumble][] is the ultimate collaborative effort to have a live, up-to-date ranking of bots.
	* It uses the power of available robocoder's computers to distribute the effort of running battles and building the rankings.
	* For more information about RoboRumble@Home you should read its [home page](http://robowiki.net/wiki/RoboRumble).
	* The version of the RoboRumble client included in Robocode is an updated version of the original one that can run with the current version of Robocode and which has been ported to Java 5.
	* Configuration files has been updated, and are available in the 'roborumble' folder.
	* Issues with downloading robots from the Robocode Repository site has been fixed.
	* Special thanks goes to Gert Heijenk ("GrubbmGait") who did a tremendous job with lots of alpha testing regarding the new RoboRumble@Home built into Robocode! :-D

#### Codesize
* [Req-38][]: Codesize.
	* The codesize tool by Christian D. Schnell has been added to support the built-in [RoboRumble][] plus a new feature for getting the codesize and robot codesize class (MiniBot, MegaBot etc.) when a robot is being packaged.
	* This tool has now been taken over by Flemming N. Larsen (agreed with Christian) and updated to version 1.1, which can handle files > 2KB, and also analyse .jar files inside .jar files.

#### Start Positions
* [Req-36][]: Initial Placement.
	* Added feature that allows specifying the initial start positions of the robots on the battlefield.
	* By specifying positions by setting ``robocode.battle.initialPositions`` in a .battle using this format ``(x1,y1,heading1),(x2,y2,heading2),(?,?,?)`` you can specify the initial location and heading for each robot specified with ``robocode.battle.selectedRobots``.
		* One example is:

				(50,50,90),(100,100,?),?
		
			* This means that:
				1. the 1st robot starts at (50,50) with a heading of 90 degrees,
				2. the 2nd robot starts at (100,100,?) with a random heading,
				3. the 3rd (and last) robot starts at a random position with a random heading.

	* See the battle/intro.battle for an example of how to use this option.

#### Robot and Control API
* Added a new method called ``getNameAndVersion()`` to the ``robocode.control.RobotSpecification``.
	* This method was added to better support [RoboRumble][] and similar ranking programs.
* Changed the ``TeamRobot.broadcastMessage()`` so it does not throw an ``IOException`` when the robot is not in a team.

#### Improved File Structure
* The file structure of Robocode has been slightly improved.
* All .jar files including robocode.jar are now located in the libs folder.
* The robot.database and .robotcache files has been moved to the robots folder.
* All RoboRumble related files are located in the roborumble folder.

## Version 1.2.6A (11-Mar-2007)

### Bug Fixes
* A ``NullPointerException`` occurred if the battle view was not initialized.
* This bug made it impossible to control Robocode via the ``robocode.control`` package when attempting to show the battle window.

### Changes
* [Req-37][]: Running Score Window.
	* The Ranking Panel and Battle Results are now windows instead of dialoges.
	* This means that the Ranking Panel and Battle Results will still be visible when the game is running in minimized mode.

## Version 1.2.6 (06-Mar-2007)

### Bug Fixes
* [Bug-66][]: Crash: starting a new round while et.Predator 1.8 is playing.
	* With some robots, a ``java.lang.NoClassDefFoundError`` occurred when Robocode tried to cleanup the static fields occupied by the robot when the battles are over.
* [Bug-67][]: Some issues with MessageEvent + priority.
	* These 4 issues were fixed:
		1. In the Robocode API -> AdvancedRobot -> setEventPriority there is information about all event priorities except MessageEvent priority.
		2. You can call ``getEventPriority("MessageEvent")``, but when you call ``setEventPriority("MessageEvent", someValue)`` you gets a ``"SYSTEM: Unknown event class: MessageEvent"`` message and the priority doesn't change. (using ``TeamRobot``).
		3. ``getEventPriority("MessageEvent")`` returns 80, so (if it is true) it "collides" with ``CustomEvent`` priority which is also 80 by default. This way you can't assume which event will be called first basing on its priority.
		4. Unlike it is done for other events, there is no ``getMessageEvents()`` function.
* [Bug-68][]: Preferences not saved.
	* The rendering options was not set correctly when loading these between battle sessions.
* When using the ``RobocodeEngine.setVisible(true)`` the Robocode window was shown with the wrong size and without the native Look & Feel.

### Changes

#### New TeamRobot Method
* Added missing ``getMessageEvents()`` to the ``TeamRobot``.

#### Default Event Priorities
* The changes were made as some events "shared" the same default priority, making it hard to tell which event would occur before the other.
* ``BulletHitBulletEvent`` priority was changed from 50 to 55.
	* Previously, both ``BulletHitEvents`` and ``BulletHitBulletEvents`` used priority = 50.
* ``MessageEvent`` priority was changed from 80 to 75.
	* Previously, both ``CustomEvents`` and ``MessageEvents`` used priority = 80.
* The Ranking Panel has been enhanced.

#### Ranking Panel
* Now the Ranking Panel contains the same columns as the Battle Results.
* Both the current scores and total scores are shown together where it makes sense.
* The column names of both the Ranking Panel and Battle Results have been improved.

#### New Pause/Debug Button
* [Req-35][]: Pause Button.
	* A Pause/Debug button has been added to the Robot Console window.
	* This is handy if you want to pause the game when only your robot's console window is open when the game is minimized.

#### Battle Window
* The Pause/Debug button on the Battle Window has been changed into a toggle button.
* The "Next Turn" button is now always visible, but not alvays enabled.

#### Documentation
* The documentation of the Robocode API (Javadoc) has been improved a lot.

#### Installer
* The Installer is now checking is the user is running Java 5.0 or newer.
* If the Java version is older than 5.0, then an error message will display telling the user to install at least JRE 5.0 or JDK 5.0, and the installation is terminated.

#### robocode.sh
* robocode.sh has been updated.
	* Armin Voetter has contributed with an improved version of robocode.sh so that the script resolves the path to Robocode by itself.

## Version 1.2.5A (19-Feb-2007)

### Bug Fix
* On some systems Robocode could not start up due to a ``NullPointerException`` in the internal sound manager/cache.

## Version 1.2.5 (18-Feb-2007)

### Bug Fixes
* [Bug-65][]: Cannot run robocode after installation.
* [Bug-63][]: BulletHitBullet only destroys one bullet.
	* When two bullets collided, one of the bullets was not destroyed, but continued.
* [Bug-64][]: Exception when referencing length of an array of ``String``.
	* ``TeamRobot.getTeammates()`` returned ``null`` instead of an empty array when no teammates are available.
* [Bug-62][]: Memory "Leak".
	* Memory leak could occur on robots using large objects on static fields.
	* Robocode now clean all static object fields that are not final after each battle, but not between rounds. That is, the static fields are now garbage collected.
* Some ``ConcurrentModificationException`` issues were removed.

#### Changes

### Sound Effects (SFX)
* The sound effects in Robocode can now be changed.
	* This is done by specifying the file for each sound effect using the ``file.sfx.xxx`` property keys in the robocode.properties file, e.g. the ``file.sfx.gunshot`` for setting the sound effect for gunshot.

   	* ``robocode.file.sfx.gunshot``: the sound of a gun shot.
	* ``robocode.file.sfx.robotCollision``: the sound of a robot colliding with another robot.
	* ``robocode.file.sfx.wallCollision``: the sound of a robot hitting the wall.
	* ``robocode.file.sfx.robotDeath``: the sound of a robot dying, i.e. exploding.
	* ``robocode.file.sfx.bulletHitsRobot``: the sound of a bullet hitting a robot.
	* ``robocode.file.sfx.bulletHitsBullet``: the sound of a bullet colliding with another bullet.

* The supported sound formats can be found [here](http://java.sun.com/j2se/1.5.0/docs/guide/sound/).

#### Music Support
* Robocode now supports music.
* By specifying the file for each music file using these properties in the robocode.properties file for setting:

	* ``file.music.theme``: the startup theme music.
	* ``file.music.background``: background music during battles.
	* ``file.music.endOfBattle``: the "end of battle" music when a battle is over.

* The supported music formats can be found [here](http://java.sun.com/j2se/1.5.0/docs/guide/sound/).

#### Misc.
* The column names in the Battle Results window have been improved.
* Keys in the robocode.properties file (the configuration file) are now automatically sorted when saved.
* Previously the keys were put in random order each time the property file was saved.

## Version 1.2.4 (25-Jan-2007)

### Bug Fixes
* [Bug-60][]: ``ConcurrentModificationException`` when extracting robots.
	* This exception sometimes occurred when robots were imported when Robocode was starting up.
* [Bug-59][]: Issue when setting the priority of a ``BulletHitBulletEvent``.
	* Added methods for setting and getting the priority of ``BulletHitBulletEvent`` that was missing completely in Robocode?!
* Removed ``IndexOutOfBoundsException`` when replaying battles.
* Explosion debrise was shown in the lower left corner (0,0) when starting battles and battle ground is set to visible.
* [Bug-61][]: Hang when checking for new version with no Internet access.
	* Robocode could hang when checking for a new version when no Internet connection was available.
	* Now a 5 second timeout has been added to prevent Robocode from hanging.

### Changes

#### Robots Die Faster
* [Req-33][]: Making robots die quicker (graphically).
	* Robocode stops painting the battlefield and playing sounds when a battle is ended after 35 turns.
	* However, the robots still have 120 turns until they are really killed like Robocode is used to, but the battle continues like if it was running in minimized mode (fast).

#### Options
* The common options for enabling replay recording has been changed to disabled per default.
	* When running lots of battles in a row with replay recording enabled Robocode runs out of memory, which causes problems when running tournaments.
* Added "View Explosion Debris" option in the View Options.
	* Explosion debris is diabled per default as this feature can slow down the game with 25% - 50% when viewing battles.

#### Javadocs
* [Req-34][]: Provide javadoc for robocode.util.Utils and robocode.control.
	* Javadocs have been provided for:
		* The ``robocode.util.Utils`` class providing angle normalizing methods.
		* The ``robocode.control`` package used for controlling Robocode externally.

#### Files
* Fixed incosistency with .jar files located in the robot folder.
* Robot packages (.jar files) is now only extracted from the root of the robots folder.
* In previous Robocode versions when starting up Robocode without a robot.database file and .robotcache directory, Robocode would extract Robot packages from the root of the robot folder and also the sub folders. When running Robocode the first time without these files, robots from the sub folders were shown (if available), but not the following times when Robocode was started up.
	* This fix was done by Robert D. Maupin ("Chase-san").

## Version 1.2.3B (14-Jan-2007)

### Bug Fixes
* [Bug-58][]: ``NullPointerException`` during replay.
	* Titus Chen made a bug fix for a ``NullPointerException`` that caused a replay to stop.
	* This occurred when "Pan" was enabled for the mixer in the Sound Options during a replay.
* When using robocode databases ("robot.database") created with version 1.2.3 and earlier version in version 1.2.3A, Robocode crashed in the startup with a ``ClassCastException``.

## Version 1.2.3A (12-Jan-2007)

### Bug Fix
* [Bug-57][]: ``ConcurrentModificationException``.
	* Removed a ``ConcurrentModificationException`` that occurred when processing robot events.

### Changes
* Robert D. Maupin ("Chase-san") replaced all old type Java containers like ``Vector``, ``Hashtable``, ``Enumeration`` with the newer and faster types like ``ArrayList``, ``HashMap``, and ``Set``.
	* This improves the performance a bit, especially when running in "minimized" mode.

## Version 1.2.3 (10-Jan-2007)

### Bug Fixes
* Removed ``NullPointerException`` when trying to restart the initial intro battle.
* [Bug-54][]: Flickering when constantly changing colors.
	 * Titus Chen made a fix for this fix, which occurred when the max. amount of robot colors (i.e. 256) was exceeded.
* [Bug-55][]: Incorrect score after replay.
	* Thanks goes to Titus Chen for reporting this issue + providing a fix for this issue.
* Minor bug fix in the Extract Results dialog, where an empty line was following each line of text.

### Changes

#### Added Replay Feature
* A new "Replay" button has been added to the toolbar at the buttom of the battle screen.
	* The replay feature makes it possible to replay a battle.
* In a comming version of Robocode, it will be possible to load and save replays.
* [Req-24][]: Recording of battles. (partially implemented)
	* Added "Enable replay recording" option to the Common Options for enabling and disabling replay recording as replay recording eats lots of memory.
		* When the replay recording is disabled, the "Replay" button will not be available.

#### Improved the security manager
* Robots are not allowed to access any internal Robocode packages anymore, except for the ``robocode.util`` package in order to let legacy robots access the ``robocode.util.Utils`` class, e.g. for calling ``normalRelativeAngle()`` etc.

#### New hotkey
* Hotkey added for exiting Robocode quickly.
	* It is now possible to exit from Robocode by pressing Alt+F4 in the main window of Robocode. Note that the main window must be active.

## Version 1.2.2 (14-Dec-2006)

### Bug Fixes
* [Bug-52][]: Extra hit wall events.
	* Extra ``HitWallEvents`` were occuring.
	* Thanks goes to Titus Chen for reporting this issue + providing a fix for this issue.
* [Bug-53][]: Teams not always ranked correctly.
	* Thanks goes to Titus Chen for reporting this issue + providing a fix for this issue as well.
* In addition, the ranking scores and final battle results have been made consistent.
* The radar scan arc was not painted correctly if the radar was moving towards left.
* Sometimes ``ArrayIndexOutOfBoundsExceptions`` occurred when adding and/or removing robots in the robots folder.

### Changes
* [Req-30][]: UI Control for adjusting the TPS quickly.
	* Added TPS slider to the toolbar on the battle window so the TPS can be changed quickly.
* [Req-32][]: Bullet size.
	* Bullet sizes has been improved.
	* Very small bullets will always be visible, even on large 5000x5000 battle fields.
* Removed the "Allow robots to change colors repeatedly" from the View Options.
	* This option did not have any affect as the current rendering engine always allows robots to change colors repeatedly.

## Version 1.2.1A (26-Nov-2006)

### Bug Fixes
* [Bug-51][]: Hit wall problems.
	* Hitting wall with an exact angle of 0, 90, 180 or 270 degrees caused a robot to disappear from the battlefield (could be seen with the sample robots Corner and Wall).
	* Thanks goes to Titus Chen for reporting this issue + providing a fix for this issue.

## Version 1.2.1 (24-Nov-2006)

### Bug Fixes
* [Bug-50][]: Bottom-left corner anomaly.
	* The check for wall collision did not work properly in some situations due to rounding problems with float vs double precision.
* [Bug-49][]: Robot gets stuck off-screen.
* [Bug-48][]: ``isMyFault()`` returns ``false`` despite moving toward the robot.
	* ``HitRobotEvent.isMyFault()`` returned ``false`` despite the fact that the robot was moving toward the robot it collided with.
	* This was the case when ``distanceRemaining == 0`` even though this could occur on purpose if the move was set to ``distanceRemaining``.
* [Bug-47][]: Teleportation in version 1.2.
	* Teleportation when hitting wall and ``abs(sin(heading)) > 0.00001`` or ``abs(cos(heading)) > 0.00001``.
* [Bug-46][]: Gun Method returns too soon.
	* The ``turnGun(double)`` method returned before the gun rotation had returned.
* [Bug-41][]: More bad bullet collision detections.
	* Bad bullet collision detection algorithm was replaced with [Paul Bourke's 2D line intersection algorithm](http://paulbourke.net/geometry/lineline2d/).

## Version 1.2 (05-Nov-2006)

### Bug Fixes
* [Bug-24][]: Robots hangs when running looong battles (and pausing).
	* This issue which was not really fixed in 1.1.5.
* [Bug-37][]: Bad bullet collision detection.
* [Bug-42][]: "Number of rounds" box is not tall enough on Gnome/Linux.
	* Some text fields in e.g. the "New Battle" were not tall enough to show their content on for e.g. Gnome/Linux.
* [Bug-43][]: Layout is bad for Gnome/Linux.
	 * It was hard to read the text on the buttons on the Rendering Options. The text was cut off.

### Changes

#### Security option
* [Req-21][]: Option for accessing external .jars
	* The ``NOSECURITY`` option has been extended so it is now possible to access 3rd party jar files.
	* If you want to access other jars in your robot you'll have to disable the security in Robocode by setting the ``NOSECURITY`` option to true, i.e. adding ``-DNOSECURITY=true`` in robocode.bat (under Windows) or robocode.sh (under Mac and Linux).
	* You'll also have to add the jar file to your ``CLASSPATH`` or put it into the /lib/ext folder of your Java Runtime Environment (JRE), if adding it to the ``CLASSPATH`` does not work.

#### Results can be saved in CSV File
* [Req-25][]: Save battle results to file.
	* Results can now be saved in the Comma Separated Value (CSV) File Format.
	* A "Save" button has been added to the battle results dialog.

#### Battle Results / Ranking panel
* The rank and name of the robots in the battle results dialog and in ranking panel has been splitted up into two independent colums, i.e "Rank" and "Name".
	* This was necessary in order to save the rank and name independently in a file.

#### Browser
* [Req-26][]: Invoke default browser from Help.
	* The default browser under Windows is now used when browsing e.g. the Online Help.
	* The browser.bat file has been removed as there is no need for it anymore.

## Version 1.1.5 (22-Oct-2006)

### Bug Fixes
* [Bug-40][]: Half of ``BulletHitBulletEvents`` are created improperly.
	* Fixed ``BulletHitBulletEvents`` where half of them referred to the wrong bullet.
* [Bug-39][]: Final results not always ranked correctly.
* [Bug-36][]: License text in installer has wierd image at the buttom.
	* Updated the Common Public License to the original version.

### Change
* The Ranking Panel total score is now updated on the fly.

## Version 1.1.4B (19-Oct-2006)

### Bug Fixes
* [Bug-24][]: Robots hangs when running looong battles (and pausing).
* [Bug-34][]: Getting ``null`` on ``getName()``.
	* The ``getName()`` on ``ScannedRobotEvent`` returned ``null``.
* [Bug-35][]: robocode.sh contains invalid ^M character.
	* Fixed the robocode.sh (Unix) file which contained a ^M (Microsoft DOS/Windows character), which caused this file to be unusable for starting Robocode.

## Version 1.1.4A (15-Oct-2006)

### Bug Fixes
* [Bug-33][]: Sound is cut off after a round or two.
	* The sounds were cut off after first round.
* [Bug-31][]: Ranking Panel does not save its size and position.
	* The Ranking Panel position and size was not saved in the window.properties file.
* [Bug-30][]: ``ad.Quest`` robot causes ``ConcurrentModificationException``.
	* This exception occurred in the robot event queue.
* Periodic ``NullPointerException`` removed from battle view.

## Version 1.1.4 (14-Oct-2006)

### Bug Fixes
* The Battle View was not updated on the primary monitor display on a dual monitor system.
* [Bug-28][]: Enabling sound makes Robocode crash.
	* When enabling sounds on-the-fly where it was originally disabled, Robocode crashed/halted with a ``NullPointerException``.
* [Bug-29][]: Runtime exception when opening new battle.
	* Removed a ``NullPointerException`` occurring when opening a battle the first time with a new version of Robocode.
* [Bug-21][]: Replace function generates extra tab.
	* When inserting text by 'copy and paste' or 'search and replace' into the Editor, extra tabs were sometimes added.
* [Req-19][]: Possible to install Robocode on Windows Vista.
	* Robocode icons has been updated.
* Lots of internal optimizations of Robocode has been made to speed up the game.

### Changes

#### Ranking Panel
* Ranking Panel added to the Options menu.
	* Thanks goes to Luis Crespo for this new feature.
* This panel shows the current robot rankings during battles.

#### Single-step Debugging
* It is now possible to do single-step debugging in Robocode.
	* Thanks goes to Luis Crespo for this new feature.
	* The "Pause" button has been extended into "Pause/Debug", and a "Next Turn" button is available to perform one turn at a time, which is vital for single-step debugging.
* A new ``Rules`` class was added containing helper methods and constants for Robocode rules.

#### Common Options
* [Req-23][]: Preference to allow disabling of the scorecard pop-up.
	* Common Options has been added that currently contains "Show results when battle(s) ends", which is used for enabling or disabling showing the results dialog when the battle(s) ends.

## Version 1.1.3 (20-Sep-2006) The "Java 5 and Sound" release

### Bug Fixes
* [Bug-25][]: Wrong score for 1st place results in some battles.
	* Wrong 1st place scores for robots, which got 1 point for winning and also 1 point for the death of an enemy robot, and hence got 2 points instead of just 1 point for the 1st place.
* [Req-18][]: Better notification about new Robocode versions.
	* Improved the notification about new available Robocode versions.
* [Bug-19][]: Confused updater.
	* Now Robocode will only give a notification about a new available version if the version number is greater than the version retrieved from the robocode.jar file.
	* The check interval has been changed from 10 days into 5 days.
* [Bug-26][]: Battlefield graphics is not always updated.

### Changes

#### Moved to Java 5.0
* The minimum requirement for Robocode is from now on Java 5 (1.5.0).
	* You must have at least a JRE 5.0 (1.5.0) or JDK 5.0.
	* Robocode has also been tested with the upcomming Java 6 (1.6.0) where it seems to run just fine.

#### Sound Effects added
* [Req-15][]: Optional sound effects.
	* Sounds have been added to Robocode along with Sound Options.
		* Thanks goes to Luis Crespo for this cool new feature.
	* You are able to change between available mixers (on your system).
	* Panning is supported, so that explosions in e.g. the left side of the screen is louder in the left speaker.
	* Volume is supported, so that e.g. a bullet with more power makes more noise.
	* **Note:** Some mixers performs better, but might not support volume and/or panning.
	* A new command line option, ``-nosound``, has been added in order to turn off sound support.
		* This feature should prove useful on systems without sound support.

#### New Methods for Setting Colors
* The ``setColors(bodyColor, gunColor, radarColor)`` has been reintroduced.
* The ``setColors(bodyColor, gunColor, radarColor, bulletColor, scanColor)`` has been added.
* The ``setAllColor(color)`` has been added.

#### Misc.
* [Req-17][]: New splashscreen picture.
	* The Robocode logo on the splash screen and battle view is now rendered using Java2D.
	* The layout of the Developer Options was improved a bit.

## Version 1.1.2 (20-Aug-2006) The "Robocode is now TPS centric instead of FPS centric" release

### Bug Fixes
* [Bug-22][]: Bad buildJikes.sh in 1.1.1
	* The ``buildJikes.sh`` contained the ^M (DOS return-carrige characters), which do not belong in a Unix/Linux file. ;-)
* The radar color was sometimes painted with too much lumination (white).

### Changes

#### TPS centric instead of FPS centric
* [Req-12][]: Faster framerates / Turns per second.
	* Robocode is no longer FPS (Frames Per Second) centric, meaning that 1 turn (time unit) = 1 frame.
	* Robocode is now TPS (Turns Per Second) centric, meaning that 1 turn is not necessarily equal to 1 frame anymore.
		* You specify how many turns you want to compute every second (desired TPS), and Robocode will render as many frames as possible depending on the TPS.
		* If the TPS is higher than the FPS, some turns are not rendered. However, this does not mean that turns are skipped.
	* The higher TPS, the lower the FPS will get.
	* The better graphics hardware acceleration the higher TPS and FPS.
	* Replaced the ``-fps`` (Frames Per Second) command line option with the ``-tps`` (Turns Per Second) option.
	* Now there is an option to display both the TPS and FPS in the titlebar in the View Options.

#### Rendering Options
* Added Rendering Options to the Preferences.
* It now possible to change the settings for Antialiasing, Text Antialiasing, Rendering Method, and number of rendering buffers.

#### Explosion rendering
* [Req-11][]: Explosion on robots dependent on bullet energy.
	* Explosions are no longer pre-scaled in 6 fixed sizes, but instead scaled real-time using Java2D.
	* The explosion sizes are now more precise depending on bullet damage, and the memory usage for the explosions has been brought down by not using pre-scaled explosion images.
	* This fixed the painting of explosions on the iMac, where explosions were painted as filled circles with version 1.1 and 1.1.1.
	* Bullets are now painted as filled energy balls with a size that depends on the bullet energy.
* [Req-13][]: Bullets are sometimes too small.
	* The size (circle area) is calculated so that:
	* A bullet with power = 0.1 (minimum power) uses 1 pixel.
	* A bullet with power = 3 (maximum power) uses 30 pixels.
	* In addition, explosions when a bullet hits a robot or another bullet are also depending on the bullet energy.

#### New Option
* Added the option "Visible Explosions" in the View Options to the Preferences.
* This option makes it possible to enable and disable the painting of explosions.

#### Setting Robot Colors
* The ``setColors(bodyColor, gunColor, radarColor)`` method is now deprecated.
	* Replaced by ``setBodyColor()``, ``setRadarColor()``, and ``setScanColor()``.
* [Req-10][]: Coloring of bullets and scan arcs.
	* Added ``setBulletColor()`` for changing the bullet color, and ``setScanColor()`` for changing the scan color (used for drawing scan arcs).

#### Improved Sample Robots
* [Req-20][]: Robot templates must be updated regarding deprecated methods.
	* All sample robots has been updated.
	* Deprecated methods are replaced by current methods.
	* Colors has been added to each robot, except for MyFirstRobot, which should be kept as simple as possible.

#### Added Restart Button
* [Req-14][]: Restart Button.
	* Restart button has been added in order to restart a battle.

#### New "No Display" Option
* [Req-4][]: Prevent API from loading unused features.
	* No graphical components are loaded anymore when Robocode is run from the command line with the ``-nodisplay`` option.
	* This feature has been added in order to run Robocode on Unix-like systems that don't have X installed on them or for running Robocode as a kind of server, e.g. for [RoboRumble][].

#### Added Browse Button
* [Req-16][]: Browse button in Development Options.

#### Keyboard Mnemonics
* Changed some keyboard mnemonics in the View Options in the Preferences.

## Version 1.1.1 (06-Jul-2006)

### Bug Fixes
* [Bug-16][]: Too many skipped turn because of CPU speed detection.
	* Robots than ran fine under v1.0.6 were skipping turns like crazy under v1.0.7.
	* The CPU speed detection has been changed to accept 50 times as many clock cycles than with with v1.0.7.
* [Bug-6][]: Text-Output Errors.
	* Robot text-output error has been fixed according to the solution provided by Matt (msj(at)sysmatrix(dot)net).
* [Bug-15][]: "Visible ground" option is not saved.
	* The state of the "View option" was not loaded correctly, and hence always set to enabled everytime Robocode was restarted.
* [Bug-11][]: A bug at ``updateMovement()`` in ``RobotPeer.java``.
	* The ``updateMovement()`` that checked for ``distanceRemaining > 1`` instead of ``distanceRemaining > 0`` when slowing down.
* [Bug-14][]: Radar color is wrong.
	* The radar was not colored correctly due to a bug in the coloring filter.
* [Bug-17][]: Robot editor's window list retains old windows.
	* The Robocode editor's window menu did not remove closed windows properly when muliple windows were opened in the editor.
* [Bug-12][]: Autoextract hangs on Mac 10.3.9.
* [Bug-13][]: Compiling may fail - Mac 10.3.9.
	* Issues with installing and compiling under Mac 10.3.9 were fixed.
* [Bug-18][]: Blank console in battles between two bots.
* [Req-2][]: Keep window size of "New battle" window.
	* The window position and sizes were not loaded properly from the windows.properties file.
	* The battle window is no longer reset every time a new battle is started, and the window size and position is now saved into the windows.properties file.
* Various part of Robocode did not work properly if installed into a folder containing spaces, e.g. compiling and viewing the API documentation did not work.

### Changes

#### 1200x1200 Battle Field
* [Req-3][]: More precise battlefieldsize configuration.
	* Added 1200x1200 battle field size as one of the standard sizes, and set the size step to 100.
	* This feature was added to accommodate [RoboRumble][].

#### Robot Colors
* The robot colors are now painted using a true HSL color model.
* The change to use the HSL color model fixed the bug regarding none or wrong coloring.
* Also, the lumination of the robot colors has been changed.

#### Robocode SG Support
* [Req-7][]: Graphical "debug" facilities like with RobocodeGL.
	* Added a checkbox to enable Robocode SG painting in the robot console.
	* The "Debug paint" button in the robot console has been renamed to "Paint".
* [Req-8][]: Debug Graphics - SG Option.
	* The "Paint" button enables painting in general, and by trickering "Robocode SG", the robot (debug) painting will be rendered as Robocode SG does it.

#### Command line / batch files
* Added the ``-Xmx512M`` option to the batch files extending the max. memory heap size to 512 MB.
* Added the ``-Dsun.io.useCanonCaches=false`` which fixes some ``SecurityException`` issues on robots that read from files, and also fixed the installing and compiling problem under Mac 10.3.9.

## Version 1.1 (14-Jun-2006) The "Continuation" release

### Bug Fixes
* [Bug-1][] and [Req-1][]: Multiple or hyperthreading CPUs (most P4s) hangs Robocode.
* [Bug-5][]: Robot compile error under jdk1.5.
	* The Jikes compiler has been updated to version 1.22.
* [Bug-7][]: Window won't repaint itself, window flickers in battle mode.
* [Bug-9][]: Screen flickers using Sun JDK1.5.0 in Linux.
* [Bug-8][]: Opening of the Robocode API help.
	* Help menu updated.
* [Bug-9][]: Screen flickers using Sun JDK1.5.0 in Linux.
* [Bug-10][]: Eclipse compile problem (Java 1.5) in ``RobocodeClassLoader``.
* Links in Help menu fixed, so you are able to browse the API etc.
	* Updated with links for "RoboWiki", "Yahoo Group: Robocode", and "Robocode Repository".
* [Req-5][]: Keyboard Navigation.
	* Hotkeys have been added to every button, menu, and menu option.

### Changes
* Added feature for Debug Painting.
* By implementing the ``Robot.onPaint(Graphics2D g)`` method of your robot(s), graphics of your choice will be rendered when enabling "Debug Paint" on the console window for that robot.
* [Req-6][]: Editor Improvements.
	* New "Edit" menu containing: "Undo", "Redo", "Cut", "Copy", "Paste", "Delete", "Find...", "Find next", "Replace", "Select All".
	* New "Window" menu containing "Close" and "Close All" options, entries for each open window (up to 9), "More Windows" option where you can get all open windows.
	* Added undo/redo stack.
	* Added linenumbers.
* New rendering engine based on Java2D:
	* Graphics is drawn faster as Java2D make use of hardware acceleration.
	* Robot colors are now painting using the HSB color model.
	* Graphics is resized when the battle window is resized.
	* Added "Visible ground" option in "View Options" which will paint background tiles and explosion debrises.
* The battlefield is always centered in the battle window.

## Robocode was release as Open Source on SourceForge

## Development was taken over by Flemming N. Larsen

## Version 1.0.7 (18-Feb-2005)
* Released as Open Source under Common Public License.
* New explosion graphics.
* Fixed a few bugs.
	* No longer possible to teleport when hitting walls ([Bug-3][]).
	* Docs fixed and regenerated ([Bug-4][]).
	* Fire assistance removed from ``AdvancedRobot`` ([Bug-2][]).
* Now requires Java 1.4.
* New system for calculating CPU speed.

## Version 1.0.6 (17-Jul-2002)
* Robots that perform file I/O will be allowed 240 skipped turns in a row before being stopped.
	* Other robots will still be allowed 30 in a row.
* Fixed issue with Linux where window frames were outside screen.
* Fixed reset compiler option in editor (broken in 1.0.5).

## Version 1.0.5 (15-Jul-2002)
* Updated dialogs for: Packager, Extractor, Team Creator, Compile Options.
	* So they don't hide behind main window.
* Fixed bug where New Battle dialog would hang on some systems.

## Version 1.0.4 (15-Jul-2002)
* Raised max ``setXX`` calls before disable to 10,000.
* Moved ``setXX`` and ``getXX`` warnings to only print when max is hit.
	* Previously at 300 and 3000, respectively.
* Fixed bug in ``clearAllEvents``.
* Updated Jikes compiler to version 1.16.

## Version 1.0.3 (28-Jun-2002)
* Added ``setFire(double)`` and ``setFireBullet(double)`` methods to ``AdvancedRobot``.
* Added ``getDataQuotaAvailable()`` call to ``AdvancedRobot``.
* Fixed bug: Robots taking action in ``Condition.test()``.
* Implemented better method for stopping misbehaving robots.
	* Basically, to help fix mistakes such as:

			bad: while (getGunHeat() != 0) {}
			good: while (getGunHeat() != 0) { doNothing(); }

	* Robots will be disabled after 10000 calls to ``getXX`` methods with no action.
	* Robots will be disabled after 1000 calls to ``setXX`` methods with no action.
	* Only ``getXX`` and ``setXX`` in the robot itself counts (``event.getXX`` does not).

## Version 1.0.2 (21-Jun-2002)
* Increased default filesystem quota to 200000 bytes.
* Droid leaders now start with 220 energy.
* Fixed bug that allowed robots to read other robots' data directories.
* Fixed bug that allowed invalid robot version strings.
* Fixed two bugs that allowed robots to exceed filesystem quota.

## Version 1.0.1 (23-Apr-2002)
* Team robots will always show in the robot selection dialog.
* Robots in .jar files without a .properties file will not show.
* The extension ".jar.zip" is now supported for cases where the browser renames .jar files.

## Version 1.0 (05-Apr-2002)
* New online help integrated.
	* http://robocode.alphaworks.ibm.com/help/index.html.
	* includes instructions for using Eclipse to build a Robot.
* ``onWin()`` is now called the moment you are the last surviving robot.
	* You can now do victory dances in ``onWin``.
	* Tracker and TrackFire updated to reflect this.
* Context assist will now work inside Eclipse.
* Fixed bug: ``getTeammates()`` returning ``null`` for last teammate.
* Fixed a few other small bugs.

## Version 0.99.4 (24-Mar-2002)
* Fixed scanning bug (missing scan events with small scanarcs).
* Added "Import downloaded robot" tool.
* Renamed "Packager" to "Package robot for upload".
* Added "Extract downloaded robot for editing" to Robot Editor
* Added "Press F5 to refresh" label to robot selection dialog.
* Added small battle demo when new version detected.

## Version 0.99.3 (21-Mar-2002)
* Fixed velocity bug.
	* Movement is now more optimized.
	* No more '1 2 0 -1 0 1 2 0' velocities.
* Fixed ``maxVelocity`` bug.
	* ``setMaxVelocity`` can no longer be used to instantly stop.
* Fixed first turn bug (``getTime`` returing 0 twice).
* New, more accurate CPU benchmark. (Updated Linpack benchmark).
	* Should fix '1ms per turn' bug on Win9X systems.

## Version 0.99.2 (13-Mar-2002)
* Added a development path to support external IDEs such as [Eclipse](http://eclipse.org).
	* Found in Options->Preferences.
	* Simply add the path to your robot project.
	* As usual, robots will be loaded from disk at the start of each battle.
* Improved support for RoboLeague.
* Documented ``robocode.control`` package (http://robocode.alphaworks.ibm.com/docs/control).
* Fixed bug: ``sendMessage`` causing ``StringIndexOutOfBounds``.

## Version 0.99.1 (11-Mar-2002)
* Fixed bug: Some messages were delayed.
* Fixed bug: Broken RoboLeague interoperability.
* Fixed bug: ``getSender()`` did not show version or duplicates.
* Fixed bug: RobotPackager not packaging teams correctly under Java2 1.3.

## Version 0.99 (05-Mar-2002) The "TEAMS" release
* Introducing: Teams!
	* See ``sampleteam.MyFirstTeam`` for an example.
	* Teams may consist of 2 to 10 robots.
	* To use teams, first create one or more TeamRobots, then create a Team.
	* TeamRobots will not show up in the New Battle dialog by default. You can change this behavior in Options->Preferences.
	* To create a team, select Robot->Team->Create Team.
	* You can add regular robots to a team, but they will not be able to communicate.
	* The *first* robot you add to a team becomes the "team leader". Team leaders start with 200 energy. (They are superheroes) When team leaders die, all other members of the team lose 30 energy. Protect your leader!
	* Team scoring is cumulative, but similar to normal scoring: Teams receive 50 points for each surviving team member every time an enemy dies. Teams receive ``10 * numEnemies`` points for each surviving teammate upon winning. Damage bonuses are for all damage dealt by that team Firsts, Seconds, Thirds are based on last surviving team member.
	* Teammates can damage each other.
	* Teams can be packaged like regular robots.
	* Teammates without a version will receive the team version [enclosed in square brackets].
	* Team messages are limited to 32K per message.
* Introducing: Droids.
	* Droids are robots without radar or scanning ability.
	* simply add "implements Droid" to a TeamRobot to create a droid.
	* Droids start with 120 energy (due to weight savings on the radar).
	* Droids do not show up in the New Battle dialog by default. You can change this behavior in Options->Preferences.
	* The API is unchanged, but scanning will not work.
	* See ``sampleteam.MyFirstDroid`` for an example.
* Added new class: TeamRobot.
	* See Javadocs for details.
	* Adds messaging capability to robots.
* Added new class: ``MessageEvent``
* Added new interface: ``Droid``
* Fixed bug: Duplicate robots sometimes showed up in robot selection dialog.
* Fixed bug: Default Window Size not working for some battles.

## Version 0.98.3: (08-Feb-2002) The "Everything but teams and it took too long" release
* ``setColors()`` now accepts any color (previously it had to be a default color).
	* Only works in the first round.
	* Only the first 16 robots in a battle will get their Reqed colors.
* Robots may now extend or use classes that are not in their package.
	* This allows for utility classes, when they are in the robots tree.
	* If you do not wish others to extend your robot, declare your robot class final.
	* If you do not with others to use classes from your package, do not declare them public.
	* All referenced classes will be packaged with the robot packager.
* Robots in the robotcache directory that do not have a .properties file will not show up in the robot selection dialog (done in order to support extended robots, above).
* You may now delete files in your data directory.
* Robocode will now always run at full speed when minimized.
* New Battle Dialog "Finish" button renamed to "Start Battle".
* New Battle Dialog "Start Battle" button Reqs focus when enabled.
* Robocode FAQ linked from help menu.
* Robocode now supports RoboLeague by Christian Schnell.
	* http://user.cs.tu-berlin.de/~lulli/roboleague/.
* Fixed bug: Default thread priority was low.
* Fixed bug: Robots had access to peer.
* Fixed bug: Survival seconds reported as Survival firsts.
* Fixed bug: Robots did not always receive all ``onRobotDeath`` events.
* Fixed bug: ``getTime`` returning last round's end time at start of rounds.
* Editor 'Open File' now defaults to the last opened directory (per session).
* Fixed minor editor bug when parsing for classname.
* Fixed bug: Robocode will no longer try to save the size/position of a maximized window.
* Fixed bug: Bullets hitting each other immediately with fast gun cooling rate.
* Fixed bug: Incorrect number of rounds reported when stop pressed.
* Fixed bug: Incorrect number of "seconds" and "thirds" displayed.

## Version 0.98.2 (28-Nov-2001) The "Screaming FPS" release
* Speed up performance when minimized.
* New license includes academic use.
* Fixed bug: disappearing energy/name strings.

## Version 0.98.1 (27-Nov-2001) The "Ok, NOW it's starting to feel real" release
* Fixed bug clearing scaled battles.
* Robot consoles changed back to white on dark gray.
* Fixed bug with case sensitivity in editor's suggested filename.
* Other minor tweaks and bug fixes.
* Updated Tracker and RamFire (no ``setInterruptible``).
* Added commentary and dates to this file.
* Added link to this file from help menu.

## Version 0.98 (27-Nov-2001) The "It's starting to feel real" release
* Added ``setColors(Color robotColor, Color gunColor, Color radarColor)`` to Robot.
	* By default, robots are the default blue.
	* Call this in your run method (no more than once per round) to set your robot's colors.
	* Accepts any ``System`` default colors, i.e. ``Color.red``.
* Robots are now instantiated each round.
	* You no longer need to re-initialize all your variables at the beginning of ``run()``.
	* ** Only static variables are persistent across rounds **.
* Graphics optimizations.
	* No more rotating images at the start of each battle.
	* Far more memory efficient.
* New model for CPU time.
	* Robocode now uses the [Linpack benchmark](http://www.netlib.org/benchmark/linpackjava/).
	* Used to determine how much time each robot is allowed for processing.
* Threading changes.
	* Robocode is now threadsafe (as far as I know, anyway).
	* Robot threads execute sequentially.
	* No more "busy wait" enhances performance, especially on large battles.
* Minimized optimizations.
	* When minimized, Robocode will not do any drawing.
	* FPS can really crank up (when set in options-preferences).
* Minor graphic changes (The gun is slightly further forward).
* New class hierarchy, in order to clean up the javadocs.
	* added ``_Robot`` and ``_AdvancedRobot``.
	* These hold deprecated methods and system-related things.
	* added ``_AdvancedRadiansRobot`` to clean up the ``AdvancedRobot`` docs.
	* You should still extend either ``Robot`` or ``AdvancedRobot``.
* Battles may now consist of 1-256 robots.
	* A warning confirmation will appear for > 24 robots.
	* A confirmation will appear for 1 robot.
* Robots will now appear as they are loaded.
* So large battles won't appear "hung".
* Consoles persist between battles (although they are cleared).
* Console threading is more efficient.
* Console scrolling is crisper (bug fixed).
* Console now has an unlimited size, once opened.
	* There is an 8K circular buffer before it is opened.
* ``setInterruptible()`` moved to ``AdvancedRobot``.
	* Deprecated version in ``Robot`` does nothing.
	* Note: Balancing of the ``Robot`` class still needs work.
* Skipping turns may happen more often, but is not as big a deal as it used to be.
	* with the exception of your first turn.
	* You will only lose events if you skip 2 in a row.
	* You will not be stopped unless you skip 60 turns in a row.
	* Skipped turns can be caused by loading classes from disk, writing files, etc. If this becomes a problem, I will preload all classes.
* Fixed bug with compiler when filename has a space.
* Fixed bug with getting ``BulletMissed`` and ``BulletHit`` events for same bullet.
* Fixed bug with editor locking up reading some files.
* Another round of major code organization.
* Again, probably more minor items that I already forgot about. :)
* (Later additions... such as:)
	* Invalid insert bug in editor
	* Copy/Paste from console.

## Version 0.97.4 (18-Nov-2001) The "finally, a single installer!" release
* Completely reworked install process.
* There is no longer a setup.exe installer.
* Jikes is now packaged with Robocode as the default compiler.
	* you may use javac if you prefer, and you have it.
* API docs now link to the Java API.
* Fixed bug: Editor did not close files after saving.
* Fixed bug: Unable to deselect "display robot names".
* Fixed bug: Shared static vars between instances of the same robot.
* Fixed a few graphics glitches.
* Minor doc updates.

## Version 0.97.3 (05-Nov-2001) The "hourly release" release :)
* Fixed ``NullPointerException`` when loading robots with no package.
* Added ``robocode.robot.filesystem.quota`` property to robocode.properties.
	* This is a temporary solution, which sets the default filesystem quota for robots.
	* Example: ``robocode.robot.filesystem.quota=200000`` for 200k.
* Fixed bug in editor that caused it to suggest an unreasonable filename.

## Version 0.97.2 (04-Nov-2001)
* Fixed bug that caused some robots to be stopped by the game.
* Battles/Rounds start faster (back to 0.96.2 speed).
* More lenient on CPU use.
	* You have roughly 4 whole seconds before your first move.
	* You have roughly ``50 + (1000 / fps)`` milliseconds to process each frame.
	* This is more than twice what the entire game itself gets. :)
	* Failure to take action in that amount of time results in a SkippedTurnEvent.
	* You will be removed from the round if you skip 30 turns in a round.
	* Un-deprecated ``onSkippedTurn`` and ``SkippedTurnEvent``.
	* No reasonable robot should ever receive a ``SkippedTurnEvent``... but now it's there just in case.

## Version 0.97.1 (03-Nov-2001)
* Abstract classes now work properly in the selection dialog and robot database.
* Fixed a few Javadoc bugs.
* Fixed pause/resume bug.
* Javadocs have deprecated calls again.

## Version 0.97 (02-Nov-2001) The "painful but worth it" release
* I probably missed a few things in this list. :)
* Introducing the Robot Packager.
	* Select your robot, type in a few details, and let it figure out the rest.
	* Saves details in  a .properties file (see details below).
	* Builds a .jar for you.
	* Save this .jar in your robots directory (not a subdirectory, for now).
	* You may distribute this jar as a standalone robot.
* .properties files.
	* Not required unless you are packaging your robot.
	* Built automatically by the packager.
	* Includes:
		*  Robot version
		*  Short description
		*  Optional author name
		*  Optional webpage
		*  Robocode version
		*  Flag for source included
* .jar files, and the robotcache.
	* Simply put a robot .jar file in your robots directory.
	* It will be extracted to the "robotcache".
	* Jar filename is the unique key for the robotcache.
	* Do not edit files in the robotcache, they may be overwritten. Copy them to your robots directory if you like. This will be a feature in a future version of Robocode.
* Robot Database.
	* Built and maintained for you.
	* Allows Robocode to remember which .class files are robots, and which not.
	* Press F5 in the robot selection dialog to refresh the database.
* Robot Selection dialog.
	* Divided up into packages.
	* Shows robot details (from .properties) if they exist.
* .html files.
	* Create a .html file if you like... see ``sample.SittingDuck``.
	* Linked from robot selection dialog.
* Major scoring changes.
	* 50 points every time an enemy dies while you are alive.
	* (10 * numOthers) points if you are the sole survivor
	* 1 point for each point of damage you do with bullets.
	* 2 points for each point of damage you do by ramming (see Ramming changes, below).
	* ``bonus .2 * damage`` done to a specific enemy, if you kill that robot, or
	* ``bonus .3 * damage`` done to a specific enemy, if you kill by ramming.
* Ramming changes.
	* Damage increased (from .4 to .6).
	* Only generates score if you are moving toward the enemy.
	* If you are ramming, and run out of energy, you will now be disabled instead of killed.
* "Life" replaced with "Energy".
	* A more sensible name.
	* All ``getLife()`` calls deprecated and replaced with ``getEnergy()``.
* Added ``getEnergy()`` to ``HitRobotEvent`` (I can't believe it wasn't there before!).
* Only ``Robots`` and ``AdvancedRobots`` will show in the New Battle dialog.
	* Fixes known bug listed below at the end of 0.96 changes.
* Custom events are now be cleared at the start of each round.
	* However, since many robots rely on them still existing, Robocode will currently re-add any custom events that were created at the beginning of the first round, for all remaining rounds. This is a temporary solution for backwards compatibility, and will cause a warning message in your console.
* You may now print to ``System.out``.
	* Will automatically redirect output to your console.
* Robot Editor now supports creating normal Java files.
* Added line number display to Robot Editor
* ``MoveCompleteCondition`` bug fixed.
* ``getTurnRemaining()``, ``getGunTurnRemaining()``, ``getRadarTurnRemaining()`` now return degrees.
* ``getTurnRemainingRadians()``, ``getGunTurnRemainingRadians()``, ``getRadarTurnRemainingRadians()`` added.
* Added ``setAdjustRadarForRobotTurn(boolean)``.
	* By default, set to the value of ``setAdjustRadarForGunTurn`` for backwards compatibility.
* Windows now remember their last position.
	* This is based on the preferred size of the window, so that different battlefield sizes may have different window position and sizes.
	* This is stored in the file "window.properties" which you can safely delete to reset.
* Added "Default Window Size" to Options menu.
	* Resizes the window to optimal (even better than before).
* Command-line parameters to run a battle.
	* ``robocode -battle battles\sample.battle -results out.txt -fps 250 -minimize``.
	* Results will go to ``System.out`` if you do not specify a results file.
	* All other Robocode output goes to ``System.err``.
* "Activity" is now defined as loss of 10 energy in the battle.
	* Inactivity Time is now the number of frames in which 10 energy must be lost.
	* This does not include loss due to inactivity itself, or hitting walls.
	* Prevents robots stopping the inactivity timer by calling ``fire(.1)`` every 15 seconds.
* Compiler uses ``-g`` option for debugging.
	* This *may* help those users trying to use advanced debuggers.
* Much improved "waiting for robots to start".
* More lenient on "robot has not performed any actions".
* Various javadoc fixes.
* Minor updates to template files.

## Version 0.96.2 (09-Oct-2001)
* Fixed bug in movement that allowed robots to exceed ``maxVelocity``.

## Version 0.96.1
* Added automatic version checking.
* Added ``getVelocity()`` to ``Robot``.
* Fixed minor bug in editor (caused the "do" in "doNothing" to highlight).
* ``WinException`` and ``DeathException`` now extend ``Error`` instead of ``RuntimeException``.
	* So you won't catch them by mistake with: ``catch (Exception e)``.
	* You still don't want to catch them, or you'll get no score.
* Fixed minor API doc bugs.
* Added a warning when you are calling the ``setXX`` methods too many times (300).
	* has no effect on whether the game stops you or not, it simply helps to explain why. before taking an action.
* Replaced BrowserControl with BrowserLauncher, from http://www.stanford.edu/~ejalbert/software/BrowserLauncher.
	* Should work on more systems.
* Synchronized ``tick()``.
	* Two threads cannot take action at the same time.
	* This entire area needs work.
* FPS no longer displayed when Swing doublebuffering is on.
* Added ``getHeadingRadians()`` to ``Bullet``.
* Fixed ``getHeading()`` in ``Bullet`` to return degrees.

## Version 0.96 (05-Oct-2001) The "Robocode is now my life" release
* Renamed "Battles" to "Rounds" -- a single battle consists of multiple rounds.
* Commented and updated all sample robots.
* All sample robots are now in package "sample".
* If the old samples exist, Robocode will ask you if it may delete them, when you first run it.
* Only Target, Crazy, Spinbot are still AdvancedRobots.
	* Target must be for the custom event.
	* Crazy and Spinbot call setTurn methods.
* SittingDuck is now an AdvancedRobot.
	* SittingDuck writes to the filesystem.
* Help system now uses system browser.  Hopefully.  Let me know of any issues.
* API Help menu item copied to RobocodeEditor, and uses the local copy.
* Robots may now use,extend,or inherit external classes, as long as they are in the same root package.
	* You must be in a package to use this feature.
* Added call: ``getDataDirectory()`` to ``AdvancedRobot`` -- returns ``java.io.File`` representing your data directory.
* Added call: ``getDataFile(String filename)`` to ``AdvancedRobot`` -- returns ``java.io.File`` representing a file in your data directory.
* AdvancedRobots may now read files that are in the same directory (or subdirectory) as their root package.
	* It is recommended that you only read from your data directory.
	* You may not read another robot's data directory.
	* If you extend another robot, and it calls ``getDataDirectory``, it will get YOUR data directory.
	* If you extend another robot, and manually try to read it's data directory, it will fail.
* Added classes: ``RobocodeFileOutputStream`` and ``RobocodeFileWriter``.
* AdvancedRobots may now write files to their data directory.
	* You must use ``RobocodeFileOutputStream`` or ``RobocodeFileWriter``.
	* Any other output stream will fail.
	* You may wrapper them with other output streams.
	* Example: ``PrintStream out1 = new PrintStream(new RobocodeFileOutputStream(getDataFile("my.dat")));``
	* There is a quota of 100K.
* Fixed scoring bug (as exhibited by ``rk.Rotator``).
* Fixed threads stuck in wait condition when new battle selected.
* Fixed threads stuck in ``onWin`` or ``onDeath`` events.
* Fixed threads not taking action.
* Fixed leftover threads.
* Fixed a half dozen other thread issues.
* Limited \# of worker threads a robot may have to 5.
* Limited ``print``/``println`` calls to ``out``, to 100 per turn.
* Robots now run in their own ``ThreadGroup``. (You can no longer can see other robot's threads).
* Robots now have their own ``Classloader``. (static variable ``messages`` will no longer work).
* Fixed ``NullPointerException`` in ``RobocodeOutputStream``.
* ``WinEvents`` or ``DeathEvents`` will now be the only thing in the event queue.
* Reworked event handler system, should be more robust.
* Tweaked event priorities. New default priorities are:
	* ``BulletHitBulletEvent``: 50.
* Valid priorities are 0 to 99 (100 is reserved).
* Added new method ``setInterruptible(boolean)`` to ``Robot``.
	* Can only be used while handling an event.
	* Always resets to false when the event ends.
	* Causes event handler to restart from the top, if the same event (or another event of the same priority) is generated while in the event handler following this call.
	* This will only happen if you take an action inside an event handler, that causes the same event handler to be called.
	* This makes it possible for Robots to turn and move like AdvancedRobots...
	* This has no effect on events of higher priority.
* Calling ``scan()`` inside of ``onScannedRobot()`` can now restart ``onScannedRobot``.
	* internally calls ``setInterruptible(true)`` for itself.
	* See ``sample.Corners`` for example.
* Robots of class ``Robot`` no longer take damage from hitting a wall.
* Robots of class ``AdvancedRobot`` take more damage from hitting a wall.
* Added ``isMyFault()`` to ``HitRobotEvent``.
	* Returns ``true`` if you caused the event by moving toward the other robot.
* Revamped robot to robot collisions.
	* Your movement will be marked complete if you are moving toward the other robot.
	* You will not be stopped if you are moving away from the other robot.
	* Collisions now cause more damage, but are easier to escape from.
	* This means robots without ``onHitRobot`` handlers still have a chance...
	* An event is generated every time one robot hits another.
	* If you are moving toward the other robot, you will get ``onHitRobotEvent`` with ``isMyFault() = true``.
	* If another robot is moving toward you, you will get an ``onHitRobotEvent`` with ``isMyFault() = false``.
	* If you are moving toward each other, you will get two events, with the ``isMyFault() = true``, first.
* Damage from robot to robot collisions is now a constant 0.4.
* Added ``getBearing()`` to ``onHitByBullet``.
* Added a ``Bullet`` object.
	* encapsulates owner, victim, power, velocity, X, and Y.
* Bullet hitting bullet generates ``BulletHitBulletEvent`` - methods include ``getBullet()`` and ``getHitBullet()``.
	* added ``getBullet()`` to ``HitByBulletEvent``, ``BulletHitEvent``, ``BulletMissedEvent``.
* Added ``fireBullet()``, which is exactly like ``fire()``, but returns a ``Bullet`` object.
	* I could not simply add a bullet return to fire, because it broke *all* existing robot .class files.
* You can now select from the results dialog.
* Fixed path for open/save battles on non-Windows systems.
* Fixed slashscreen for some systems.
* Fixed minor API doc bugs.
* Updated many API docs.
* Renamed and reorganized many internal classes.
* Fixed bug with allowed package names (now allows digits).
* Windows installer now defaults to c:\.
* Added ``getGunHeat()`` to ``Robot``.
	* When gunHeat is 0, you can fire.
	* ``gunHeat`` goes up by ``(1 + firePower / 5)`` when you fire.
	* This is unchanged from the v0.95.
* Added ``getGunCoolingRate()`` to ``Robot``.
* Deprecated ``getGunCharge()``.
	* Use ``getGunHeat()`` instead.
* Deprecated ``endTurn()``.
	* It will still work for now, but you should replace calls to to it with ``execute()``.
* Removed ``onSkippedTurn``.
	* Your robot will simply be stopped instead.
	* The results dialog will report "not deterministic".
	* Not deterministic means that a battle started with the exact same conditions and starting positions, may not end up with the same results. (Ok, so far, you can't test that...) :)
	* Added ``getWaitCount()`` and ``getMaxWaitCount()`` to ``AdvancedRobot``.
* Deprecated ``SkippedTurnEvent``.
	* Well, it's no longer used.
* Deprecated all ``getRobotXX`` methods.
	* Replaced with ``getXX`` methods.
* Deprecated all ``getXXDegrees`` and ``setXXDegrees`` methods.
	* Just use the ``getXX`` and ``setXX`` methods.
* Compiler will always show output, and uses the ``-deprecation`` flag.
	* so you can see that you're using a deprecated call.
	* I would like to remove all deprecated calls in the next version.
* Known bug: New battle dialog not smart enough to differentiate between Robots classes and other classes.
	* You will be able to put other classes into a battle.
	* They will sit there and do nothing.

## Version 0.95 (09-Sep-2001) "The /. release"
* Completely rewrote all windows and dialogs to use LayoutManagers.
* Linux support vastly improved (although fps still not great).
* Improved framerate calculation (should be smoother on most systems).
* Robot exceptions now all go to robot's console.
* Fixed bug in initialization - radar turn.
* Hitting a wall no longer resets inactivity counter.
* Better pause functionality (fixed bugs).
* Known bug: Help system still not using external browser.
* Smarter dialog locations.

## Version 0.94 (09-Aug-2001)
* Fixed a few bugs so Linux version would run.
* Known bug: Linux version does not run well.

## Version 0.93 (23-Apr-2001)
* Completely redone graphics for tanks, guns, radar.
* Firepower adjusted. Higher-power bullets now move slightly slower and fire slightly slower.
* Gun must 'charge up' before firing.  This avoids the "lucky shot" syndrome at the beginning of a battle.
* Added ``execute()`` method for ``AdvancedRobots``. Better name for 'endTurn'.
* Optimized drawing of explosion graphics.
* Added buttons for selecting battle size.
* Bullets can now hit each other (not perfect yet).
* Updated security manager to work with jdk1.4.
* Revised bounding box for new graphics. The graphics should no longer have a 5-pixel blank area.
* Optimized scanning code.
* Added 'color mask' to determine which parts of image should be recolored.
* Replaced splashscreen and icons.
* Java-based help, API, check new version.
* Robot menu (disabled).
* Changed ``fireDelay`` mechanism to be ``gunCharge``. Still in progress.
* Renamed to Robocode.
* Added Buttons for framerate.
* Added Buttons for battle size.

## Version 0.92 (Mar-2001)
* Added ``getTime()`` method to ``Robots``.
* Added ``getFireDelay()`` method to ``Robots``.
* Added explosions.

## Version 0.91 (Mar-2001)
* Fixed bug in ``waitFor()`` so that automatic scanning will not repeatedly generate events when the condition is ``true``.
* Fixed bugs in Tracker sample robot.
* Fixed display issues with view scan arc option.

## Version 0.9 (Mar-2001)
* Completely reworked ``scan()`` to use a sweep as the radar moves. If the radar is not moving, the scan will be a straight line.
* Added fire assistance to regular ``Robot`` classes (not ``AdvancedRobots``).
	* If you fire at a robot you see during ``scan()``, and gun/radar are together, and you ``fire()`` before doing anything else, you will fire at its center.

## Version 0.8 (Feb-2001)
* Initial release.
	* Robocode brought to IBM.

## Version 0.1 (Sep-2000)
* Development started as late-night project.




[RoboRumble]: http://robowiki.net/wiki/RoboRumble (RoboWiki - RoboRumble)
[Markdown]: http://daringfireball.net/projects/markdown/syntax (Markdown syntax)

[IRobotSnapshot.getContestantIndex()]: https://robocode.sourceforge.io/docs/robocode/robocode/control/snapshot/IRobotSnapshot.html#getContestantIndex()  (robocode.control.snapshot.IRobotSnapshot.getContestantIndex())
[IRobotSnapshot.getRobotIndex()]: https://robocode.sourceforge.io/docs/robocode/robocode/control/snapshot/IRobotSnapshot.html#getRobotIndex() (robocode.control.snapshot.IRobotSnapshot.getRobotIndex())
[IRobotSnapshot.getTeamIndex()]: https://robocode.sourceforge.io/docs/robocode/robocode/control/snapshot/IRobotSnapshot.html#getTeamIndex()  (robocode.control.snapshot.IRobotSnapshot.getTeamIndex())

[Patch-1]: http://sourceforge.net/p/robocode/patches/1/ (#1 Improved priority battle handling)

[Bug-1]:   http://sourceforge.net/p/robocode/bugs/1/    (Multiple or hyperthreading CPUs (most P4s) hangs Robocode)
[Bug-2]:   http://sourceforge.net/p/robocode/bugs/2/    (Remove firing assistance for AdvancedRobo)
[Bug-3]:   http://sourceforge.net/p/robocode/bugs/3/    (Teleportation of robot can happen (rare))
[Bug-4]:   http://sourceforge.net/p/robocode/bugs/4/    (#4 API Doc incorrect)
[Bug-5]:   http://sourceforge.net/p/robocode/bugs/5/    (Robot compile error under jdk1.5)
[Bug-6]:   http://sourceforge.net/p/robocode/bugs/6/    (Text-Output Errors)
[Bug-7]:   http://sourceforge.net/p/robocode/bugs/7/    (Window won't repaint itself, window flickers in battle mode)
[Bug-8]:   http://sourceforge.net/p/robocode/bugs/8/    (Opening of the Robocode API help)
[Bug-9]:   http://sourceforge.net/p/robocode/bugs/9/    (Screen flickers using Sun JDK1.5.0 in Linux)
[Bug-10]:  http://sourceforge.net/p/robocode/bugs/10/   (Eclipse compile problem (Java 1.5) in RobocodeClassLoader)
[Bug-11]:  http://sourceforge.net/p/robocode/bugs/11/   (A bug at updateMovement() in RobotPeer.jav)
[Bug-12]:  http://sourceforge.net/p/robocode/bugs/12/   (Autoextract hangs on Mac 10.3.9)
[Bug-13]:  http://sourceforge.net/p/robocode/bugs/13/   (Compiling may fail - Mac 10.3.9)
[Bug-14]:  http://sourceforge.net/p/robocode/bugs/14/   (Radar color is wrong)
[Bug-15]:  http://sourceforge.net/p/robocode/bugs/15/   ("Visible ground" option is not saved)
[Bug-16]:  http://sourceforge.net/p/robocode/bugs/16/   (Too many skipped turn because of CPU speed detection)
[Bug-17]:  http://sourceforge.net/p/robocode/bugs/17/   (Robot editor's window list retains old windows)
[Bug-18]:  http://sourceforge.net/p/robocode/bugs/18/   (Blank console in battles between two bots)
[Bug-19]:  http://sourceforge.net/p/robocode/bugs/19/   (Confused updater)
[Bug-21]:  http://sourceforge.net/p/robocode/bugs/21/   (Replace function generates extra tab)
[Bug-22]:  http://sourceforge.net/p/robocode/bugs/22/   (Bad buildJikes.sh in 1.1.)
[Bug-24]:  http://sourceforge.net/p/robocode/bugs/24/   (Robots hangs when running looong battles (and pausing))
[Bug-25]:  http://sourceforge.net/p/robocode/bugs/25/   (Wrong score for 1st place results in some battles)
[Bug-26]:  http://sourceforge.net/p/robocode/bugs/26/   (Battlefield graphics is not always updated)
[Bug-28]:  http://sourceforge.net/p/robocode/bugs/28/   (Enabling sound makes Robocode crash)
[Bug-29]:  http://sourceforge.net/p/robocode/bugs/29/   (Runtime exception when opening new battle)
[Bug-30]:  http://sourceforge.net/p/robocode/bugs/30/   (ad.Quest robot causes ConcurrentModificationException)
[Bug-31]:  http://sourceforge.net/p/robocode/bugs/31/   (Ranking Panel does not save its size and position)
[Bug-33]:  http://sourceforge.net/p/robocode/bugs/33/   (Sound is cut off after a round or two)
[Bug-34]:  http://sourceforge.net/p/robocode/bugs/34/   (Getting null on getName())
[Bug-35]:  http://sourceforge.net/p/robocode/bugs/35/   (robocode.sh contains invalid ^M character)
[Bug-36]:  http://sourceforge.net/p/robocode/bugs/36/   (License text in installer has wierd image at the buttom)
[Bug-37]:  http://sourceforge.net/p/robocode/bugs/37/   (Bad bullet collision detection)
[Bug-39]:  http://sourceforge.net/p/robocode/bugs/39/   (Final results not always ranked correctly)
[Bug-40]:  http://sourceforge.net/p/robocode/bugs/40/   (Half of BulletHitBulletEvents are created improperly)
[Bug-41]:  http://sourceforge.net/p/robocode/bugs/41/   (More bad bullet collision detections)
[Bug-42]:  http://sourceforge.net/p/robocode/bugs/42/   ("Number of rounds" box is not tall enough on Gnome/Linux)
[Bug-43]:  http://sourceforge.net/p/robocode/bugs/43/   (Layout is bad for Gnome/Linux)
[Bug-46]:  http://sourceforge.net/p/robocode/bugs/46/   (Gun Method returns too soon)
[Bug-47]:  http://sourceforge.net/p/robocode/bugs/47/   (Teleportation in version 1.2)
[Bug-48]:  http://sourceforge.net/p/robocode/bugs/48/   (isMyFault() returns false despite moving toward the robot)
[Bug-49]:  http://sourceforge.net/p/robocode/bugs/49/   (Robot gets stuck off-screen)
[Bug-50]:  http://sourceforge.net/p/robocode/bugs/50/   (Bottom-left corner anomaly)
[Bug-51]:  http://sourceforge.net/p/robocode/bugs/51/   (Hit wall problems)
[Bug-52]:  http://sourceforge.net/p/robocode/bugs/52/   (Extra hit wall events)
[Bug-53]:  http://sourceforge.net/p/robocode/bugs/53/   (Teams not always ranked correctly)
[Bug-54]:  http://sourceforge.net/p/robocode/bugs/54/   (Flickering when constantly changing colors)
[Bug-55]:  http://sourceforge.net/p/robocode/bugs/55/   (Incorrect score after replay)
[Bug-57]:  http://sourceforge.net/p/robocode/bugs/57/   (ConcurrentModificationException)
[Bug-58]:  http://sourceforge.net/p/robocode/bugs/58/   (NullPointerException during replay)
[Bug-59]:  http://sourceforge.net/p/robocode/bugs/59/   (Issue when setting the priority of a BulletHitBulletEvent)
[Bug-60]:  http://sourceforge.net/p/robocode/bugs/60/   (ConcurrentModificationException when extracting robots)
[Bug-61]:  http://sourceforge.net/p/robocode/bugs/61/   (Hang when checking for new version with no Internet access)
[Bug-62]:  http://sourceforge.net/p/robocode/bugs/62/   (Memory "Leak")
[Bug-63]:  http://sourceforge.net/p/robocode/bugs/63/   (BulletHitBullet only destroys one bullet)
[Bug-64]:  http://sourceforge.net/p/robocode/bugs/64/   (Exception when referencing length of an array of String)
[Bug-65]:  http://sourceforge.net/p/robocode/bugs/65/   (Cannot run robocode after installation)
[Bug-66]:  http://sourceforge.net/p/robocode/bugs/66/   (Crash: starting a new round while et.Predator 1.8 is playing)
[Bug-67]:  http://sourceforge.net/p/robocode/bugs/67/   (Some issues with MessageEvent + priority)
[Bug-68]:  http://sourceforge.net/p/robocode/bugs/68/   (Preferences not saved)
[Bug-69]:  http://sourceforge.net/p/robocode/bugs/69/   (Robot causes Null Pointer Exception)
[Bug-70]:  http://sourceforge.net/p/robocode/bugs/70/   (Version 1.2.6A incompatible with Roboleague)
[Bug-71]:  http://sourceforge.net/p/robocode/bugs/71/   (RobocodeEngine becomes slower the more battles that are run)
[Bug-72]:  http://sourceforge.net/p/robocode/bugs/72/   (The game won't play sounds on 2nd launch)
[Bug-73]:  http://sourceforge.net/p/robocode/bugs/73/   (Sound don't work)
[Bug-74]:  http://sourceforge.net/p/robocode/bugs/74/   (Java FilePermission error during startup)
[Bug-75]:  http://sourceforge.net/p/robocode/bugs/75/   (getTeammates() problem)
[Bug-76]:  http://sourceforge.net/p/robocode/bugs/76/   (Unique error)
[Bug-78]:  http://sourceforge.net/p/robocode/bugs/78/   (Robots are disabled with no timer or countdown)
[Bug-79]:  http://sourceforge.net/p/robocode/bugs/79/   (Output displayed in bursts)
[Bug-80]:  http://sourceforge.net/p/robocode/bugs/80/   (Results file is empty with the command line)
[Bug-81]:  http://sourceforge.net/p/robocode/bugs/80/   (Replay recording does not record paintings)
[Bug-82]:  http://sourceforge.net/p/robocode/bugs/82/   (Undo comment does not change font color of code)
[Bug-83]:  http://sourceforge.net/p/robocode/bugs/83/   (Ranking Panel Does not update number of competitors)
[Bug-84]:  http://sourceforge.net/p/robocode/bugs/84/   (Preferences page problem on machine w/out sound card)
[Bug-85]:  http://sourceforge.net/p/robocode/bugs/85/   (No window position in-bounding)
[Bug-86]:  http://sourceforge.net/p/robocode/bugs/86/   (Using UI removes focus from interactive bots)
[Bug-87]:  http://sourceforge.net/p/robocode/bugs/87/   (Round indicator incorrect)
[Bug-88]:  http://sourceforge.net/p/robocode/bugs/88/   (Scorch layer)
[Bug-90]:  http://sourceforge.net/p/robocode/bugs/90/   (Limit per round, not turn)
[Bug-91]:  http://sourceforge.net/p/robocode/bugs/91/   (ConcurrentModificationException)
[Bug-92]:  http://sourceforge.net/p/robocode/bugs/92/   (RoboRumble tries to connect with GUI)
[Bug-93]:  http://sourceforge.net/p/robocode/bugs/93/   (onPaint(Graphics2D g) called prematurely)
[Bug-94]:  http://sourceforge.net/p/robocode/bugs/94/   (Inconsistent Behavoir of RobocodeEngine.setVisible())
[Bug-95]:  http://sourceforge.net/p/robocode/bugs/95/   (OutOfMemory: Robots are Being Left on the Stack)
[Bug-96]:  http://sourceforge.net/p/robocode/bugs/96/   (Initializing Label even when no display)
[Bug-97]:  http://sourceforge.net/p/robocode/bugs/97/   (Exception when packaging robots)
[Bug-98]:  http://sourceforge.net/p/robocode/bugs/98/   (When minimized doesn't show actual tps)
[Bug-99]:  http://sourceforge.net/p/robocode/bugs/99/   (Clicking on a bottom area results in ClassCastException)
[Bug-100]: http://sourceforge.net/p/robocode/bugs/100/  (Double-clicking "restart")
[Bug-101]: http://sourceforge.net/p/robocode/bugs/101/  ("Keyboard lockup" with interactive robots)
[Bug-102]: http://sourceforge.net/p/robocode/bugs/102/  (Bots can hold memory after being destroyed)
[Bug-103]: http://sourceforge.net/p/robocode/bugs/103/  (ConcurrentModificationException)
[Bug-104]: http://sourceforge.net/p/robocode/bugs/104/  (Reproducable scoring bug)
[Bug-105]: http://sourceforge.net/p/robocode/bugs/105/  (testingCondition flag not reset)
[Bug-106]: http://sourceforge.net/p/robocode/bugs/106/  (Incorrect repaint of paused battlefield)
[Bug-107]: http://sourceforge.net/p/robocode/bugs/107/  (Bullet hit locations reported inaccurately)
[Bug-108]: http://sourceforge.net/p/robocode/bugs/108/  (It is possible to restart a battle without any robots)
[Bug-110]: http://sourceforge.net/p/robocode/bugs/110/  (Partial match recording replay)
[Bug-111]: http://sourceforge.net/p/robocode/bugs/111/  (Wasted time at end of each round)
[Bug-112]: http://sourceforge.net/p/robocode/bugs/112/  (Forcing stop: no score will be generated)
[Bug-113]: http://sourceforge.net/p/robocode/bugs/113/  (-battle option runs at full speed)
[Bug-114]: http://sourceforge.net/p/robocode/bugs/114/  (Wait Interrupted Message)
[Bug-115]: http://sourceforge.net/p/robocode/bugs/115/  (AWTException with RoboRumble on OS X)
[Bug-116]: http://sourceforge.net/p/robocode/bugs/116/  (Exclude filter removes bots from the RoboRumble)
[Bug-117]: http://sourceforge.net/p/robocode/bugs/117/  (Sound card is being dodgey/not detected by OS causes error)
[Bug-118]: http://sourceforge.net/p/robocode/bugs/118/  (Battles fail when executing with Eclipse debugger)
[Bug-120]: http://sourceforge.net/p/robocode/bugs/120/  (The renderering is slower on Vista than XP)
[Bug-122]: http://sourceforge.net/p/robocode/bugs/122/  (Not all shortcut keys work on MacOS)
[Bug-123]: http://sourceforge.net/p/robocode/bugs/123/  (Compiler fails to build due to CR/LF in scripts)
[Bug-125]: http://sourceforge.net/p/robocode/bugs/125/  (TimeoutExceptions occur when debugging in Eclipse)
[Bug-130]: http://sourceforge.net/p/robocode/bugs/130/  (Various usability issues)
[Bug-131]: http://sourceforge.net/p/robocode/bugs/131/  (Sometimes the compiler window hangs)
[Bug-134]: http://sourceforge.net/p/robocode/bugs/134/  (Robot problem after Options->Clean robot cache)
[Bug-135]: http://sourceforge.net/p/robocode/bugs/135/  (-battle broken)
[Bug-137]: http://sourceforge.net/p/robocode/bugs/137/  (Roborumble "ITERATE" broken)
[Bug-139]: http://sourceforge.net/p/robocode/bugs/139/  (Bug in RobotPeer.updateMovement?)
[Bug-141]: http://sourceforge.net/p/robocode/bugs/141/  (The -DROBOTPATH=<path> option does not work)
[Bug-142]: http://sourceforge.net/p/robocode/bugs/142/  (Broken .sh files)
[Bug-143]: http://sourceforge.net/p/robocode/bugs/143/  (Blank console window when compiling)
[Bug-144]: http://sourceforge.net/p/robocode/bugs/144/  (drawArc does not work as expected)
[Bug-146]: http://sourceforge.net/p/robocode/bugs/146/  (Spammy output when running roborumble)
[Bug-147]: http://sourceforge.net/p/robocode/bugs/147/  (gunHeat is negative)
[Bug-148]: http://sourceforge.net/p/robocode/bugs/148/  (Wrong bullet power)
[Bug-149]: http://sourceforge.net/p/robocode/bugs/149/  (Replay exception)
[Bug-151]: http://sourceforge.net/p/robocode/bugs/151/  (Exception when changing between Robot to AdvancedRobot)
[Bug-153]: http://sourceforge.net/p/robocode/bugs/153/  (Junior Robot turnAheadRight bug)
[Bug-154]: http://sourceforge.net/p/robocode/bugs/154/  (Robot name missing when replaying XML record)
[Bug-156]: http://sourceforge.net/p/robocode/bugs/156/  (Spammy output on robot console windows)
[Bug-158]: http://sourceforge.net/p/robocode/bugs/158/  (Ubuntu throws NullPointerException in main)
[Bug-159]: http://sourceforge.net/p/robocode/bugs/159/  (Installation fail on windows if directory contain space)
[Bug-160]: http://sourceforge.net/p/robocode/bugs/160/  (Battle Results screen displaying old results)
[Bug-161]: http://sourceforge.net/p/robocode/bugs/161/  (Robot disabled by any other than losing energy can recover)
[Bug-162]: http://sourceforge.net/p/robocode/bugs/162/  (Team battle)
[Bug-163]: http://sourceforge.net/p/robocode/bugs/163/  (Spaces or native names in name of robocode directory)
[Bug-164]: http://sourceforge.net/p/robocode/bugs/164/  (Compiler Classpath Suggestion)
[Bug-165]: http://sourceforge.net/p/robocode/bugs/165/  (NullPointerException when using -battle option from cmd-line)
[Bug-166]: http://sourceforge.net/p/robocode/bugs/166/  (Bots referencing robocode.robocodeGL broken)
[Bug-168]: http://sourceforge.net/p/robocode/bugs/168/  (Bots inconviently stop working if they go over time limit)
[Bug-169]: http://sourceforge.net/p/robocode/bugs/169/  (pe.SandboxDT_3.02 stopped working in 1.6.2 and later version)
[Bug-170]: http://sourceforge.net/p/robocode/bugs/170/  (Robot Colors don't stick between rounds)
[Bug-171]: http://sourceforge.net/p/robocode/bugs/171/  (A battleview size exceed 800x600 filled with black)
[Bug-172]: http://sourceforge.net/p/robocode/bugs/172/  (Robot console fails to display some deaths/wins)
[Bug-173]: http://sourceforge.net/p/robocode/bugs/173/  (Robot packager can be activated once per running)
[Bug-174]: http://sourceforge.net/p/robocode/bugs/174/  (Robot console is sometimes empty)
[Bug-175]: http://sourceforge.net/p/robocode/bugs/175/  (Development robots cause problems with data files)
[Bug-177]: http://sourceforge.net/p/robocode/bugs/177/  (Open battle menu dialog is not loading robots)
[Bug-178]: http://sourceforge.net/p/robocode/bugs/178/  (Typing to find bot no longer works)
[Bug-179]: http://sourceforge.net/p/robocode/bugs/179/  (Event.setTime() method should not be hidden)
[Bug-181]: http://sourceforge.net/p/robocode/bugs/181/  (API: Typo in Documentation onBulletMissed(BulletMissedEvent))
[Bug-176]: http://sourceforge.net/p/robocode/bugs/176/  (Editor UNDO does delete the line when no undo left)
[Bug-180]: http://sourceforge.net/p/robocode/bugs/180/  (Editor: Find (set cursor position))
[Bug-182]: http://sourceforge.net/p/robocode/bugs/182/  (roborumble.sh and teamrumble.sh are broken)
[Bug-183]: http://sourceforge.net/p/robocode/bugs/183/  (NullPointerException in BattlesRunner.runBattlesImpl)
[Bug-184]: http://sourceforge.net/p/robocode/bugs/184/  (Custom event priority broken)
[Bug-185]: http://sourceforge.net/p/robocode/bugs/185/  (Webpage button lay over robot description)
[Bug-186]: http://sourceforge.net/p/robocode/bugs/186/  (Rounds number do not saved between run)
[Bug-187]: http://sourceforge.net/p/robocode/bugs/187/  (Not enough java memory allocated in launch scripts)
[Bug-188]: http://sourceforge.net/p/robocode/bugs/188/  (Meleerumble using 2 bots instead of 10)
[Bug-189]: http://sourceforge.net/p/robocode/bugs/189/  (API - cannot subclass Event in 1.7.1)
[Bug-190]: http://sourceforge.net/p/robocode/bugs/190/  (Errors with some robot classes when rebuilding database)
[Bug-191]: http://sourceforge.net/p/robocode/bugs/191/  (EOFException during repository rebuild)
[Bug-192]: http://sourceforge.net/p/robocode/bugs/192/  (Fair Play!)
[Bug-193]: http://sourceforge.net/p/robocode/bugs/193/  (TeamRumble uploading result for Robot instead of team)
[Bug-194]: http://sourceforge.net/p/robocode/bugs/194/  (JarJar multi-registration)
[Bug-195]: http://sourceforge.net/p/robocode/bugs/195/  (Client tries to remove all participants)
[Bug-196]: http://sourceforge.net/p/robocode/bugs/196/  (Wrong file path used for development bots)
[Bug-197]: http://sourceforge.net/p/robocode/bugs/197/  (Melee rumble doesn't use "smart battles")
[Bug-199]: http://sourceforge.net/p/robocode/bugs/199/  (FontMetrics StackOverflowError)
[Bug-200]: http://sourceforge.net/p/robocode/bugs/200/  (Graphics2D.setFont() has no effect)
[Bug-201]: http://sourceforge.net/p/robocode/bugs/201/  (setMaxVelocity(lower than current) + reverse direction bug)
[Bug-202]: http://sourceforge.net/p/robocode/bugs/202/  (Installer says to run robocode.jar)
[Bug-204]: http://sourceforge.net/p/robocode/bugs/204/  (Nanobot rumble not sending melee or team parameters)
[Bug-205]: http://sourceforge.net/p/robocode/bugs/205/  (Wrong survival scores sent by rumble client)
[Bug-206]: http://sourceforge.net/p/robocode/bugs/206/  (Funny behaviors with robot graphics/painting)
[Bug-207]: http://sourceforge.net/p/robocode/bugs/207/  (Access denied javax.swing -DNOSECURITY=true)
[Bug-208]: http://sourceforge.net/p/robocode/bugs/208/  (Does not extract .properties files into bot data dirs)
[Bug-209]: http://sourceforge.net/p/robocode/bugs/209/  ([Codesize] Invalid entry point in codesize-1.1.jar)
[Bug-210]: http://sourceforge.net/p/robocode/bugs/210/  (Bullet and Ram Damage Bonuses are wrong)
[Bug-212]: http://sourceforge.net/p/robocode/bugs/212/  (Team jar files reported as corrupted)
[Bug-213]: http://sourceforge.net/p/robocode/bugs/213/  (NullPointerException when setting classpath directory)
[Bug-214]: http://sourceforge.net/p/robocode/bugs/214/  (Accel/decel rules introduced in 1.7.1.3 causes trouble)
[Bug-215]: http://sourceforge.net/p/robocode/bugs/215/  (Missed onRobotDeath events)
[Bug-216]: http://sourceforge.net/p/robocode/bugs/216/  (Sometimes too few results for robots are displayed)
[Bug-218]: http://sourceforge.net/p/robocode/bugs/218/  (Robocode enters infinite loop with the Restart button)
[Bug-222]: http://sourceforge.net/p/robocode/bugs/222/  (Some of sound not working)
[Bug-226]: http://sourceforge.net/p/robocode/bugs/226/  (java.io.FileNotFoundException in RobotFileSystemManager.init)
[Bug-227]: http://sourceforge.net/p/robocode/bugs/227/  (Can't load Katana 1.0 or DrussGT 1.3.1wilo)
[Bug-228]: http://sourceforge.net/p/robocode/bugs/228/  (Krabb.sliNk.GarmTeam 0.9v locks up in new beta)
[Bug-229]: http://sourceforge.net/p/robocode/bugs/229/  (IllegalArgumentException on painting in some robots?)
[Bug-230]: http://sourceforge.net/p/robocode/bugs/230/  (Lockup on start if too many bots in robots dir)
[Bug-231]: http://sourceforge.net/p/robocode/bugs/231/  (Lockup on start if too many bots in robots dir (cont'd))
[Bug-232]: http://sourceforge.net/p/robocode/bugs/232/  (Graphics2D.getTransform() throws NPE)
[Bug-233]: http://sourceforge.net/p/robocode/bugs/233/  ("Teleport")
[Bug-234]: http://sourceforge.net/p/robocode/bugs/234/  (Source is not included)
[Bug-236]: http://sourceforge.net/p/robocode/bugs/236/  (Robot Editor doesn't accept packagename with dot (.) in it)
[Bug-237]: http://sourceforge.net/p/robocode/bugs/237/  (OS X 10.6: Cannot run Robocode from robocode.sh)
[Bug-238]: http://sourceforge.net/p/robocode/bugs/238/  (OS X 10.6: The editor cannot see the JD)
[Bug-240]: http://sourceforge.net/p/robocode/bugs/240/  (morbid.MorbidPriest_1.0 fails to load)
[Bug-243]: http://sourceforge.net/p/robocode/bugs/243/  (Robot console text sometimes disappears)
[Bug-244]: http://sourceforge.net/p/robocode/bugs/244/  (Robot static data isn't being GCed after battle)
[Bug-245]: http://sourceforge.net/p/robocode/bugs/245/  (Removing directories from "development options" doesn't work)
[Bug-247]: http://sourceforge.net/p/robocode/bugs/247/  (Version ordering is somewhat strange with letters)
[Bug-250]: http://sourceforge.net/p/robocode/bugs/250/  (Installer installs AutoExtract$1.class)
[Bug-252]: http://sourceforge.net/p/robocode/bugs/252/  (yk.JahRoslav 1.1 throws WinException)
[Bug-254]: http://sourceforge.net/p/robocode/bugs/254/  (Roborumble doesn't upload with EXECUTE=NOT)
[Bug-255]: http://sourceforge.net/p/robocode/bugs/255/  (java.lang.Error: Interrupted attempt to aquire read lock)
[Bug-257]: http://sourceforge.net/p/robocode/bugs/257/  (Team RoboRumble uploading is broken)
[Bug-258]: http://sourceforge.net/p/robocode/bugs/258/  (isTeammate() called on null gives NullPointerException)
[Bug-259]: http://sourceforge.net/p/robocode/bugs/259/  (jlm.javaDisturbance loses substantial score in 1.7.2 Beta)
[Bug-260]: http://sourceforge.net/p/robocode/bugs/260/  (ArrayIndexOutOfBoundsException when starting team battle)
[Bug-261]: http://sourceforge.net/p/robocode/bugs/261/  ((.NET) condition tested on concurrently modified collection)
[Bug-262]: http://sourceforge.net/p/robocode/bugs/262/  (TeamRumble: Cannot find robot in nested .jar files)
[Bug-263]: http://sourceforge.net/p/robocode/bugs/263/  (Cannot extract downloaded robot for editing in Robot Editor)
[Bug-265]: http://sourceforge.net/p/robocode/bugs/265/  (Occasionally losing the bit of text in the robot console)
[Bug-267]: http://sourceforge.net/p/robocode/bugs/267/  (Strange issue first time running roborumble in 1.7.2.0 Beta2)
[Bug-269]: http://sourceforge.net/p/robocode/bugs/269/  (Minor visual bug - Currently selected robot gets covered)
[Bug-270]: http://sourceforge.net/p/robocode/bugs/270/  (Strange thread exceptions with kid.DeltaSquad in 1.7)
[Bug-271]: http://sourceforge.net/p/robocode/bugs/271/  (Battle engine consumes more CPU power over time)
[Bug-272]: http://sourceforge.net/p/robocode/bugs/272/  (isTeammate() sometimes returns false with teammates)
[Bug-274]: http://sourceforge.net/p/robocode/bugs/274/  ("Ignoring" messages in rumble are duplicated)
[Bug-275]: http://sourceforge.net/p/robocode/bugs/275/  (Duplicate version numbers prevents uploading)
[Bug-276]: http://sourceforge.net/p/robocode/bugs/276/  (tzu.TheArtOfWar 1.2 gets NullPointerExceptions)
[Bug-277]: http://sourceforge.net/p/robocode/bugs/277/  (Problems with Graphics2D.fill/draw(Shape))
[Bug-278]: http://sourceforge.net/p/robocode/bugs/278/  (Attempting to install robocode over an existing install NPEs)
[Bug-280]: http://sourceforge.net/p/robocode/bugs/280/  (NPE when uploading results)
[Bug-281]: http://sourceforge.net/p/robocode/bugs/281/  (The robocode.command is missing the execute permissions bit)
[Bug-282]: http://sourceforge.net/p/robocode/bugs/282/  (Cannot see robot with no package in New Battle dialog)
[Bug-283]: http://sourceforge.net/p/robocode/bugs/283/  (Possible for robot to kill other robot threads)
[Bug-284]: http://sourceforge.net/p/robocode/bugs/284/  (Robot Packager doesn't package source file in Eclipse proj)
[Bug-285]: http://sourceforge.net/p/robocode/bugs/285/  (Robot Packaging Wizard doesn't save value for Next & Back)
[Bug-286]: http://sourceforge.net/p/robocode/bugs/286/  (ClassNotFoundException at RobotClassLoader.java:271)
[Bug-287]: http://sourceforge.net/p/robocode/bugs/287/  (Zipped robots data files are not extracted)
[Bug-288]: http://sourceforge.net/p/robocode/bugs/288/  ("skipped" turns at start with -Ddebug=true)
[Bug-289]: http://sourceforge.net/p/robocode/bugs/289/  (Exclude filters not working)
[Bug-290]: http://sourceforge.net/p/robocode/bugs/290/  (Development Options remove wrong item)
[Bug-291]: http://sourceforge.net/p/robocode/bugs/291/  (JavaDoc missing _Robot and similar)
[Bug-292]: http://sourceforge.net/p/robocode/bugs/292/  (Robot PrintStream doesn't handle write in a portable fashion)
[Bug-293]: http://sourceforge.net/p/robocode/bugs/293/  (Wrong headings with the JuniorRobot)
[Bug-297]: http://sourceforge.net/p/robocode/bugs/297/  (x,y coords between BulletHitEvent & HitByBulletEvent differ)
[Bug-299]: http://sourceforge.net/p/robocode/bugs/299/  (Custom events no longer firing after clearing event queue)
[Bug-301]: http://sourceforge.net/p/robocode/bugs/301/  (getTurnRateRadians incorrect for negative velocity)
[Bug-302]: http://sourceforge.net/p/robocode/bugs/302/  (Hide enemy name implementation bug)
[Bug-303]: http://sourceforge.net/p/robocode/bugs/303/  (bullet.equals semantic has been change in 1.7.3.0 version)
[Bug-304]: http://sourceforge.net/p/robocode/bugs/304/  (setColor(null) causes NPE)
[Bug-305]: http://sourceforge.net/p/robocode/bugs/305/  (TeamRumble priority battles bug)
[Bug-306]: http://sourceforge.net/p/robocode/bugs/306/  (Rumble sh scripts for launching do not handle spaces in path)
[Bug-307]: http://sourceforge.net/p/robocode/bugs/306/  (Console output cannot handle non-ascii names)
[Bug-308]: http://sourceforge.net/p/robocode/bugs/308/  (ConcurrentModificationException in URLJarCollector)
[Bug-309]: http://sourceforge.net/p/robocode/bugs/309/  (robot in development generates * into filename)
[Bug-310]: http://sourceforge.net/p/robocode/bugs/310/  (Interface Robot skips turns at end of round)
[Bug-311]: http://sourceforge.net/p/robocode/bugs/311/  (out.write(int) uses up allocated printing quickly)
[Bug-312]: http://sourceforge.net/p/robocode/bugs/312/  (Enabling Paint Freezes Robocode)
[Bug-313]: http://sourceforge.net/p/robocode/bugs/313/  (Robocode .NET does not work on Java 7)
[Bug-315]: http://sourceforge.net/p/robocode/bugs/315/  (Unable to change drawing color in .NET (C#))
[Bug-318]: http://sourceforge.net/p/robocode/bugs/318/  (Installer throws NumberFormatException on Linux 3.0)
[Bug-319]: http://sourceforge.net/p/robocode/bugs/319/  (Package name allows bad chars)
[Bug-320]: http://sourceforge.net/p/robocode/bugs/320/  ("About" window colors are awful)
[Bug-323]: http://sourceforge.net/p/robocode/bugs/323/  (Robocode can't find the ECJ (Eclipse Compiler for Java))
[Bug-326]: http://sourceforge.net/p/robocode/bugs/326/  (Package of team fails to load in team battles)
[Bug-328]: http://sourceforge.net/p/robocode/bugs/328/  (Issue with the robocode.dll + # chars in the path for a dll)
[Bug-331]: http://sourceforge.net/p/robocode/bugs/331/  (RoboRumble client has infinite timeout)
[Bug-332]: http://sourceforge.net/p/robocode/bugs/332/  (Use OpenGL backend under linux)
[Bug-333]: http://sourceforge.net/p/robocode/bugs/333/  (.NET runs release dll not debug dll so can't debug)
[Bug-334]: http://sourceforge.net/p/robocode/bugs/334/  (Snapshot API never shows bullets in "HIT_WALL" status)
[Bug-335]: http://sourceforge.net/p/robocode/bugs/335/  (Skipped turns ... issues)
[Bug-336]: http://sourceforge.net/p/robocode/bugs/336/  (Skipped turns ... issues)
[Bug-337]: http://sourceforge.net/p/robocode/bugs/337/  (Hangups with New Editor in 1.7.4.0)
[Bug-338]: http://sourceforge.net/p/robocode/bugs/338/  ("Accept-Encoding: gzip" not in Roborumble HTTP Headers)
[Bug-339]: http://sourceforge.net/p/robocode/bugs/339/  (All Text Missing)
[Bug-340]: http://sourceforge.net/p/robocode/bugs/340/  (Robocode crash on window resize (linux-opengl))
[Bug-341]: http://sourceforge.net/p/robocode/bugs/341/  (InteractiveRobots gets error "After the event was added...")
[Bug-342]: http://sourceforge.net/p/robocode/bugs/342/  (New bots not given priority)
[Bug-344]: http://sourceforge.net/p/robocode/bugs/344/  (BattleAdaptor missing in robocode.control.events)
[Bug-345]: http://sourceforge.net/p/robocode/bugs/345/  (Graphics still being rendered when minimized)
[Bug-346]: http://sourceforge.net/p/robocode/bugs/346/  (Cannot extract sources from robot packages)
[Bug-347]: http://sourceforge.net/p/robocode/bugs/347/  (/bin/sh^M bad interpreter)
[Bug-348]: http://sourceforge.net/p/robocode/bugs/348/  (.NET: UnauthorizedAccessException in AppDomainShell.Dispose())
[Bug-349]: http://sourceforge.net/p/robocode/bugs/349/  (Instances of RobocodeEngine don't seem to be independant - memory leak and performance decrease)
[Bug-350]: http://sourceforge.net/p/robocode/bugs/350/  (Bullet id from battle record XML file is sometimes -1 causing a NumberFormatException)
[Bug-351]: http://sourceforge.net/p/robocode/bugs/351/  (Robot.onBattleEnded(BattleEndedEvent) provides wrong scores)
[Bug-352]: http://sourceforge.net/p/robocode/bugs/352/  (Results from BattleCompletedEvent.getIndexedResults() are always sorted)
[Bug-353]: http://sourceforge.net/p/robocode/bugs/353/  (RobocodeEngine.setVisible() can cause a NullPointerException)
[Bug-354]: http://sourceforge.net/p/robocode/bugs/354/  (Replaying an XML record can cause an ArrayIndexOutOfBoundsException)
[Bug-355]: http://sourceforge.net/p/robocode/bugs/355/  (Priority battles not accepted for mini/micro/nano rumbles)
[Bug-356]: http://sourceforge.net/p/robocode/bugs/356/  (Update Roborumble URLs from Darkcanuck to LiteRumble)
[Bug-357]: http://sourceforge.net/p/robocode/bugs/357/  (Tab characters are inserted in the last line of a robot source file when opening it)
[Bug-358]: http://sourceforge.net/p/robocode/bugs/358/  (Robot in default package cannot write to files. Should at least get a warning)
[Bug-361]: http://sourceforge.net/p/robocode/bugs/361/  (Problem in the text editor related with the .java file modification)
[Bug-362]: http://sourceforge.net/p/robocode/bugs/362/  (Rumble client does not remove participants in wrong codesize group)
[Bug-363]: http://sourceforge.net/p/robocode/bugs/363/  (No Last Survivor Bonus being given)
[Bug-364]: http://sourceforge.net/p/robocode/bugs/364/  (Robot Packager does not include the robot data dir in the .jar file)
[Bug-366]: http://sourceforge.net/p/robocode/bugs/366/  (Receiving enemy's real name on HitByBulletEvent)
[Bug-368]: http://sourceforge.net/p/robocode/bugs/368/  (Issues with sentries)
[Bug-369]: http://sourceforge.net/p/robocode/bugs/369/  (RoboRumble: NoClassDefFoundError for CodeSizeCalculator)
[Bug-370]: http://sourceforge.net/p/robocode/bugs/370/  (Robot Packager cannot find robot .properties file in development path)
[Bug-371]: http://sourceforge.net/p/robocode/bugs/371/  (High cpu usage on editor)
[Bug-372]: http://sourceforge.net/p/robocode/bugs/372/  (Cannot load battle file when -DNOSECURITY=true is enabled - java.lang.IllegalAccessError)
[Bug-373]: http://sourceforge.net/p/robocode/bugs/373/  (Wrong robot size calculation in version 1.9.0 - 1.9.2)
[Bug-374]: http://sourceforge.net/p/robocode/bugs/374/  (Wrong size report for minirumble in v1.9.2.2)
[Bug-375]: http://sourceforge.net/p/robocode/bugs/375/  (Wrong width and height returned for .NET robots)
[Bug-378]: http://sourceforge.net/p/robocode/bugs/378/  (robocode.robocodeGL.system.GLRenderer ClassNotFoundException)
[Bug-380]: http://sourceforge.net/p/robocode/bugs/380/  (Yet another historical bot related bug)
[Bug-381]: http://sourceforge.net/p/robocode/bugs/381/  (Improve feedback after entering empty package name during robot creation)
[Bug-382]: http://sourceforge.net/p/robocode/bugs/382/  (Unable to run robocode.bat -- AccessControlException)
[Bug-383]: http://sourceforge.net/p/robocode/bugs/383/  (Java 8 lamba expressions cause ClassNotFoundException)
[Bug-386]: http://sourceforge.net/p/robocode/bugs/386/  (Can't run Robocode 1.9.2.6 on Mac OS)
[Bug-387]: http://sourceforge.net/p/robocode/bugs/387/  (Not printing in console when a bot is force stopped)
[Bug-388]: http://sourceforge.net/p/robocode/bugs/388/  (UI exception in team battles)
[Bug-389]: http://sourceforge.net/p/robocode/bugs/389/  (Third-party team JARs broken with Java 9)
[Bug-390]: http://sourceforge.net/p/robocode/bugs/390/  (Bad CPU constant calculation on Java 9)
[Bug-391]: http://sourceforge.net/p/robocode/bugs/391/  (Fix for "Illegal reflective access")
[Bug-392]: http://sourceforge.net/p/robocode/bugs/392/  (Bullets of the same bot collide at low bullet powers and high gun-cooling rate)
[Bug-393]: http://sourceforge.net/p/robocode/bugs/393/  (More frequent roborumble server checks)
[Bug-394]: http://sourceforge.net/p/robocode/bugs/394/  (HiDPI scaling causes visual glitches)
[Bug-395]: http://sourceforge.net/p/robocode/bugs/395/	(Roborumble client duplicates battle results on network error)
[Bug-397]: http://sourceforge.net/p/robocode/bugs/397/  (Robocode UI cannot remember battle settings upon restart)
[Bug-398]: http://sourceforge.net/p/robocode/bugs/398/  ("Fixed issue with the RobocodeEngine" breaks develope)
[Bug-399]: http://sourceforge.net/p/robocode/bugs/399/  (RANDOMSEED option does not support Java 8)
[Bug-400]: http://sourceforge.net/p/robocode/bugs/400/  (Problem to compile Robot)
[Bug-401]: http://sourceforge.net/p/robocode/bugs/401/  (RoboRumble client: Biased Melee prioritized pairings)
[Bug-402]: http://sourceforge.net/p/robocode/bugs/402/  (roborumble.sh etc. has incorrect class path)
[Bug-403]: http://sourceforge.net/p/robocode/bugs/403/  (MouseEvent returning wrong position)
[Bug-404]: http://sourceforge.net/p/robocode/bugs/404/  (Confusion between development/non-development versions of bots)
[Bug-405]: http://sourceforge.net/p/robocode/bugs/405/	(Fix #405 unnecessary FileNotFound exception)
[Bug-406]: http://sourceforge.net/p/robocode/bugs/405/  (DNS interaction is not blocked by Robocode's security manager)

[Req-1]:   http://sourceforge.net/p/robocode/feature-requests/1/    (Multiple or hyperthreading CPUs (most P4s) hangs Robocode)
[Req-2]:   http://sourceforge.net/p/robocode/feature-requests/2/    (Keep window size of "New battle" window)
[Req-3]:   http://sourceforge.net/p/robocode/feature-requests/3/    (More precise battlefieldsize configuration)
[Req-4]:   http://sourceforge.net/p/robocode/feature-requests/4/    (Prevent API from loading unused features)
[Req-5]:   http://sourceforge.net/p/robocode/feature-requests/5/    (Keyboard Navigation)
[Req-6]:   http://sourceforge.net/p/robocode/feature-requests/6/    (Editor Improvements)
[Req-7]:   http://sourceforge.net/p/robocode/feature-requests/7/    (Graphical "debug" facilities like with RobocodeGL)
[Req-8]:   http://sourceforge.net/p/robocode/feature-requests/8/    (Debug Graphics - SG Option)
[Req-10]:  http://sourceforge.net/p/robocode/feature-requests/10/   (Coloring of bullets and scan arcs)
[Req-11]:  http://sourceforge.net/p/robocode/feature-requests/11/   (Explosion on robots dependent on bullet energy)
[Req-12]:  http://sourceforge.net/p/robocode/feature-requests/12/   (Faster framerates / Turns per second)
[Req-13]:  http://sourceforge.net/p/robocode/feature-requests/13/   (Bullets are sometimes too small)
[Req-14]:  http://sourceforge.net/p/robocode/feature-requests/14/   (Restart Button)
[Req-15]:  http://sourceforge.net/p/robocode/feature-requests/15/   (Optional sound effects)
[Req-16]:  http://sourceforge.net/p/robocode/feature-requests/16/   (Browse button in Development Options)
[Req-17]:  http://sourceforge.net/p/robocode/feature-requests/17/   (New splashscreen picture)
[Req-18]:  http://sourceforge.net/p/robocode/feature-requests/18/   (Better notification about new Robocode versions)
[Req-19]:  http://sourceforge.net/p/robocode/feature-requests/19/   (Possible to install Robocode on Windows Vista)
[Req-20]:  http://sourceforge.net/p/robocode/feature-requests/20/   (Robot templates must be updated regarding deprecated methods)
[Req-21]:  http://sourceforge.net/p/robocode/feature-requests/21/   (Option for accessing external .jars)
[Req-23]:  http://sourceforge.net/p/robocode/feature-requests/23/   (Preference to allow disabling of the scorecard pop-up)
[Req-24]:  http://sourceforge.net/p/robocode/feature-requests/24/   (Recording of battles)
[Req-25]:  http://sourceforge.net/p/robocode/feature-requests/25/   (Save battle results to file)
[Req-26]:  http://sourceforge.net/p/robocode/feature-requests/26/   (Invoke default browser from Help)
[Req-30]:  http://sourceforge.net/p/robocode/feature-requests/30/   (UI Control for adjusting the TPS quickly)
[Req-31]:  http://sourceforge.net/p/robocode/feature-requests/31/   (Redirecting Robot output, running without GUI)
[Req-32]:  http://sourceforge.net/p/robocode/feature-requests/32/   (Bullet size)
[Req-33]:  http://sourceforge.net/p/robocode/feature-requests/33/   (Making robots die quicker (graphically))
[Req-34]:  http://sourceforge.net/p/robocode/feature-requests/34/   (Provide javadoc for robocode.util.Utils and robocode.control)
[Req-35]:  http://sourceforge.net/p/robocode/feature-requests/35/   (Pause Button)
[Req-36]:  http://sourceforge.net/p/robocode/feature-requests/36/   (Initial Placement)
[Req-37]:  http://sourceforge.net/p/robocode/feature-requests/37/   (Running Score Window)
[Req-38]:  http://sourceforge.net/p/robocode/feature-requests/38/   (Codesize)
[Req-39]:  http://sourceforge.net/p/robocode/feature-requests/39/   (Support for RR@Hom)
[Req-44]:  http://sourceforge.net/p/robocode/feature-requests/44/   (Restart tweak)
[Req-45]:  http://sourceforge.net/p/robocode/feature-requests/45/   (Reset compiler cancel button)
[Req-49]:  http://sourceforge.net/p/robocode/feature-requests/49/   (Speedup time required for rebuilding robot database)
[Req-50]:  http://sourceforge.net/p/robocode/feature-requests/50/   (Recalculate CPU Constant)
[Req-52]:  http://sourceforge.net/p/robocode/feature-requests/52/   (The 'New Battle' window sometimes is spamed with .robotcache)
[Req-53]:  http://sourceforge.net/p/robocode/feature-requests/53/   (More control over the event queue)
[Req-54]:  http://sourceforge.net/p/robocode/feature-requests/54/   (All input received from events)
[Req-55]:  http://sourceforge.net/p/robocode/feature-requests/55/   (Exclude Filter for RoboRumble)
[Req-56]:  http://sourceforge.net/p/robocode/feature-requests/56/   (Robots Cache Cleaner)
[Req-57]:  http://sourceforge.net/p/robocode/feature-requests/57/   (Events processed in chronological order)
[Req-58]:  http://sourceforge.net/p/robocode/feature-requests/58/   (HitRobotEvent - damage)
[Req-60]:  http://sourceforge.net/p/robocode/feature-requests/60/   (Enhanced CPU constant calculation)
[Req-62]:  http://sourceforge.net/p/robocode/feature-requests/62/   (Fix for massive turn skipping when cpu constant < granularity)
[Req-63]:  http://sourceforge.net/p/robocode/feature-requests/63/   (Replay should store debug graphics)
[Req-64]:  http://sourceforge.net/p/robocode/feature-requests/64/   (Change default battle settings like e.g. "Number of Rounds")
[Req-65]:  http://sourceforge.net/p/robocode/feature-requests/65/   (Score % display)
[Req-66]:  http://sourceforge.net/p/robocode/feature-requests/66/   ("onBattleIEnd(*)" Event)
[Req-70]:  http://sourceforge.net/p/robocode/feature-requests/70/   (Longer package name allowed)
[Req-74]:  http://sourceforge.net/p/robocode/feature-requests/74/   (Option for enabling/disabling robot timeouts)
[Req-75]:  http://sourceforge.net/p/robocode/feature-requests/75/   (Add client version to POST data sent to server)
[Req-77]:  http://sourceforge.net/p/robocode/feature-requests/77/   (Better "lifebar" display)
[Req-78]:  http://sourceforge.net/p/robocode/feature-requests/78/   (Visual debugging without cpu penalty)
[Req-80]:  http://sourceforge.net/p/robocode/feature-requests/80/   (Screenshot of battleview)
[Req-82]:  http://sourceforge.net/p/robocode/feature-requests/82/   (Launch upload result in separate thread)
[Req-84]:  http://sourceforge.net/p/robocode/feature-requests/84/   (Added append option in copy method (FileTransfer class))
[Req-86]:  http://sourceforge.net/p/robocode/feature-requests/86/   (Rankings should be visible when Robocode is minimized)
[Req-88]:  http://sourceforge.net/p/robocode/feature-requests/88/   (Command Line option for saving a battle record file)
[Req-89]:  http://sourceforge.net/p/robocode/feature-requests/89/   (Launch Robocode from .br (battle record) files)
[Req-90]:  http://sourceforge.net/p/robocode/feature-requests/90/   (Prevent JuniorRobot to be unresponsive)
[Req-92]:  http://sourceforge.net/p/robocode/feature-requests/92/   (Scrollable properties)
[Req-93]:  http://sourceforge.net/p/robocode/feature-requests/93/   (Rename /robots/.robotcache to /robots/.data)
[Req-99]:  http://sourceforge.net/p/robocode/feature-requests/99/   (Move away from Jikes towards ECJ)
[Req-101]: http://sourceforge.net/p/robocode/feature-requests/101/  (onRoundEnded())
[Req-113]: http://sourceforge.net/p/robocode/feature-requests/113/  (Skipped turn events)
[Req-114]: http://sourceforge.net/p/robocode/feature-requests/114/  (RateControlRobot vs. TeamRobot)
[Req-115]: http://sourceforge.net/p/robocode/feature-requests/115/  (Installed package should contain readme file)
[Req-118]: http://sourceforge.net/p/robocode/feature-requests/118/  (Enable/disable development paths)
[Req-119]: http://sourceforge.net/p/robocode/feature-requests/119/  (Provide JuniorRobot template for inexperienced user)
[Req-121]: http://sourceforge.net/p/robocode/feature-requests/121/  (Issues with editor font)
[Req-124]: http://sourceforge.net/p/robocode/feature-requests/124/  (Ability to save the properties file for robots in dev. path)
[Req-128]: http://sourceforge.net/p/robocode/feature-requests/128/  (In battle name hiding)
[Req-129]: http://sourceforge.net/p/robocode/feature-requests/129/  (Rules.getBulletSpeed)
[Req-134]: http://sourceforge.net/p/robocode/feature-requests/134/  (Calculate codesize after compile in editor)
[Req-135]: http://sourceforge.net/p/robocode/feature-requests/135/  (Twin Duel configuration files)
[Req-144]: http://sourceforge.net/p/robocode/feature-requests/144/  (Mac ... start with icon and name)
[Req-147]: http://sourceforge.net/p/robocode/feature-requests/147/  (The snapshot API is ambiguous for bullets shot by teams)
[Req-156]: http://sourceforge.net/p/robocode/feature-requests/156/  (Codesize added to properties file)
[Req-159]: http://sourceforge.net/p/robocode/feature-requests/159/  (Fix overkilled garbage collection on static fields)
[Req-160]: http://sourceforge.net/p/robocode/feature-requests/160/  (Fixed width font in console)
