package org.jcing.jcingworld.editor.test;

import java.util.HashMap;

public class Coordinator<O> {
    
    O obj;
    
    HashMap<Integer, Coordinator<O>> subX;
    HashMap<Integer, Coordinator<O>> subY;
    
    final int BASE;
    
    public Coordinator(){
        BASE = 10;
    }
    
    public void init(){
        subX = new HashMap<Integer, Coordinator<O>>(BASE+1, 1);
        subY = new HashMap<Integer, Coordinator<O>>(BASE+1, 1);
        
    }
    
    public void insert(int x, int y){
        if(subX.containsKey(x)){
           subX.put(x, arg1); 
        }else if(subY.containsKey(y)){
            
        }
       
        
    }
    
    
}
