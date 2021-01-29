package com.barbar.npkproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderActivity extends AppCompatActivity {

    private EditText ET_field_items;
    private EditText ET_field_address;
    private EditText ET_field_comments;
    private Button confirm_button;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("temp_orders_list");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ET_field_address = findViewById(R.id.address_field);
        ET_field_comments = findViewById(R.id.comments_field);
        ET_field_items = findViewById(R.id.items_field);
        confirm_button = findViewById(R.id.confirm_button);



        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ET_field_items.getText().toString().length() < 2 || ET_field_address.getText().toString().length() < 5) {
                    Toast.makeText(getApplicationContext(), "Введите корректный заказ", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("items", ET_field_items.getText().toString());
                        data.put("address", ET_field_address.getText().toString());
                        data.put("comments", ET_field_comments.getText().toString());
                        data.put("login", getLogin());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    saveText(data.toString());
                    myRef.child(getLogin()).setValue(data.toString());
                    goToTheConfirmPage();
                }
            }
        });
    }

    private void goToTheConfirmPage () {
        Intent intent = new Intent(this, ConfirmOrderActivity.class);
        startActivity(intent);
    }

    private void saveText(String order) {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("temp_order", order);
        ed.apply();
    }

    private String getLogin () {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}