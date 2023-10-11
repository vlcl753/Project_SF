package com.example.sfproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ProfileActivity extends AppCompatActivity {

    private Button profile_editbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profile_editbtn = findViewById(R.id.profile_editbtn);
        profile_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, Profile_EditActivity.class);
                startActivity(intent);
            }
        });
    }

    public void img_setting_click(View v){
        Intent intent = new Intent(ProfileActivity.this, Profile_EditActivity.class);
        startActivity(intent);
    }
}