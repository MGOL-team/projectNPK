package com.barbar.npkproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    SharedPreferences sPref;

    private EditText ETemail;
    private EditText ETpassword;
    private EditText ETfirst_name;
    private EditText ETsecond_name;
    private Button registration_button;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users_list");

    List<String> user_names_list = new ArrayList<>();
    List<String> user_passes_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ETemail = findViewById(R.id.et_email);
        ETpassword = findViewById(R.id.et_password);
        ETfirst_name = findViewById(R.id.first_name);
        ETsecond_name = findViewById(R.id.second_name);
        registration_button = findViewById(R.id.button_reg);

        ETemail.setText(getLogin());
        ETpassword.setText(getPassword());
        ETfirst_name.setText(getFirstName());
        ETsecond_name.setText(getSecondName());

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data_snapshot = snapshot.getValue(String.class);
                try {
                    assert data_snapshot != null;
                    JSONObject data = new JSONObject(data_snapshot);
                    user_names_list.add((String) data.get("login"));
                    user_passes_list.add((String) data.get("password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (user_names_list.get(user_names_list.size() - 1).equals(ETemail.getText().toString()) &&
                        user_passes_list.get(user_passes_list.size() - 1).equals(hashFunction(ETpassword.getText().toString() + ETemail.getText().toString()))) {
                    goToNextPage();
                }
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

        registration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ETemail.getText().toString();
                String password = ETpassword.getText().toString();

                if (email.length() < 2 || password.length() < 3) {
                    Toast.makeText(getApplicationContext(), "Слишком короткий логин или пароль", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.startsWith("id_")) {
                    Toast.makeText(getApplicationContext(), "Данное имя пользователя запрещено", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (email.contains("?") || email.contains(" ")) {
                    Toast.makeText(getApplicationContext(), "Данное имя пользователя запрещено", Toast.LENGTH_SHORT).show();
                    return;
                }

                registration(email, password);
            }
        });
    }


    private void registration (String email, String password) {

        String firstName = ETfirst_name.getText().toString();
        String secondName = ETsecond_name.getText().toString();

        if (firstName.length() < 2 || secondName.length() < 2) {
            Toast.makeText(getApplicationContext(), "Пожалуйста, введите ваше имя и фамилию", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.contains("admin")) {
            Toast.makeText(getApplicationContext(), "Данное имя пользователя используется системой", Toast.LENGTH_LONG).show();
            // TODO redo ☻ return;
        }

        for (int i = 0;i < user_names_list.size();i++) {
            if (email.equals(user_names_list.get(i))) {
                Toast.makeText(getApplicationContext(), "Данное имя пользователя уже занято", Toast.LENGTH_LONG).show();
                return;
            }
        }

        saveText();

        JSONObject data = new JSONObject();
        try {
            data.put("login", email);
            data.put("password", hashFunction(password + email));
            data.put("first_name", firstName);
            data.put("second_name", secondName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        database.getReference("users_list").push().setValue(data.toString());
    }

    private void goToNextPage () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveText() {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("login", ETemail.getText().toString());
        ed.putString("password", ETpassword.getText().toString());
        ed.putString("first_name", ETfirst_name.getText().toString());
        ed.putString("second_name", ETsecond_name.getText().toString());
        ed.apply();
    }

    private String getLogin () {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "");
    }

    private String getPassword () {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("password", "");
    }

    private String getFirstName () {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("first_name", "");
    }

    private String getSecondName () {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("second_name", "");
    }

    private String hashFunction (String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes());
            return new String(messageDigest.digest());
        } catch (Exception ignore) {
            return "Error in hashFunction";
        }
    }
}