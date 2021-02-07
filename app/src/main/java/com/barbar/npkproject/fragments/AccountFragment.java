package com.barbar.npkproject.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.barbar.npkproject.DataAnalyze;
import com.barbar.npkproject.Database;
import com.barbar.npkproject.MainActivity;
import com.barbar.npkproject.R;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private static Database myBase;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    static TextView textView;
    EditText editTextField;
    Button sendButton;
    TextView textViewUser;
    TextView userName;

    ListView listView;

    List<User> users = new ArrayList<User>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        textView = view.findViewById(R.id.text_view);
        editTextField = view.findViewById(R.id.text_edit);
        sendButton = view.findViewById(R.id.send_button);
        textViewUser = view.findViewById(R.id.text_edit_user);
        userName = view.findViewById(R.id.user_name);

        listView = view.findViewById(R.id.list_view);
        MarkAdapter adapter = new MarkAdapter(getContext());
        listView.setAdapter(adapter);

        userName.setText(getFullName());

        myRef.child(getLogin()).child("rating").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    JSONObject object = new JSONObject(Objects.requireNonNull(snapshot.getValue(String.class)));

                    users.add(new User(object.get("value").toString(), object.get("login").toString()));
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

        myBase = new Database(database.getReference("users").child(getLogin()),  this);

        updateResultField();

        sendButton.setOnClickListener(v -> {
            String messageValue = editTextField.getText().toString();
            updateResultField();
            if (messageValue.equals("")) {
                Toast.makeText(getActivity(), "Введите число", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double num = Double.parseDouble(messageValue);

                if (num < 1 || num > 5) {
                    Toast.makeText(getActivity(), "Введите число от 1 до 5", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Введите нормальное число", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

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
            TextView mark_us =  some_view.findViewById(R.id.user_mark);
            TextView us_id =  some_view.findViewById(R.id.id_user);
            mark_us.setText('@' + users.get(position).mark);
            us_id.setText(users.get(position).from_who);

            return some_view;
        }
    }

    @SuppressLint("SetTextI18n")
    public static void updateResultField() {
        List<JSONObject> ratingList = myBase.getAllDataMap().get("rating");

        textView.setText(new DataAnalyze().getResult(ratingList));
    }

    private String getLogin () {
        SharedPreferences sPref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }

    private String getFullName () {
        SharedPreferences sPref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("first_name", "COMMON TEXT") + " " + sPref.getString("second_name", "COMMON TEXT") ;
    }
}