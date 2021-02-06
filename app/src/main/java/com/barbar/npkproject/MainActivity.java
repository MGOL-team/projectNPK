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
    TabItem my_order;
    TabItem account;
    TabItem check_order;
    TabItem settings;
    public ViewPager tabPager;

    /// test 123

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textLoginName = findViewById(R.id.text_login_name);
        textLoginName.setText(getLogin());
        tabLayout = findViewById(R.id.tabBar);
        tabPager = findViewById(R.id.tabPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        tabPager.setAdapter(pagerAdapter);
        settings = findViewById(R.id.settings_tab);
        my_order = findViewById(R.id.my_order_tab);
        account = findViewById(R.id.account_tab);
        check_order = findViewById(R.id.check_order_tab);

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

    }

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
                    return new My_orders_fragment(MainActivity.this);
                case 1:
                    return new AccountFragment();
                case 2:
                    return new DeliverOrder();
                case 3:
                    return new SettingsFragment();
                case 4:
                    return new fragment_confirm();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs + 1;
        }
    }

    private String getLogin () {
        SharedPreferences sPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        return sPref.getString("login", "COMMON TEXT");
    }
}