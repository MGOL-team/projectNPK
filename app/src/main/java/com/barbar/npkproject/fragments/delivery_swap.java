package com.barbar.npkproject.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.barbar.npkproject.MainActivity;
import com.barbar.npkproject.R;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link delivery_swap#newInstance} factory method to
 * create an instance of this fragment.
 */
public class delivery_swap extends Fragment {

    TabLayout orderLayout;
    public ViewPager orderPager;
    boolean wasCreated = false;
    View view;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public delivery_swap() {
        // Required empty public constructor
    }

    public static delivery_swap newInstance(String param1, String param2) {
        delivery_swap fragment = new delivery_swap();
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
        if (wasCreated) {
            return view;
        } else {
            wasCreated = true;
        }
        view = inflater.inflate(R.layout.fragment_delivery_swap, container, false);


        orderLayout = view.findViewById(R.id.orderBar);
        orderPager = view.findViewById(R.id.orderPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager(), orderLayout.getTabCount());
        orderPager.setAdapter(pagerAdapter);

        orderLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                orderPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        orderLayout.setupWithViewPager(orderPager);
        // Inflate the layout for this fragment
        return view;
    }


    private class PagerAdapter extends FragmentPagerAdapter {

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
                    return new AllDeliverOrder();
                case 1:
                    return new AcceptedOrders();
                default:
                    return null;
            }
        }

        public CharSequence getPageTitle(int position) {
            switch (position){
                case 1:
                    return "ПРИНЯТЫЕ";
                default:
                    return "ВСЕ";
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }
}