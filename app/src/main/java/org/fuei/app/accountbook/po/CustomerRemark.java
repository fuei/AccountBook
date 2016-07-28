package org.fuei.app.accountbook.po;

import org.fuei.app.accountbook.util.VariableUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by fuei on 2016/7/17.
 */
public class CustomerRemark {
    private int id;
    private int customer_id;
    private int white_go;
    private int white_come;
    private int green_go;
    private int green_come;
    private String vegetable_come;
    private float owe_money;
    private float all_money;
    private int sum_frame;
    private int data_date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customer_id;
    }

    public void setCustomerId(int customer_id) {
        this.customer_id = customer_id;
    }

    public int getWhiteGo() {
        return white_go;
    }

    public void setWhiteGo(int white_go) {
        this.white_go = white_go;
    }

    public int getWhiteCome() {
        return white_come;
    }

    public void setWhiteCome(int white_come) {
        this.white_come = white_come;
    }

    public int getGreenGo() {
        return green_go;
    }

    public void setGreenGo(int green_go) {
        this.green_go = green_go;
    }

    public int getGreenCome() {
        return green_come;
    }

    public void setGreenCome(int green_come) {
        this.green_come = green_come;
    }

    public JSONArray getVegetableCome() throws JSONException {
        ArrayList<JSONObject> list = new ArrayList<JSONObject>();
        JSONArray jsonArray = null;
        if (vegetable_come != null) {
            jsonArray = new JSONArray(vegetable_come);
        }

        return jsonArray;
    }

    public String getVegetableComeStr() {
        return vegetable_come;
    }

    public void setVegetableCome(String vegetable_come) {
        this.vegetable_come = vegetable_come;
    }

    public float getOweMoney() {
        return owe_money;
    }

    public void setOweMoney(float owe_money) {
        this.owe_money = VariableUtils.SaveOneNum(owe_money);
    }

    public float getAllMoney() {
        return all_money;
    }

    public void setAllMoney(float all_money) {
        this.all_money = VariableUtils.SaveOneNum(all_money);
    }

    public int getSumFrame() {
        return sum_frame;
    }

    public void setSumFrame(int sum_frame) {
        this.sum_frame = sum_frame;
    }

    public int getDataDate() {
        return data_date;
    }

    public void setDataDate(int data_date) {
        this.data_date = data_date;
    }


}
