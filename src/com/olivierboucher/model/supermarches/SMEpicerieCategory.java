package com.olivierboucher.model.supermarches;

import com.olivierboucher.model.EpicerieCategory;

/**
 * Created by olivier on 1/30/15.
 */
public class SMEpicerieCategory extends EpicerieCategory {
    private int id;

    public SMEpicerieCategory(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId(){
        return this.id;
    }
}
