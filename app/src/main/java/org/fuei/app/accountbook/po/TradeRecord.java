package org.fuei.app.accountbook.po;

/**
 * Created by fuei on 2016/7/11.
 */
public class TradeRecord {
    private int id;
    private int vegetableId;
    private String vegetableName;
    private int customerId;
    private float gross_weight;
    private float net_weight;
    private int white_frame_count;
    private int green_frame_count;
    private float frame_weight;
    private float unit_price;
    private float sum_price;
    private int data_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVegetableId() {
        return vegetableId;
    }

    public void setVegetableId(int vegetableId) {
        this.vegetableId = vegetableId;
    }

    public String getVegetableName() {
        return vegetableName;
    }

    public void setVegetableName(String vegetableName) {
        this.vegetableName = vegetableName;
    }

    public float getUnitPrice() {
        return unit_price;
    }

    public void setUnitPrice(float unit_price) {
        this.unit_price = unit_price;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public float getGrossWeight() {
        return gross_weight;
    }

    public void setGrossWeight(float gross_weight) {
        this.gross_weight = gross_weight;
    }

    public float getNetWeight() {
        return net_weight;
    }

    public void setNetWeight(float net_weight) {
        this.net_weight = net_weight;
    }

    public int getWhiteFrameCount() {
        return white_frame_count;
    }

    public void setWhiteFrameCount(int white_frame_count) {
        this.white_frame_count = white_frame_count;
    }

    public float getFrameWeight() {
        return frame_weight;
    }

    public void setFrameWeight(float frame_weight) {
        this.frame_weight = frame_weight;
    }

    public float getSumPrice() {
        return sum_price;
    }

    public void setSumPrice(float sum_price) {
        this.sum_price = sum_price;
    }

    public int getDataDate() {
        return data_date;
    }

    public void setDataDate(int data_date) {
        this.data_date = data_date;
    }

    public int getGreenFrameCount() {
        return green_frame_count;
    }

    public void setGreenFrameCount(int green_frame_count) {
        this.green_frame_count = green_frame_count;
    }

    @Override
    public String toString() {
        return this.vegetableName;
    }
}
