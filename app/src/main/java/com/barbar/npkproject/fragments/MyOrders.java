package com.barbar.npkproject.fragments;

import android.annotation.SuppressLint;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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

public class MyOrders extends Fragment {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    DeliverAdapter adapter;

    ListView listView;
    List<Order> orders = new ArrayList<>();

    ImageButton imageButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MyOrders() {
        // Required empty public constructor
    }

    public static MyOrders newInstance(String param1, String param2) {
        MyOrders fragment = new MyOrders();
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

        database.getReference("orders_list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    JSONObject object = new JSONObject(Objects.requireNonNull(snapshot.getValue(String.class)));
                    if (object.get("login").toString().equals(login)) {
                        orders.add(new Order(
                                object.get("login").toString(),
                                object.get("items").toString(),
                                snapshot.getKey(),
                                object.get("address").toString(),
                                object.get("comments").toString(),
                                object.get("status").toString(),
                                object.get("courier").toString(),
                                object.get("secret_code").toString()
                        ));
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.toString() + "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                for (int i = 0;i < orders.size();i++) {
                    if (orders.get(i).key.equals(key)) {
                        orders.remove(i);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference("temp_orders_list").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    JSONObject object = new JSONObject(Objects.requireNonNull(snapshot.getValue(String.class)));
                    if (object.get("login").toString().equals(login)) {
                        orders.add(new Order(object, snapshot.getKey(), "Ожидает оплаты"));
                    }
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error with JSON", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0;i < orders.size();i++) {
                    if (orders.get(i).status.equals("Ожидает оплаты")) {
                        orders.remove(i);
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);

        listView = view.findViewById(R.id.list_view);
        adapter = new DeliverAdapter(getContext());
        listView.setAdapter(adapter);

        imageButton = view.findViewById(R.id.new_order_button);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), view.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new MakeOrderFragment();
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.my_orders, fragment);
                ft.commit();
            }
        });

        return view;
    }

    private class DeliverAdapter extends ArrayAdapter<Order> {

        public DeliverAdapter(Context context) {
            super(context, R.layout.order_items, orders);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View some_view = inflater.inflate(R.layout.my_orders_item, parent, false);
            TextView order_name =  some_view.findViewById(R.id.order_name);
            TextView order_courier =  some_view.findViewById(R.id.order_courier);
            TextView order_address =  some_view.findViewById(R.id.order_adres);
            TextView order_comment =  some_view.findViewById(R.id.comment_text);
            TextView order_status =  some_view.findViewById(R.id.order_status);
            Button acceptButton = some_view.findViewById(R.id.accept_button);

            if (orders.get(position).courier.length() < 2){
                order_courier.setHeight(0);
            }
            if (orders.get(position).comments.length() < 2){
                order_comment.setHeight(0);
            }

            order_name.setText("Товар: " + orders.get(position).what);
            order_courier.setText("Курьер: @" + orders.get(position).courier);
            order_address.setText("Адрес: " + orders.get(position).address);
            order_comment.setText(orders.get(position).comments);

            order_status.setText("Статус: " + orders.get(position).status);

            acceptButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!orders.get(position).status.equals("Доставлено")) {
                        return;
                    }
                    Fragment fragment = null;
                    try {
                        fragment = new PutMark(new JSONObject(orders.get(position).toString()), "customer", orders.get(position).key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.my_orders, fragment);
                    ft.commit();
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