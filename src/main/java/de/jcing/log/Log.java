package de.jcing.log;

import java.io.PrintStream;
import java.util.HashMap;

public class Log {

	public static final int LOG_DEBUG = 100;
	public static final int LOG_INFO = 200;
	public static final int LOG_WARNING = 300;
	public static final int LOG_ERROR = 300;

	public static final int TO_CONSOLE = 10;
	public static final int TO_FILE = 20;
	
	
	// TODO: implement Filelogging

	public static final int DEFAULT_LOG = TO_CONSOLE;

	private static PrintStream outStream = System.out;
	private static PrintStream errorStream = System.err;

	private static HashMap<Class<?>, Log> logs = new HashMap<Class<?>, Log>();

	private static int logLvl = 0;

	private static int logType = DEFAULT_LOG;

	public static Log getLog(Class<?> fromClass) {
		if (!logs.containsKey(fromClass))
			logs.put(fromClass, new Log(fromClass));
		return logs.get(fromClass);
	}  

	public Log getLog(String fromName) {
		for (Class<?> c : logs.keySet()) {
			if (logs.get(c).name.equals(fromName))
				return logs.get(c);
		}
		return logs.put(String.class, new Log(fromName));
	}

	private static void print(String message, boolean error) {
		switch (logType) {
		case TO_CONSOLE:
			if (error)
				errorStream.println(message);
			else
				outStream.println(message);
		}
	}

	public static void setLogLvl(int lvl) {
		logLvl = lvl;
	}

	public static void log(String message, int LOG_LEVEL) {
		if (LOG_LEVEL >= logLvl) {
			print(message,LOG_LEVEL >= LOG_ERROR);
		}
	}

	private Class<?> id;
	private String name;

	private Log(Class<?> identifier) {
		this.id = identifier;
		name = id.getName();
	}

	private Log(String name) {
		this.id = String.class;
		this.name = name;
	}
	
	public void debug(String message) {
		log(message, LOG_DEBUG);
	}

	public void info(String message) {
		log(message, LOG_INFO);
	}

	public void warn(String message) {
		log(message, LOG_WARNING);
	}
	public void error(String message) {
		log(message, LOG_ERROR);
	}

	public static PrintStream getErrorSteam() {
		return errorStream;
	}
	
	public static PrintStream getPrintStream() {
		return outStream;
	}

}
