package com.olivierboucher.model.supermarches;

import com.olivierboucher.model.EpicerieProduct;

public class SMEpicerieProduct extends EpicerieProduct {

    public SMEpicerieProduct(){

    }
    @Override
    public SMEpicerieRebate getRebate(){
        return (SMEpicerieRebate)this.getRebate();
    }
}
