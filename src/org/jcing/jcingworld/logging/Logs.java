package org.jcing.jcingworld.logging;

import java.io.PrintStream;

import org.jcing.logstream.PrintStreamManager;

public final class Logs {

    private Logs(){
    }
    
    public static final PrintStreamManager logs = new PrintStreamManager();
    
    
    public static PrintStream display = logs.create("Display");
    
    public static PrintStream engine = logs.create("Engine");
        public static PrintStream loader = logs.create("Loader", engine);
            public static PrintStream textureLoader = logs.create("tex",loader,true);
                public static PrintStream atlas = logs.create("atlas", textureLoader);
                
            public static PrintStream objLoader = logs.create("obj", loader, false);
    
            
   public static PrintStream game = logs.create("Game");
       public static PrintStream terrain = logs.create("terrain", game);
    
    
}
