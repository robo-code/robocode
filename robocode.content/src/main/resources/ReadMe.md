## ReadMe for Robocode

Updated 29-Jul-2021 by Flemming N. Larsen

[Robocode Home Page](https://robocode.sourceforge.io/)

### TABLE OF CONTENTS

1. [What is Robocode?](#what-is-robocode)
2. [History of Robocode](#history-of-robocode)
3. [System Requirements](#system-requirements)
4. [Getting Started](#getting-started)
5. [Robocode API](#robocode-api)
6. [Robocode Repository](#robocode-repository)
7. [Community](#community)
8. [Challenges](#challenges)
9. [Enter the Competition](#enter-the-competition)
10. [Command Line](#command-line)
11. [Reporting Issues / Bugs](#reporting-issues--bugs)
12. [Feature Requests](#feature-requests)
13. [News](#news)
14. [How to contribute](#how-to-contribute)

### WHAT IS ROBOCODE?

Robocode is a programming game where the goal is to code a robot battle tank to compete against other robots in a battle
arena. So the name Robocode is short for "Robot code". The player is the programmer of the robot, who will have no
direct influence on the game. Instead, the player must write the AI of the robot telling it how to behave and react to
events occurring in the battle arena. Battles are running in real-time and on-screen.

The motto of Robocode is: *Build the best, destroy the rest!*

Beside being a programming game, Robocode is used for learning how to program primarily in the Java language, but other
languages like Kotlin and Scala is possible as well.

Schools and universities are using Robocode as part of teaching how to program, but also for studying artificial
intelligence (AI). The concept of Robocode is easy to understand, and a fun way to learn how
to program.

Robocode offers a complete development environment and comes with an installer, built-in robot editor and Java compiler.
Robocode only requires that a JRE (Java Runtime Environment) exist already on the system where Robocode is installed.
Hence, everything a Robocode developer (Robocoder) needs to get started is provided with the main Robocode distribution
file (`robocode-xxx-setup.jar`). Robocode also supports developing robots using external IDEs like e.g.

- [IntelliJ IDEA](https://www.jetbrains.com/idea/)
- [Eclipse](https://www.eclipse.org/downloads/)
- [NetBeans](https://netbeans.apache.org/),
- [Visual Studio Code](https://code.visualstudio.com/)

An external IDE aids the developer much better than the build-in robot editor in Robocode, which is just meant for
making quick small fixes.

The fact that Robocode runs on the Java platform makes it possible to run it on most operating systems supporting Java,
meaning that it will be able to run on Windows, Linux, macOS, and other UNIX variants. Note that Java 8 is
the minimum version required by Robocode, but you can use newer versions for running Robocode and when developing bots.
See the [System Requirements](#system-requirements) for more information.

Be aware that many users of Robocode (aka Robocoders) find Robocode to be very fun, but also very addictive. :-)

Robocode comes free of charge and is being developed as a leisure project where no money is involved. The developers of
Robocode are developing on Robocode because they think it is fun to do.

Robocode is an [Open Source](https://en.wikipedia.org/wiki/Open_source) project, which means that all sources are open
to everybody.

Robocode is provided under the terms of [EPL](https://www.eclipse.org/legal/epl-v10.html) (Eclipse Public License).

### HISTORY OF ROBOCODE

The Robocode game was originally started by Mathew A. Nelson as a personal endeavour in late 2000 and became a
professional one when he brought it to his job at IBM, in the form of an AlphaWorks download, in July 2001.

IBM was interested in Robocode, as they saw an opportunity to promote Robocode as a fun way to get started with learning
how to program in Java, and IBM wanted to promote Java as well.

The inspiration for creating Robocode came from [Robot Battle](https://en.wikipedia.org/wiki/Robot_Battle), a
programming game written by Brad Schick in 1994. Robot Battle was, in turn, inspired by
[RobotWar](https://en.wikipedia.org/wiki/RobotWar), an Apple II+ game from the early 1980s.

Articles from IBM ("Rock 'em, sock 'em Robocode", "Robocode Rumble", and "Secrets from the Robocode masters"), and the
Robocode community behind the RoboWiki made Robocode very popular as a programming game, and for many years Robocode has
been used for education and research at schools and universities all over the world.

At the beginning of 2005, Mathew convinced IBM to release Robocode as
[Open Source](https://en.wikipedia.org/wiki/Open_source) on SourceForge. At this point, the development of Robocode had
somewhat stopped. The community around Robocode began to develop their own versions of Robocode with bug fixes and new
features, e.g. the "Contributions for Open Source Robocode" and later on the two projects, RobocodeNG and Robocode 2006,
by Flemming N. Larsen.

Eventually, Flemming took over for Mathew on the Robocode project at SourceForge as administrator and developer in July
2006 to continue development on the original Robocode game. Hence, the RobocodeNG project was dropped, and Robocode 2006
was merged into the new official Robocode version 1.1 of the game containing lots of improvements. Since then, lots of
new versions of Robocode have been released with more and more features and contributions from the community.

In May 2007, the [RoboRumble](https://robowiki.net/wiki/RoboRumble) client got built into Robocode. RoboRumble is widely
used by the Robocode community for creating up-to-date robot ranking lists for the 1-vs-1, Melee, Team, and Twin Dual
competitions.

In 2012, the Robocode called Julian ("Skilgannon") created [LiteRumble](https://robowiki.net/wiki/LiteRumble), which is
intended to be a lightweight, easily deployable RoboRumble system designed to run on the Google App Engine.

In May 2010 a **.Net plugin** was provided for Robocode using a .NET / Java bridge that made it possible to develop
robots for the [.Net Framework](https://dotnet.microsoft.com/download/dotnet-framework/) version 3.5 beside developing
robots in Java. This feature was provided by Pavel Savara, who is a major Robocode contributor.

In April 2021 the .Net plugin was discontinued as the .Net Framework and the required toolchain for building both the
plugin, and the documentation files for it had got some serious issues that made it very difficult to build and maintain.

### SYSTEM REQUIREMENTS

To run Robocode, Java 8 or a newer version must be installed on your system. Both the Java Runtime Environment (JRE) and
the Java Developer Kit (JDK) can be used. Note that the JRE does not include the standard Java compiler (javac) which
comes with the JDK. However, Robocode comes with a built-in compiler ECJ (Eclipse Compiler for Java).
Hence, it is sufficient to run Robocode with the JRE only.

Also, note that it is important that these environment variables have been set up before running Robocode:

- **JAVA_HOME** must be set up to point at the home directory for Java
  (JDK or JRE).  
  Windows example: `JAVA_HOME=C:\Program Files\AdoptOpenJDK\jdk-16.0.0.36-hotspot`  
  Linux and macOS example: `JAVA_HOME=/usr/lib/jvm/adoptopenjdk-16-hotspot-amd64`

- **PATH** must include the path to the `bin` of the Java home
  directory (`JAVA_HOME`) that includes the `java` executable for starting the Java Virtual Machine (JVM).  
  Windows example: `PATH=%PATH%;%JAVA_HOME%`  
  Linux, macOS example: `PATH=${PATH}:${JAVA_HOME}/bin`

You can read more details from here:

- [System Requirements](https://robowiki.net/wiki/Robocode/System_Requirements)

### GETTING STARTED

Most documentation about Robocode is provided thru the [RoboWiki](https://robowiki.net/) which is hosted by Julian
("Skilgannon"). The RoboWiki is an amazing source of information about Robocode development used by most Robocoders,
and it also contains lots of official documentation for Robocode.

It is recommended to read the articles on the [RoboWiki](https://robowiki.net/) for getting started with Robocode.
The first article to start with is provided here:

- [Getting Started](https://robowiki.net/wiki/Robocode/Getting_Started)

Make sure to should read about the anatomy of a robot, the game physics, scoring etc.

### ROBOCODE API

The Robocode API is provided here:

- [Java Robot API](https://robocode.sourceforge.io/docs/robocode/)
- [Java Control API](https://robocode.sourceforge.io/docs/robocode/index.html?robocode/control/package-summary.htm)

The Robocode API consists of several APIs.

- **Robot API**: Within the Java package `robocode`.
  The Robot API is used for developing robots and is the only part of the API that robots are allowed to access.

- **Robot Interfaces**: Within the Java package `robocode.robotinterfaces`.
  The Robot Interfaces are used for developing new robot types with a different API than the standard Robot API.
  **Note:** The game rules and robot behaviours cannot be changed.

- **Control API**: Within the Java package `robocode.control`.
  The Control API is used for letting another application startup battles with selected robots in Robocode and retrieve
  the results. It is also possible to get snapshots of robots and bullets (like position, heading, energy level etc.) at
  a specific time in a battle.

### ROBOCODE REPOSITORY

If you want to try out new robots other than the sample robots that come with Robocode, you should visit the
[LiteRumble home](https://literumble.appspot.com/) that contains lots of bots.

### COMMUNITY

The community around Robocode is using the [RoboWiki](https://robowiki.net/) as a communication channel. At the
RoboWiki, people share new ideas, code snippets, algorithms, strategies, and lots of other stuff about Robocode. New
official documentation from the developers of Robocode will be put at the RoboWiki as well.

On the [RoboWiki](https://robowiki.net/), these strategies are provided:

- [Radar](https://robowiki.net/wiki/Radarr)
- [Movement](https://robowiki.net/wiki/Category:Movement)
- [Targeting](https://robowiki.net/wiki/Category:Targeting)

The code snippets are also provided on the RoboWiki:

- [Code Snippets](https://robowiki.net/wiki/Category:Code_Snippets)

You can also get in touch with other Robocoders on:

- The [Robocode](https://groups.google.com/g/robocode) group at Google.
- The [Facebook](https://www.facebook.com/groups/129627130234) group.

### CHALLENGES

A good way to improve yourself as a robot developer is to try out some real challenges. On the
[RoboWiki](https://robowiki.net/), two famous challenges exist for testing/studying a robot´s movement, targeting, and
gun abilities:

- [Movement Challenges](https://robowiki.net/wiki/Category:Movement_Challenges)
- [Targeting Challenges](https://robowiki.net/wiki/Category:Targeting_Challenges)
- [RoboRumble Gun Challenge](https://robowiki.net/wiki/RoboRumble_Gun_Challenge)

But there is a lot of other challenges available on RoboWiki besides the ones listed here.

### ENTER THE COMPETITION

If you want to challenge your robot(s) and yourself as Robocoder, the [LiteRumble](https://robowiki.net/wiki/LiteRumble)
is the best way to do it. LiteRumble is the ultimate collaborative effort to have a live,
[up-to-date ranking](https://literumble.appspot.com/) of Robocode bots.

So don't hesitate to [enter the RoboRumble competition](https://robowiki.net/wiki/RoboRumble/Enter_The_Competition).

### COMMAND LINE

It is possible to specify options and predefined properties from the command-line when running Robocode. The usage of
these can be listed by writing this from a command prompt or shell:

    robocode -help

For example, it is possible to:

- Disable the graphical user interface (GUI).
- Disable security that is specific to Robocode (but does not override the security that comes with the JVM).
- Enable/disable the debugging mode, useful when debugging robots.
- Play a battle based on an existing Robocode .battle file.
- Replay a recorded battle visually.
- Save the results of battles in a comma-separated file.

You can read more details here:

- [Console Usage](https://robowiki.net/w/index.php?title=Robocode/Console_Usage)

### REPORTING ISSUES / BUGS

If you discover an issue with Robocode you are encouraged to report it as soon as you discover it. The sooner the better.

A bug report should be reported on the [Bugs](https://sourceforge.net/p/robocode/bugs/) page on the SourceForge site for
Robocode. Each bug report will be prioritized among other bug reports depending on its impact on the game.

It will be a great help if you describe the necessary steps to make to reproduce the issue. You are very welcome to
provide a screenshot, source code or anything else that will show the bug. It is also a very good idea to write which OS
and version of both Robocode and Java you are using.

If you are a registered user at SourceForge (register [here](https://sourceforge.net/user/registration) you will be able
to add a "monitor" to your bug report. This way you will be able to receive notifications when someone adds comments to
your report, but will also be able to better track the current status of the bug, e.g. when the bug is fixed and with
which version of Robocode the fix is available.

If you are a developer yourself and have a good idea of how the bug can be fixed, you are more than welcome to do so by
providing a [pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests)
at [GitHub](https://github.com/robo-code/robocode). By fixing the bug, you will become a contributor to Robocode
yourself. You can learn more about how to contribute [here](#how-to-contribute). Note that we accept bug fixes under the
terms of [EPL](https://www.eclipse.org/legal/epl-v10.html).

### FEATURE REQUESTS

If you got an idea for a new feature or improvement for Robocode, you are very welcome to share your idea by summiting a
feature request or start a discussion on the
[Robocode Application Developers](https://groups.google.com/g/robocode-developers) group.

A feature request should be put on the [Feature Requests](https://sourceforge.net/p/robocode/feature-requests/) on the
SourceForge site for Robocode. Each feature request will be prioritized among other feature requests.

Note that if the feature is a big change to the game, e.g. change the robot behaviour, it might not be accepted, as
Robocode is being used for competitions like e.g. the [LiteRumble](https://literumble.appspot.com/).

It will be a great help if you describe your idea in detail, and how you think it could be implemented into Robocode.
For example, will it be possible to extend an existing feature with your idea?

If you are a registered user at SourceForge (register [here](https://sourceforge.net/user/registration) you will be able
to add a "monitor" to your request. This way you will be able to receive notifications when someone adds comments to
your request entry, but will also be able to better track the current status of your entry, e.g. when the feature has
been implemented and with which version of Robocode it will be available.

If you are a developer yourself and have a good idea of how the feature could be implemented, you are more than welcome
to do so if the feature is being accepted. By implementing the feature, you will become a contributor to Robocode
yourself. You can learn more about how to contribute [here](#how-to-contribute). Note that we accept implementations
under the terms of [EPL](https://www.eclipse.org/legal/epl-v10.html).

### NEWS

News about Robocode is put on the blog spot for Robocode. Here it is possible to subscribe to an RSS feed to receive
news about Robocode.

- [Robocode News](https://robo-code.blogspot.com/)

You can also follow Robocode on Twitter and Facebook here:

- [Twitter for Robocode](https://twitter.com/robocode)
- [Robocode on Facebook](https://www.facebook.com/group.php?gid=129627130234)

The RoboWiki can be followed on Twitter as well:

- [Twitter for RoboRumble](https://twitter.com/robowiki)

### HOW TO CONTRIBUTE

If you want to contribute to Robocode with e.g. a new feature or bug fix, you should start by reading the
[Developers Guide for building Robocode](https://robowiki.net/wiki/Robocode/Developers_Guide_for_building_Robocode).

Note that we accept code changes under the terms of [EPL](https://www.eclipse.org/legal/epl-v10.html).

There exist no or little documentation about the internals of Robocode, and the codebase will need to be examined as a
contributor to get an insight into how Robocode is implemented. Thus, it required a skilled Java developer to figure out
how Robocode is put together.

Robocode is divided into several modules. You can read Pavel Savara's blog post to get a good overview of Robocode here:

- [Robocode modules](https://zamboch.blogspot.com/2009/06/robocode-modules-as-in-version-17.html)

Help for Robocode internals can be provided through the
[Robocode Application Developers](https://groups.google.com/g/robocode-developers) where you can register yourself, and
start up a new topic. This is the best way of getting information and asking about details for the internals in Robocode.

If a contribution is a somewhat small change to involves under 10 files, then the preferred way is to do a
[pull request](https://docs.github.com/en/github/collaborating-with-issues-and-pull-requests/about-pull-requests) at
[GitHub](https://github.com/robo-code/robocode).

Your pull request will be reviewed and tested out before being accepted and merged into Robocode. Also, note that
additional work might be done by other Robocode developers to finalize the work or make some adjustments.
