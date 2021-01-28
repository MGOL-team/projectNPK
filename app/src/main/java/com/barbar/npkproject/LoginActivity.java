package com.barbar.npkproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sPref;

    private EditText ETemail;
    private EditText ETpassword;

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

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = snapshot.getValue(String.class);
                user_names_list.add(data.substring(0, data.indexOf("?")));
                user_passes_list.add(data.substring(data.indexOf("?") + 1));
                //Toast.makeText(getApplicationContext(), data.substring(0, data.indexOf("?")) + " " + data.substring(data.indexOf("?") + 1), Toast.LENGTH_SHORT).show();
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
    protected void onResume() {
        super.onResume();
        if (!getLogin().equals("COMMON TEXT")) {
            Toast.makeText(getApplicationContext(), getLogin() + " " + getPassword(), Toast.LENGTH_SHORT).show();
            signing(getLogin(), getPassword());
            if (successLogReg) {
                goToNextPage();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Сохраненных аккаунтов не обнаружено", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        String email = ETemail.getText().toString();
        String password = ETpassword.getText().toString();

        if (email.length() == 0 && password.length() == 0) {
            signing(getLogin(), getPassword());
            if (successLogReg) {
                goToNextPage();
            }
            return;
        }

        if (email.length() < 2 || password.length() < 3) {
            Toast.makeText(getApplicationContext(), "Слишком короткий логин или пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.startsWith("id_")) {
            Toast.makeText(getApplicationContext(), "Данное имя пользователя уже занято", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.contains("?") || password.contains("?") || email.contains(" ") || password.contains(" ")) {
            Toast.makeText(getApplicationContext(), "Данное имя пользователя уже занято", Toast.LENGTH_SHORT).show();
            return;
        }

        if (view.getId() == R.id.button_log) {
            signing(email, password);
        } else if (view.getId() == R.id.button_reg) {
            registration(email, password);
        }

        if (successLogReg) {
            saveText();
            goToNextPage();
        }
    }

    private boolean successLogReg = false;

    private void signing (String email, String password) {
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

    private void registration (String email, String password) {
        successLogReg = true;

        for (int i = 0;i < user_names_list.size();i++) {
            if (email.equals(user_names_list.get(i))) {
                Toast.makeText(getApplicationContext(), "Данное имя пользователя уже занято", Toast.LENGTH_LONG).show();
                successLogReg = false;
                return;
            }
        }

        saveText();

        Toast.makeText(getApplicationContext(), "registration", Toast.LENGTH_SHORT).show();
        database.getReference("users_list").push().setValue(email + "?" + password);
    }

    private void goToNextPage () {
        Intent intent = new Intent(this, OrderActivity.class);
        startActivity(intent);
    }

    private void saveText() {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("login", ETemail.getText().toString());
        ed.putString("password", ETpassword.getText().toString());
        ed.apply();
    }

    private String getLogin () {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }

    private String getPassword () {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("password", "COMMON TEXT");
    }
}