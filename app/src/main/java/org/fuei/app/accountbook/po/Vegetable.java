package org.fuei.app.accountbook.po;

import org.fuei.app.accountbook.util.VariableUtils;

/**
 * Created by fuei on 2016/7/16.
 */
public class Vegetable {
    private int id;
    private String name;
    private float unit_price;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getUnitPrice() {
        return unit_price;
    }

    public void setUnitPrice(float unit_price) {
        this.unit_price = VariableUtils.SaveOneNum(unit_price);
    }
}
