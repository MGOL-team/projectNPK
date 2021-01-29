package com.barbar.npkproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {

    MainActivity mainActivity;

    private final Map<String, List<JSONObject>> allDataMap;

    private void initializeListsOfMaps() {
        allDataMap.put("rating", new ArrayList<>());
    }

    public Database (DatabaseReference myRef, MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        allDataMap = new HashMap<>();

        initializeListsOfMaps();
        fillMapFromDatabase(myRef);
    }

    private void fillMapFromDatabase (DatabaseReference myRef) {
        myRef.child("rating").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String data = snapshot.getValue(String.class);
                try {
                    allDataMap.get("rating").add(new JSONObject(data));
                } catch (JSONException ignore) {}
                mainActivity.updateResultField();
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
    }

    public Map<String, List<JSONObject>> getAllDataMap () {
        return allDataMap;
    }
}
