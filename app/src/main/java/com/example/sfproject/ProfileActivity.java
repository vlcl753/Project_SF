package com.example.sfproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void img_setting_click(View v){
        Intent intent = new Intent(ProfileActivity.this, TestActivity.class);
        startActivity(intent);
    }
}