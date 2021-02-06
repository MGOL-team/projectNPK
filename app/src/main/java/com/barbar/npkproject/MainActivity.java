package com.barbar.npkproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.barbar.npkproject.fragments.AccountFragment;
import com.barbar.npkproject.fragments.DeliverOrder;
import com.barbar.npkproject.fragments.My_orders_fragment;
import com.barbar.npkproject.fragments.SettingsFragment;
import com.barbar.npkproject.fragments.fragment_confirm;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
public class MainActivity extends AppCompatActivity {

    TextView textLoginName;
    TabLayout tabLayout;
    public ViewPager tabPager;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textLoginName = findViewById(R.id.text_login_name);
        textLoginName.setText('@' + getLogin());
        tabLayout = findViewById(R.id.tabBar);
        tabPager = findViewById(R.id.tabPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        tabPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /*listView = findViewById(R.id.list_view);
        MarkAdapter adapter = new MarkAdapter(this);
        listView.setAdapter(adapter);

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



        myBase = new Database(database.getReference("users").child(getLogin()), this);

        updateResultField();

        sendButton.setOnClickListener(view -> {
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
                Toast.makeText(getApplicationContext(), "Введите нормальное число", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

/*    private class User {
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
            TextView mark_us = (TextView) some_view.findViewById(R.id.user_mark);
            TextView us_id = (TextView) some_view.findViewById(R.id.id_user);
            mark_us.setText(users.get(position).mark);
            us_id.setText(users.get(position).from_who);

            return some_view;
        }
    }
*/
    public class PagerAdapter extends FragmentPagerAdapter{

        private int numOfTabs;

        public PagerAdapter(@NonNull FragmentManager fm, int numOfTabs) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.numOfTabs = numOfTabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new AccountFragment();
                case 1:
                    return new My_orders_fragment(MainActivity.this);
                case 2:
                    return new DeliverOrder();
                case 3:
                    return new SettingsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }

    private String getLogin () {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}