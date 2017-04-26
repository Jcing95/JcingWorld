package org.jcing.jcingworld.terrain;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class TinyTile implements Externalizable {
    private byte x, y;
    
    private Chunk ch;
    
    public TinyTile(){
        
    }
    
    public void generate(Chunk ch){
        
    }
    
    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        // TODO Auto-generated method stub
        
    }
    
}
