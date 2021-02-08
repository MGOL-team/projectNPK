package com.barbar.npkproject.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.barbar.npkproject.DataAnalyze;
import com.barbar.npkproject.Database;
import com.barbar.npkproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class PutMark extends Fragment {

    private JSONObject data;
    private String typeOfCurrentUser;
    private Database myBase;
    String key;

    EditText mark_key;
    EditText commentField;
    RatingBar putMark;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public PutMark(JSONObject data, String typeOfCurrentUser, String key) {
        this.data = data;
        this.typeOfCurrentUser = typeOfCurrentUser;
        this.key = key;
    }

    public static PutMark newInstance(String param1, String param2) {
        PutMark fragment = new PutMark(null, null, null);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        myBase = new Database(database.getReference("users").child(getLogin()));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_put_mark, container, false);

        ImageButton button_back = view.findViewById(R.id.button_back);
        mark_key = view.findViewById(R.id.mark_key);
        Button confirmButton = view.findViewById(R.id.mark_button);
        commentField = view.findViewById(R.id.text_edit);
        putMark = view.findViewById(R.id.rating_stars);
        putMark.setStepSize(1);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMark();
            }
        });

        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeActivity();
            }
        });
        if (typeOfCurrentUser != "courier") {
            mark_key.setEnabled(false);
            mark_key.setBackgroundColor(1);
            try {
                mark_key.setHint("ВАШ КОД:\n" + data.get("secret_code").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return view;
    }

    private void changeActivity(){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(this);
            ft.commit();
    }

    private boolean toMark () {
        if (putMark.getRating() == 0) {
            Toast.makeText(getContext(), "Поставьте оценку", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            if (typeOfCurrentUser.equals("courier")) {
                if (!data.get("secret_code").toString().equals(mark_key.getText().toString())) {
                    Toast.makeText(getContext(), "Неправильный код", Toast.LENGTH_LONG).show();
                    return false;
                }
            } else {
                if (!data.get("status").toString().equals("Завершено")) {
                    Toast.makeText(getContext(), "Пожалуйста, дождитесь доставки", Toast.LENGTH_LONG).show();
                    return false;
                }
            }

            JSONObject object = new JSONObject();
            object.put("value", putMark.getRating());
            object.put("time", new Date().getTime());
            object.put("login", getLogin());
            object.put("type", "estimator");
            object.put("comment", commentField.getText().toString());
            String myRating = updateResultField();
            if (myRating.equals("NaN")) {
                object.put("apr_rating", 1);
            } else {
                object.put("apr_rating", updateResultField());
            }

            if (typeOfCurrentUser.equals("courier")) {
                database.getReference("orders_list").child(key).removeValue();
                data.put("status", "Завершено");
                database.getReference("orders_list").child(key).setValue(data.toString());
                database.getReference("users").child(data.get("login").toString()).child("rating").push().setValue(object.toString());
            } else {
                database.getReference("users").child(data.get("courier").toString()).child("rating").push().setValue(object.toString());
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
            //Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            return false;
        }
        changeActivity();
        Toast.makeText(getContext(), "Благодарим за оценку!", Toast.LENGTH_LONG).show();
        return true;
    }

    private String getLogin () {
        SharedPreferences sPref = Objects.requireNonNull(this.getActivity()).getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }

    public String updateResultField() {
        List<JSONObject> ratingList = myBase.getAllDataMap().get("rating");
       return new DataAnalyze().getResult(ratingList);
    }
}