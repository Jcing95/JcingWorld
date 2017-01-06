package org.jcing.filesystem;

import java.util.HashMap;

public class JHash<O> {

    public static final int MODULATOR = 16;

    private HashMap<Integer, JHash<O>> subMap;
    private O obj;
    //    private JHash<O> superMap;
    //TODO: modulo array or no rehash

    private int iteration;

    public JHash() {
        subMap = new HashMap<Integer, JHash<O>>(MODULATOR + 1, 2.0f);
        iteration = 0;
    }

    public JHash(JHash<O> superMap, int iteration) {
        this.iteration = iteration;
        subMap = new HashMap<Integer, JHash<O>>();
        //        this.superMap = superMap;

    }

    /*
     * 
     * put 123
     * 
     * super = it 0
     * 
     * 123/10 - 12
     * 12 / 10 = 2
     * 
     * 
     * 
     * 
     */

    public void put(O obj, int index) {
        if (index == 0) {
            this.obj = obj;
        } else {
            //            System.out.println(index % MODULATOR);
            if (subMap.get(index % MODULATOR) == null) {
                subMap.put(index % MODULATOR, new JHash<O>(this, iteration + 1));
            }
            subMap.get(index % MODULATOR).put(obj, index / MODULATOR);
        }
    }

    public O get(int index) {
        if (index == 0) {
            return obj;
        } else {
            //            if (subMap.get(index % MODULATOR) == null) {
            //                subMap.put(index % MODULATOR ,new JHash<O>(this, iteration + 1));
            //            }
            if (subMap.get(index % MODULATOR) != null) {
                return subMap.get(index % MODULATOR).get(index / MODULATOR);
            }
            return null;
        }
    }

    public O getObj() {
        return obj;
    }

    public void setObj(O obj) {
        this.obj = obj;
    }

}
