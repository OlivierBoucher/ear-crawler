package com.olivierboucher.model.supermarches;

import com.olivierboucher.model.EpicerieCategory;

public class SMEpicerieCategory extends EpicerieCategory {
    public SMEpicerieCategory(int id, String name, String slug, int productCategoryId){
        super(id,name, slug, productCategoryId);
    }
}
