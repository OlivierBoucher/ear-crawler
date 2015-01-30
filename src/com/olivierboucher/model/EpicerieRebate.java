package com.olivierboucher.model;

import java.util.Date;

/**
 * Created by olivier on 1/30/15.
 */
public abstract class EpicerieRebate extends EpicerieElement {
    private double price;
    private double rebate;
    private int rebate_percent;
    private Date start;
    private Date end;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRebate() {
        return rebate;
    }

    public void setRebate(double rebate) {
        this.rebate = rebate;
    }

    public int getRebate_percent() {
        return rebate_percent;
    }

    public void setRebate_percent(int rebate_percent) {
        this.rebate_percent = rebate_percent;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
