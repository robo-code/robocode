package net.sf.robocode.io;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public final class RobocodeLogManager {

	public static Level setLevel(Level level) {
		Level originalLevel = Logger.getLogger("robocode").getLevel();

		Logger.getLogger("robocode").setLevel(level);
		Logger.getLogger("net.sf.robocode").setLevel(level);

		return originalLevel;
	}
}
