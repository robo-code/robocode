"Virtual Combat" is based on the open source programming game "Robocode". 
(http://robocode.sourceforge.net/)

================================================================================
Virtual Combat Installation Instructions
================================================================================
1.	Audience
	Individuals who wish to install Virtual Combat on their Windows/Linux/MacOSX 
	systems.
		
2.	Purpose
	The application is used as the environment to develop robots for 
	participation in CodeFest. The set up is provided solely as a 
	"platform to devlop robots". The application has modules for providing 
	offline API documentation, battle viewing, battle recording, playing battle 
	from old records	and has a Robot Editor that is used to develop robots. 
	Virtual Combat is based on the open source programming game "Robocode" and 
	has new exciting features.
		
3.	Prerequisites
	It is recommended to have the following versions of softwares. Please 
	upgrade/install if your machine is missing any of these:
		
	Hardware: Recommended to have at least 512 MB RAM. Having a processor with 
	at least 2 cores will improve performance significantly. 
	Operating System: Windows XP, Windows Vista, Windows 7, Linux (tested for 
	Fedora, Ubuntu), Mac OSX.
		
	Web Browser: It is recommended to have current version of firefox
	(http://www.mozilla.com/en-US/firefox/), IE 8 or 
	Google Chrome(http://www.google.com/chrome).
		
	JAVA: Install J2SE. Installation instructions can be found at SUN or you can
	read the following instructions to install JAVA.
			
	3.1 Download JAVA (JDK and JRE)
		Download the latest JDK from here:
		----------------------------------
	
		https://cds.sun.com/is-bin/INTERSHOP.enfinity/WFS/CDS-CDS_Developer-Site/en_US/-/USD/ViewProductDetail-Start?ProductRef=jdk-6u23-oth-JPR@CDS-CDS_Developer
	
		Download the latest JRE from here:
		----------------------------------
		http://java.com/en/download/index.jsp
	
		For linux based systems, you can install the following packages through 
		command line as follows:
	
		++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		$ apt-get install sun-java6-jdk sun-java6-jre
		++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		It is recommended to have java 5.0 or higher. To check the version of java 
		in your system, type this in your terminal:
	
		++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		$ java -version
		++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
4.	Install Virtual Combat
	4.1	Download the set up either as .jar file or in zipped format from 
		http://itbhu.ac.in/codefest/event.php?name=virtual%20combat .
			
	4.2	From virtual-combat-setup.jar
		Run the jar as  an executable. For windows as well as linux users :
		
		Right Click > Open With > Sun Java Runtime Environment(or Sun JRE)
		Install it at desired location. You can choose the default option.
			
		From zipped file: Extract the zip. The created folder is your 
		installation folder.
	
5. Running Virtual Combat
	Windows: Starting is as simple as double clicking the icon on your Desktop. 
	Optionally you can browse to the installation folder and start the	
	application from there.
	
	Linux: To run the application, use command line as follows.
	
	Browse to your installation directory. Suppose the installation directory is
	in your home directory,say, at "~/virtual-combat/". Then, the application 
	can be started from shell as:
		
	$ ~/virtual-combat/virtual-combat.sh

================================================================================
Virtual Combat Configuration Instructions
================================================================================

You can configure the application with following options:

6.	Run the application as original "Robocode" application. In this mode, there 
	will be no flag, obstacle, base or team.
	
	Instructions
	============
	
	6.1 Open the <installation-dir>/config/battle.properties file in text editor.
	
	6.2 Comment all the three lines required to run the application in 
		"Virtual Combat" format by placing a '#' at start of each line.
	
	6.3 Uncomment all the three lines required to run the application in 
		"Robocode" format by removing the '#' at start of each line.
			
	The new battle.properties file would now look something like this.
	
	++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	MAPSDIR=./maps/

	# To run the application in VIRTUAL COMBAT format, uncomment the following three lines.
	#BATTLEMAP=Map2.xml
	#GAMEMODE=CaptureTheFlag
	#BATTLE_LOGO=VIRTUALCOMBAT


	# To run the application in ROBOCODE original format, uncomment the following three lines.
	BATTLEMAP=Map1.xml
	GAMEMODE=NORMALMODE
	BATTLE_LOGO=ROBOCODE
	++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
7. Change the map for arena.
	Note: Maps can be changed while running in "Virtual Combat" format only. If 
		  you are running in "Robocode" format, the default map "Map1.xml" is 
		  the only option. (To see how to run in Robocode mode, read above.)
	
	Instructions
	============
	
	7.1 There are several maps provided in the folder <installation-dir>/maps/. 
		You can take a look at them !
	7.2 To change the map to be used for battle:
	
		Open the <installation-dir>/config/battle.properties file in text editor.
		
		Change the property BATTLEMAP to map name. The map name must be exactly 
		same as a map in <installation-dir>/maps/   directory.
		
		Example:
		
		BATTLEMAP=Map4.xml
		
	7.3 Save and exit.

================================================================================
Virtual Combat Development Instructions
================================================================================

8. How to create, save and compile a robot. Get it ready for battle.

	8.1 Start the application.
	8.2 Goto Robot > Robot Editor.
	8.3 The Robot Editor window will appear.
		Goto File > New > Robot
	8.4 Enter your bot_name. (eg. Hercules)
	8.5 Next you will be prompted to supply a package name. The package name 
		serves as the parent of several bots to be placed under common name. THE
		PACKAGE	NAME MUST BE THE SAME AS YOUR TEAM-NAME FOR VIRTUAL COMBAT.
	8.6 A default robot appears. Code to train your robot for battle !!
	8.7 A default functional code has been provided for the robot.
	8.7 Save the robot. File > Save
		Choose the default option.
	8.8 Compile the robot.
		Goto Compiler > Compile.
	8.9 Your robot is all set for battle.

9. How to create a team of robots.
	
	9.1 Goto Robot > Create a robot team
	9.2 Select the bots you want to place in the team. Click Next.
	9.3 Enter Team-name and a short description too.
	9.4 Click Create Team.
	9.5 Your team is ready.

10. How to start a battle.

	10.1 Start the application.
	10.2 Goto Battle > New
	10.3 Select two teams for battle.
	10.4 Specify the number of rounds too. The default is 5.
	10.5 Click Next.
	10.6 Specify the Battle Field size. Note that an arena size of 800 X 600 will 
		be used for rank calculations.
	10.7 Click Start Battle.
	10.8 Enjoy the battle !!
	10.9 Observe the slider at the bottom of the window to increase or decrease 
		the pace of battle.
	
11. How to save the record of a battle.

	11.1 A battle record can be saved for display later after the battle is over.
	11.2 Goto Battle > Save Record
	11.3 Choose the destination folder for saving. The default is 
		<installation-dir>/battles/
	11.4 Specify a file name and save it.
	
	11.5 To open a battle record:
	11.6 Goto File > Open Record
	11.7 Select the record (will be a file with .br extension)
	11.8 Enjoy the battle !!

12. How to package a team for submission.

	12.1 First, ensure that the package name for all the robots is the same as 
		your registered team name for Virtual Combat.
	12.2 Goto Robot > Package robot for upload
	12.3 Select the team you want to package. A maximum of 4 robots are allowed 
		for a team to take part in battle. If there are more than 4 bots, then 
		the first 4 bots in the list shall be considered for battle and team's 
		ranking	will be decided based on that. So choose your robots cautiously 
		for packaging.
	12.4 Click Next.
	12.5 Check the "include source" box.
	12.6 Enter a version number and a short description for the package. Other 
		fields are optional.
	12.7 Click Next.
	12.8 Browse to destination folder where you want to save the package 
		(in .jar format).
	12.9 Save the jar with filename as <teamname>.<teamname>.jar
	
		Example: For 'teamcodefest' the packaged file is to be named as :
					teamcodefest.teamcodefest.jar
					
	12.10 This file needs to be uploaded to CodeFest at 
			http://www.itbhu.ac.in/codefest/event.php?name=virtual%20combat
	12.11 All submissions are to be done through the website portal only.
	
================================================================================
Virtual Combat Development : FAQs
================================================================================

Q: 	Where to get the documentation for the API ?
A:	Start the application. Goto Help > Robocode API.

Q:	Can I select more than 2 teams for battle ?
A:	No, you can't. This is because, in Virtual Combat mode, there are only two 
	flags and two bases. Hence, only two teams.

Q:	I did not form any teams, yet the battle ran without any warning or error. 
	How ?
A:	The battle runs fine but the final result only displays Rank1 and Rank 2. 
	This rank is decided based on individual scores of robots.

Q:	Why am I not able to submit my code?
A:	Please notice that submissions are allowed only with minimum interval of 
	15 minutes for a team. Also check that your team member has agreed to be 
	part of your team(A notification was sent at the time of team registration 
	to the other member). If it has been more than 15 minutes from your last 
	submission and you still can't submit your code please post here.

Q: 	What happens if I challenge a team ranked lower than me ?
A: 	What does your logic say !!?? :) The winner team will have a better ranking.

Q: 	When I updated my code, my rank immediately came down to last rank.. !!
A:	An update by you causes an automated match to take place with Team Codefest.
	An error occurred during the macth that resulted into your drop to last rank. 
	Please check again the code submitted by you and then retry.

Q: 	Can I use other languages that run on JVM ?
A:	No.