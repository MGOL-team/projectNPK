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
import android.widget.Button;
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
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class AllDeliverOrder extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("orders_list");

    DeliverAdapter adapter;

    ListView listView;
    List<Order> orders = new ArrayList<>();

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public AllDeliverOrder() {
        // Required empty public constructor
    }



    public static AllDeliverOrder newInstance(String param1, String param2) {
        AllDeliverOrder fragment = new AllDeliverOrder();
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

        String login = getLogin();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    JSONObject object = new JSONObject(Objects.requireNonNull(snapshot.getValue(String.class)));
                    if (!object.get("login").toString().equals(login)) {
                        orders.add(new Order(object, snapshot.getKey(), "status"));
                    }

                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getContext(), "Adapter == null", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deliver_order, container, false);


        listView = view.findViewById(R.id.list_view);
        adapter = new DeliverAdapter(getContext());
        listView.setAdapter(adapter);

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
            TextView us_id = some_view.findViewById(R.id.user_id);
            TextView orderField = some_view.findViewById(R.id.order_text);
            Button acceptButton = some_view.findViewById(R.id.accept_button);

            orderField.setText(orders.get(position).what);
            us_id.setText(orders.get(position).from_who);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Order order = orders.get(position);
                    myRef.child(order.key).removeValue();

                    JSONObject object = new JSONObject();
                    try {
                        object.put("items", order.what);
                        object.put("address", order.address);
                        object.put("comments", order.comments);
                        object.put("login", order.from_who);
                        object.put("type", "estimator");
                        object.put("courier", getLogin());
                        object.put("status", "Заказ доставляется");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    myRef.child(order.key).setValue(object.toString());

                    orders.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });

            return some_view;
        }
    }

    private String getLogin () {
        SharedPreferences sPref = Objects.requireNonNull(this.getActivity()).getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}