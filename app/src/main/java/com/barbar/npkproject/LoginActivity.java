package com.barbar.npkproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sPref;

    private EditText ETemail;
    private EditText ETpassword;
    private String first_name;
    private String second_name;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users_list");

    List<String> user_names_list = new ArrayList<>();
    List<String> user_passes_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ETemail = findViewById(R.id.et_email);
        ETpassword = findViewById(R.id.et_password);

        ETemail.setText(getLogin());
        ETpassword.setText(getPassword());

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data_snapshot = snapshot.getValue(String.class);
                try {
                    assert data_snapshot != null;
                    JSONObject data = new JSONObject(data_snapshot);
                    user_names_list.add((String) data.get("login"));
                    user_passes_list.add((String) data.get("password"));
                    first_name = data.get("first_name").toString();
                    second_name = data.get("second_name").toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (user_names_list.get(user_names_list.size() - 1).equals(ETemail.getText().toString()) &&
                        user_passes_list.get(user_passes_list.size() - 1).equals(hashFunction(ETpassword.getText().toString() + ETemail.getText().toString()))) {
                    saveText();
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

        findViewById(R.id.button_log).setOnClickListener(this);
        findViewById(R.id.button_reg).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String email = ETemail.getText().toString();
        String password = ETpassword.getText().toString();

        if (view.getId() == R.id.button_reg) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
        else if (view.getId() == R.id.button_log) {

            if (successLogReg) {
                saveText();
                goToNextPage();
            }
            signing(email, password);
        }
    }

    private boolean successLogReg = false;

    private void signing (String email, String password) {
        password = hashFunction(password + email);
        for (int i = 0;i < user_names_list.size();i++) {
            if (email.equals(user_names_list.get(i)) && password.equals(user_passes_list.get(i))) {
                Toast.makeText(getApplicationContext(), "singing", Toast.LENGTH_SHORT).show();
                successLogReg = true;
                break;
            }
        }
        if (!successLogReg) {
            Toast.makeText(getApplicationContext(), "this user does not exist", Toast.LENGTH_LONG).show();
        }
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
        ed.putString("first_name", first_name);
        ed.putString("second_name", second_name);
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