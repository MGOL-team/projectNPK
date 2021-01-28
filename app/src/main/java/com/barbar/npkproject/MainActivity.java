package com.barbar.npkproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    Database myBase;

    TextView textView;
    EditText editTextField;
    Button sendButton;
    TextView textLoginName;
    TextView textViewUser;

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

    @SuppressLint("SetTextI18n")
    public void updateResultField () {
        List<String> ratingList = myBase.getAllDataMap().get("rating");

        textView.setText(new DataAnalyze().getResult(ratingList));
    }

    private String getLogin () {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}