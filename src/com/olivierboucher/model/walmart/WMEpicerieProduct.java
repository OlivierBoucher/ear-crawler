package com.olivierboucher.model.walmart;

import com.olivierboucher.model.EpicerieProduct;


public class WMEpicerieProduct extends EpicerieProduct {
    public WMEpicerieProduct(){}

    @Override
    public WMEpicerieRebate getRebate(){
        return (WMEpicerieRebate)this.rebate;
    }
    @Override
    public WMEpicerieCategory getCategory(){return (WMEpicerieCategory)this.category;}
}
