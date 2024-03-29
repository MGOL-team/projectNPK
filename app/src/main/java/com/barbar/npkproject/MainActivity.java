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
import com.barbar.npkproject.fragments.MakeOrderFragment;
import com.barbar.npkproject.fragments.MyOrders;
import com.barbar.npkproject.fragments.SettingsFragment;
import com.barbar.npkproject.fragments.delivery_swap;
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
        tabLayout.setupWithViewPager(tabPager);

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

        int[] imageResId = {
                R.drawable.account, R.drawable.make_order, R.drawable.delivery, R.drawable.settings
        };

        for (int i = 0; i < imageResId.length; i++) {
            tabLayout.getTabAt(i).setIcon(imageResId[i]);
        }

    }

    private class PagerAdapter extends FragmentPagerAdapter{

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
                    return new MyOrders();
                case 2:
                    return new delivery_swap();
                case 3:
                    return new SettingsFragment();
                default:
                    return null;
            }
        }

        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0:
                    return "АККАУНТ";
                case 1:
                    return "ЗАКАЗАТЬ";
                case 2:
                    return "ДОСТАВИТЬ";
                case 3:
                    return "НАСТРОЙКИ";
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