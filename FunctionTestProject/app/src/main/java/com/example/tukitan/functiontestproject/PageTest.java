package com.example.tukitan.functiontestproject;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PageTest extends AppCompatActivity {

    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_test);
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
    }
}
