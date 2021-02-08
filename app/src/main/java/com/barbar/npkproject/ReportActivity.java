package com.barbar.npkproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReportActivity extends AppCompatActivity {

    Button button_report;
    ImageButton button_back;
    TextView textView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("report_list");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        button_report = findViewById(R.id.report_button);
        textView = findViewById(R.id.report_text);
        button_back = findViewById(R.id.button_back);

        button_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textView.getText().toString().length() > 5) {
                    myRef.push().setValue(getLogin() + ": " + textView.getText().toString());
                    textView.setText("");
                    Toast.makeText(getApplicationContext(), "Благодарим за отзыв", Toast.LENGTH_LONG).show();
                    goToNextPage();
                } else {
                    Toast.makeText(getApplicationContext(), "Слишком короткий отзыв", Toast.LENGTH_LONG).show();
                }
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextPage();
            }
        });
    }

    private void goToNextPage () {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private String getLogin () {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}