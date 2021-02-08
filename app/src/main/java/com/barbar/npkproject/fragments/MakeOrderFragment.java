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
import android.widget.Toast;

import com.barbar.npkproject.MainActivity;
import com.barbar.npkproject.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.IndoorBuilding;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MakeOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MakeOrderFragment extends Fragment {

    private EditText ET_field_items;
    private EditText ET_field_address;
    private EditText ET_field_comments;
    private Button confirm_button;

    MainActivity mainActivity;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("temp_orders_list");


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MakeOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment my_orders_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MakeOrderFragment newInstance(String param1, String param2) {
        MakeOrderFragment fragment = new MakeOrderFragment();
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

        View view = inflater.inflate(R.layout.fragment_my_orders_fragment, container, false);

        ET_field_address = view.findViewById(R.id.address_field);
        ET_field_comments = view.findViewById(R.id.comments_field);
        ET_field_items = view.findViewById(R.id.items_field);
        confirm_button = view.findViewById(R.id.confirm_button);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.clear();
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        googleMap.addMarker(markerOptions);
                    }
                });
                googleMap.setOnIndoorStateChangeListener(new GoogleMap.OnIndoorStateChangeListener() {
                    @Override
                    public void onIndoorBuildingFocused() {
                        IndoorBuilding building = googleMap.getFocusedBuilding();
                        Toast.makeText(getContext(), "" + building.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onIndoorLevelActivated(IndoorBuilding indoorBuilding) {

                    }
                });
            }
        });


        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ET_field_items.getText().toString().length() < 2 || ET_field_address.getText().toString().length() < 5) {
                    Toast.makeText(getContext(), "Введите корректный заказ", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject data = new JSONObject();
                    try {
                        data.put("items", ET_field_items.getText().toString());
                        data.put("address", ET_field_address.getText().toString());
                        data.put("comments", ET_field_comments.getText().toString());
                        data.put("login", getLogin());
                        data.put("type", "estimator");
                        data.put("courier", "");
                        /// TODO another status
                        data.put("status", "Оплачено");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    saveText(data.toString());

                    // TODO recomment
                    //myRef.child(getLogin()).setValue(data.toString());

                    database.getReference("orders_list").push().setValue(data.toString());

                    Fragment fragment = new FragmentConfirm();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.my_orders, fragment);
                    ft.commit();
                }
            }
        });

        return view;
    }


    private void saveText(String order) {
        SharedPreferences sPref = this.getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("temp_order", order);
        ed.apply();
    }

    private String getLogin () {
        SharedPreferences sPref = this.getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);

        return sPref.getString("login", "COMMON TEXT");
    }


}