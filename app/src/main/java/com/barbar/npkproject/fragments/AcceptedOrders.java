package com.barbar.npkproject.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.barbar.npkproject.Order;
import com.barbar.npkproject.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AcceptedOrders extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    DeliverAdapter adapter;

    ListView listView;
    List<Order> orders;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AcceptedOrders() {
        // Required empty public constructor
    }

    public static AcceptedOrders newInstance(String param1, String param2) {
        AcceptedOrders fragment = new AcceptedOrders();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deliver_order, container, false);

        orders = new ArrayList<>();

        listView = view.findViewById(R.id.list_view);
        adapter = new DeliverAdapter(getContext());
        listView.setAdapter(adapter);

        String login = getLogin();

        database.getReference("orders_list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    JSONObject object = new JSONObject(snapshot.getValue(String.class));
                    if (object.get("courier").toString().equals(login)) {
                        orders.add(new Order(object, snapshot.getKey(), (String) object.get("status")));
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "" + e.toString(), Toast.LENGTH_LONG).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), view.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private class DeliverAdapter extends ArrayAdapter<Order> {

        public DeliverAdapter(Context context) {
            super(context, R.layout.order_items, orders);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View some_view = inflater.inflate(R.layout.order_items, parent, false);
            TextView us_id =  some_view.findViewById(R.id.user_id);
            TextView order_name =  some_view.findViewById(R.id.order_name);
            TextView order_adress =  some_view.findViewById(R.id.order_adress);
            TextView order_comment =  some_view.findViewById(R.id.comment_text);

            us_id.setText(orders.get(position).from_who);
            order_name.setText(orders.get(position).what);
            order_adress.setText(orders.get(position).address);
            us_id.setText(orders.get(position).from_who);
            order_comment.setText(orders.get(position).comments);

            return some_view;
        }
    }

    private String getLogin () {
        SharedPreferences sPref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}