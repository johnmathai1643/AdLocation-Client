package com.example.john.locationads;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabViewer extends Fragment {

    ViewPager pager;
    ViewPagerAdapter adapter;
//    SlidingTabLayout tabs;
    CharSequence Titles[]={"Home","Events"};
    int Numboftabs =2;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_viewer, container, false);

        // Creating The Toolbar and setting it as the Toolbar for the activity
//
//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        setSupportActionBar(toolbar);
//
//
//        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
//        adapter =  new ViewPagerAdapter(getSupportFragmentManager(),Titles,Numboftabs);
//
//        // Assigning ViewPager View and setting the adapter
//        pager = (ViewPager) findViewById(R.id.pager);
//        pager.setAdapter(adapter);
//
//        // Assiging the Sliding Tab Layout View
//        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
//
//        // Setting Custom Color for the Scroll bar indicator of the Tab View
//        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return getResources().getColor(R.color.tabsScrollColor);
//            }
//        });
//
//        // Setting the ViewPager For the SlidingTabsLayout
//        tabs.setViewPager(pager);


        return view;
    }


}
