package org.jcing.jcingworld.terrain.construction;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;

public class TCollumn implements Externalizable{
	
	
	public static final int SIZE = 4;
	private int x, y;
	
	private LinkedList<Integer> vPlanes;
	
	public TCollumn(){
//		vPlanes.sort();
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
