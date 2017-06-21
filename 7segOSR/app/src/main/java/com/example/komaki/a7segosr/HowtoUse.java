package com.example.komaki.a7segosr;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HowtoUse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_howto_use);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new FragmentAdapter(getSupportFragmentManager()));
    }
}
