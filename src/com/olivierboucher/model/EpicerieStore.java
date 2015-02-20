package com.olivierboucher.model;

public abstract class EpicerieStore extends EpicerieElement {
    protected int id;
    protected String name;
    private String slug;
    public EpicerieStore(){};
    public EpicerieStore(int id, String name, String slug){
        this.id = id;
        this.name = name;
        this.setSlug(slug);
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
}
