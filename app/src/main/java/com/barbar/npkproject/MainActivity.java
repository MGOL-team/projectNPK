package com.barbar.npkproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    Database myBase;

    TextView textView;
    EditText editTextField;
    Button sendButton;
    TextView textLoginName;
    TextView textViewUser;

    ListView listView;

    ImageButton toDoOrderButton;
    ImageButton toCheckOrdersButton;
    ImageButton goToSettingsButton;
    List<User> users = new ArrayList<User>();
    {
        users.add(new  User("Васька", "котэ"));
        users.add(new  User("Мурзик", "котяра"));
        users.add(new User("Мурка", "кошка"));
        users.add(new  User("Барсик", "котик"));
        users.add(new  User("Лиза", "кошечка"));
    }
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);
        editTextField = findViewById(R.id.text_edit);
        sendButton = findViewById(R.id.send_button);
        textLoginName = findViewById(R.id.text_login_name);
        textViewUser = findViewById(R.id.text_edit_user);
        toDoOrderButton = findViewById(R.id.to_do_order_button);
        toCheckOrdersButton = findViewById(R.id.to_check_orders_button);
        listView = findViewById(R.id.list_view);
        goToSettingsButton = findViewById(R.id.settings_button);

        MarkAdapter adapter = new MarkAdapter(this);

        listView.setAdapter(adapter);

        myRef.child(getLogin()).child("rating").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    JSONObject object = new JSONObject(Objects.requireNonNull(snapshot.getValue(String.class)));

                    //users.add(new User((String) object.get("value"), (String) object.get("login")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        toDoOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        goToSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        toCheckOrdersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
                startActivity(intent);
            }
        });

        textLoginName.setText(getLogin());

        myBase = new Database(database.getReference("users").child(getLogin()), this);

        updateResultField();

        sendButton.setOnClickListener(view -> {
            String messageValue = editTextField.getText().toString();
            updateResultField();
            if (messageValue.equals("")) {
                Toast.makeText(getApplicationContext(), "Введите число", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double num = Double.parseDouble(messageValue);

                if (num < 1 || num > 5) {
                    Toast.makeText(getApplicationContext(), "Введите число от 1 до 5", Toast.LENGTH_SHORT).show();
                    return;
                }
                String myRating = (String) textView.getText();
                if (myRating.equals("NaN")) {
                    myRef.child(textViewUser.getText().toString()).child("rating").push().setValue("" + num + " 1 " + new Date().getTime());
                } else {
                    myRef.child(textViewUser.getText().toString()).child("rating").push().setValue("" + num + " " + myRating + " " + new Date().getTime());
                }
                textViewUser.setText("");
                editTextField.setText("");
                updateResultField();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Введите нормальное число", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class User {
        public  String from_who;
        public  String mark;

        public User(String from_who, String mark) {
            this.from_who = from_who;
            this.mark = mark;
        }
    }

    private class MarkAdapter extends ArrayAdapter<User> {

        public MarkAdapter(Context context) {
            super(context, R.layout.mark_items, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View some_view = inflater.inflate(R.layout.mark_items, parent, false);
            TextView mark_us = (TextView) some_view.findViewById(R.id.user_id_mark);
            TextView us_id = (TextView) some_view.findViewById(R.id.user_id);
            mark_us.setText(users.get(position).mark);
            us_id.setText(users.get(position).from_who);

            return some_view;
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateResultField () {
        List<JSONObject> ratingList = myBase.getAllDataMap().get("rating");

        textView.setText(new DataAnalyze().getResult(ratingList));
    }

    private String getLogin () {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}