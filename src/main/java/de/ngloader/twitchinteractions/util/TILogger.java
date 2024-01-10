package de.ngloader.twitchinteractions.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TILogger {

	public static Logger logger = Logger.getLogger("TwitchInteractions");

	private static boolean verbose = false;

	public static void setVerbose(boolean verbose) {
		TILogger.verbose = verbose;
	}

	public static void debug(String message) {
		if (TILogger.verbose) {
			TILogger.logger.log(Level.FINE, "[Debug] " + message);
		}
	}

	public static void info(String message) {
		TILogger.logger.log(Level.INFO, message);
	}

	public static void warn(String message) {
		TILogger.logger.log(Level.WARNING, message);
	}

	public static void error(String message, Throwable throwable) {
		TILogger.logger.log(Level.SEVERE, message, throwable);
	}
}