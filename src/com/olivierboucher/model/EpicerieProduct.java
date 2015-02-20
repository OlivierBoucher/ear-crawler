package com.olivierboucher.model;


public abstract class EpicerieProduct extends EpicerieElement {
    protected int id;
    protected String description;
    protected String note;
    protected String size;
    protected String origin;
    protected String thumbnail;

    protected EpicerieCategory category;
    protected EpicerieStore store;
    protected EpicerieRebate rebate;

    public EpicerieCategory getCategory(){
        return this.category;
    }
    public EpicerieStore getStore(){
        return this.store;
    }
    public EpicerieRebate getRebate(){
        return this.rebate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setCategory(EpicerieCategory category) {
        this.category = category;
    }

    public void setStore(EpicerieStore store) {
        this.store = store;
    }

    public void setRebate(EpicerieRebate rebate) {
        this.rebate = rebate;
    }
}
