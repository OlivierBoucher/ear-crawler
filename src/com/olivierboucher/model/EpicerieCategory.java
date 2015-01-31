package com.olivierboucher.model;

/**
 * Created by olivier on 1/30/15.
 */
public abstract class EpicerieCategory extends EpicerieElement {
    protected int id;
    protected String name;
    public EpicerieCategory(){};
    public EpicerieCategory(int id, String name){
        this.id = id;
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public int getId(){
        return this.id;
    }
}
