package sample;
import robocode.*;
import java.io.*;
import java.awt.Color;

/**
 * SittingDuck - a sample robot by Mathew Nelson
 *
 * Along with sitting still doing nothing,
 * this robot demonstrates persistency.
 */
public class SittingDuck extends AdvancedRobot
{
	static boolean incrementedBattles = false;
	
	public void run() {
		
		setColors(Color.yellow,null,null);
		
		int roundCount, battleCount;
		
		// Read file "count.dat" which contains 2 lines,
		// a round count, and a battle count
		try {
			BufferedReader r = new BufferedReader(new FileReader(getDataFile("count.dat")));
			// Try to get the counts
			roundCount = Integer.parseInt(r.readLine());
			battleCount = Integer.parseInt(r.readLine());
		} catch (IOException e) {
			// Something went wrong reading the file, reset to 0.
			roundCount = 0;
			battleCount = 0;
		} catch (NumberFormatException e) {
			// Something went wrong converting to ints, reset to 0
			roundCount = 0;
			battleCount = 0;
		}
		
		// Increment the # of rounds
		roundCount++;
		// If we havenn't incremented # of battles already,
		// (Note:  Because robots are only instantiated once per battle,
		//         member variables remain valid throughout it.
		if (!incrementedBattles)
		{
			// Increment # of battles
			battleCount++;
			incrementedBattles = true;
		}
		
		try {
			PrintStream w = new PrintStream(new RobocodeFileOutputStream(getDataFile("count.dat")));
			w.println(roundCount);
			w.println(battleCount);
			// PrintStreams don't throw IOExceptions during prints,
			// they simply set a flag.... so check it here.
			if (w.checkError())
				out.println("I could not write the count!");
			w.close();
		} catch (IOException e) {
			out.println("IOException trying to write: " + e);
		}

		out.println("I have been a sitting duck for " + roundCount + " rounds, in " + battleCount + " battles.");
		
	}

}																				