package com.olivierboucher.model;

public abstract class EpicerieCategory extends EpicerieElement {
    protected int id;
    protected String name;
    protected String slug;
    private int productCategoryId;
    public EpicerieCategory(){};
    public EpicerieCategory(int id, String name, String slug, int productCategoryId){
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.productCategoryId = productCategoryId;
    }
    public String getName(){
        return this.name;
    }
    public int getId(){
        return this.id;
    }
    public String getSlug() {
        return slug;
    }
    public void setSlug(String slug) {
        this.slug = slug;
    }
    public int getProductCategoryId() {
        return productCategoryId;
    }
    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}
