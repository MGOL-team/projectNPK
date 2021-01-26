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

        myBase = new Database(database.getReference("users").child(getLogin()));

        updateResultField();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                    myRef.child(textViewUser.getText().toString()).child("rating").push().setValue("" + num + "");
                    textViewUser.setText("");
                    editTextField.setText("");
                    updateResultField();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Введите нормальное число", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateResultField () {
        List<String> ratingList = myBase.getAllDataMap().get("rating");

        double sum = 0;

        try {
            for (String data : ratingList) {
                double num = Double.parseDouble(data);
                sum += num;
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ошибка в updateResultField", Toast.LENGTH_SHORT).show();
        }

        sum /= ratingList.size();


        String result = String.valueOf(sum);
        if (result.length() > 4) {
            result = result.substring(0, 4);
        }

        textView.setText("Rating : " + result);
    }

    private String getLogin () {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}