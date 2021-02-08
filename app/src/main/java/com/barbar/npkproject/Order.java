package com.barbar.npkproject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

public class Order {
    public String from_who;
    public String what;
    public String key;
    public String address;
    public String comments;
    public String status;
    public String courier;
    public String secretCode;

    public Order(String from_who, String what, String key, String address, String comments, String status, String courier, String secretCode) {
        this.from_who = from_who;
        this.what = what;
        this.key = key;
        this.address = address;
        this.comments = comments;
        this.status = status;
        this.courier = courier;
        this.secretCode = secretCode;
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
            this.secretCode = object.get("secret_code").toString();
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
            object.put("status", status);
            object.put("courier", courier);
            object.put("secret_code", secretCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    public static String generateSecretCode (int size) {
        StringBuilder code = new StringBuilder();
        for (int i = 0;i < size;i++) {
            int rand = (int) (Math.random() * 5);
            if (rand < 2) {
                rand = (int) (Math.random() * 26) % 26;
                code.append((char) (rand + 97));
            } else if (rand < 4) {
                rand = (int) (Math.random() * 26) % 26;
                code.append((char) (rand + 97));
            } else {
                rand = (int) (Math.random() * 9) % 9;
                code.append((char) (rand + 1 + 48));
            }
        }
        return code.toString();
    }


}
