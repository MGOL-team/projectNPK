package com.barbar.npkproject.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.barbar.npkproject.DataAnalyze;
import com.barbar.npkproject.Database;
import com.barbar.npkproject.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PutMark#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PutMark extends Fragment {

    private String nameOfUser;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PutMark(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PutMark.
     */
    // TODO: Rename and change types and number of parameters
    public static PutMark newInstance(String param1, String param2) {
        PutMark fragment = new PutMark(null);
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
    }

    private int mark = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_put_mark, container, false);

        ImageButton scoreButton1 = view.findViewById(R.id.star_1);
        ImageButton scoreButton2 = view.findViewById(R.id.star_2);
        ImageButton scoreButton3 = view.findViewById(R.id.star_3);
        ImageButton scoreButton4 = view.findViewById(R.id.star_4);
        ImageButton scoreButton5 = view.findViewById(R.id.star_5);
        Button confirmButton = view.findViewById(R.id.mark_button);
        EditText commentField = view.findViewById(R.id.text_edit);

        scoreButton1.setOnClickListener(v -> mark = 1);
        scoreButton2.setOnClickListener(v -> mark = 2);
        scoreButton3.setOnClickListener(v -> mark = 3);
        scoreButton4.setOnClickListener(v -> mark = 4);
        scoreButton5.setOnClickListener(v -> mark = 5);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comments = commentField.getText().toString();
                toMark(comments, nameOfUser);
            }
        });

        return view;
    }

    private boolean toMark (String comments, String whoDoIRate) {

        if (mark < 0) {
            Toast.makeText(getContext(), "Поставьте оценку", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            JSONObject object = new JSONObject();
            object.put("value", mark);
            object.put("time", new Date().getTime());
            object.put("login", getLogin());
            object.put("type", "estimator");
            object.put("comment", comments);
            String myRating = updateResultField();
            if (myRating.equals("NaN")) {
                object.put("apr_rating", 1);
            } else {
                object.put("apr_rating", updateResultField());
            }

            database.getReference("users").child(whoDoIRate).child("rating").push().setValue(object.toString());

        } catch (Exception e) {
            Toast.makeText(getActivity(), "ERROR", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getLogin () {
        SharedPreferences sPref = Objects.requireNonNull(this.getActivity()).getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }

    public String updateResultField() {
        List<JSONObject> ratingList = new Database(database.getReference("users").child(getLogin()),  this).getAllDataMap().get("rating");
       return new DataAnalyze().getResult(ratingList);
    }
}