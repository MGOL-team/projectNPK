package com.barbar.npkproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sPref;

    private EditText ETemail;
    private EditText ETpassword;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ETemail = findViewById(R.id.et_email);
        ETpassword = findViewById(R.id.et_password);

        if (!getLogin().equals("COMMON TEXT")) {
            Toast.makeText(getApplicationContext(), getLogin(), Toast.LENGTH_SHORT).show();
            goToNextPage();
        } else {
            Toast.makeText(getApplicationContext(), "Сохраненных аккаунтов не обнаружено", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.button_log).setOnClickListener(this);
        findViewById(R.id.button_reg).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String email = ETemail.getText().toString();
        String password = ETpassword.getText().toString();

        if (email.length() < 1 || password.length() < 3) {
            return;
        }

        if (view.getId() == R.id.button_log) {
            if (!getLogin().equals("COMMON TEXT")) {
                signing(email, password);
            } else {
                Toast.makeText(getApplicationContext(), "Данного аккаунта не существует", Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.button_reg) {
            registration(ETemail.getText().toString(), ETpassword.getText().toString());
        }

        if (successLogReg) {
            saveText();
            goToNextPage();
        }
    }

    private boolean successLogReg = false;

    private void signing (String email, String password) {
        Toast.makeText(getApplicationContext(), "singing", Toast.LENGTH_SHORT).show();
        successLogReg = true;
    }

    private void registration (String email, String password) {
        Toast.makeText(getApplicationContext(), "registration", Toast.LENGTH_SHORT).show();
        successLogReg = true;
        database.getReference("users_list").push().setValue(email);
    }

    private void goToNextPage () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void saveText() {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("login", "id_" + ETemail.getText().toString());
        ed.putString("password", ETpassword.getText().toString());
        ed.apply();
//        Toast.makeText(MainActivity.this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    private String getLogin () {
        sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
//        Toast.makeText(MainActivity.this, "Text loaded", Toast.LENGTH_SHORT).show();
    }
}