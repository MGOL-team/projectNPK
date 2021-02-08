package com.barbar.npkproject;

import org.json.JSONException;
import org.json.JSONObject;

public class Order {
    public String from_who;
    public String what;
    public String key;
    public String address;
    public String comments;
    public String status;
    public String courier;

    public Order(String from_who, String what, String key, String address, String comments, String status, String courier) {
        this.from_who = from_who;
        this.what = what;
        this.key = key;
        this.address = address;
        this.comments = comments;
        this.status = status;
        this.courier = courier;
    }

    public Order (JSONObject object, String key, String status) {
        try {
            from_who = object.get("login").toString();
            what = object.get("items").toString();
            this.key = key;
            this.address = object.get("address").toString();
            this.comments = object.get("comments").toString();
            this.status = status;
            this.courier = object.get("courier").toString();
        } catch (Exception ignore) {}
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("login", from_who);
            object.put("items", what);
            object.put("address", address);
            object.put("comments", comments);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }


}
