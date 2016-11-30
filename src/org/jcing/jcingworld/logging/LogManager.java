package org.jcing.jcingworld.logging;

import java.io.PrintStream;

import org.jcing.logstream.LogStream;

public class LogManager {
	public static final LogStream logStream = new LogStream();
	
	public static PrintStream DisplayLog = logStream.createStream("Display");
	public static PrintStream MainGameLog = logStream.createStream("MainGame");
	public static PrintStream MainEngine = logStream.createStream("MainEngine");
	public static PrintStream Loader = logStream.createStream("Loader");
	
	public static PrintStream EngineShading = logStream.createStream("EngineShading", MainEngine);
	public static PrintStream TextureLoader = logStream.createStream("EngineShading", Loader);
	
	
}
