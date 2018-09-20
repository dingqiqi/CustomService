package com.lakala.cloudpos.customservicedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lakala.cloudpos.cusservicelib.ServiceManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "";

        ServiceManager.startCustomService(this, url);
    }
}
