package com.example.sfproject;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }


    public void LoginClick(View v){
        Intent intent = new Intent(TestActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    public void ProflieClick(View v){
        Intent intent = new Intent(TestActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}