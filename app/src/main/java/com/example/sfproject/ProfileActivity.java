package com.example.sfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private Button profile_editbtn;
    private BottomNavigationView bottomNavigationView;
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
        bottomNavigationView = findViewById(R.id.bottom_navigationview);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId(); // 아이템 ID를 가져옵니다.
                // 아이템 ID에 따라 액티비티를 시작합니다.
                int itemID = item.getItemId();
                if (itemID == R.id.home) {
                    // 홈 아이템을 클릭했을 때 MainActivity로 이동
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                } else if (itemID == R.id.add) {
                    // 글쓰기 아이템을 클릭했을 때 Post_CreateActivity로 이동
                    startActivity(new Intent(ProfileActivity.this, Post_CreateActivity.class));
                } else if (itemID == R.id.noti) {
                    // 알람 아이템을 클릭했을 때 Notification으로 이동
                    startActivity(new Intent(ProfileActivity.this, Notification.class));
                } else if (itemID == R.id.setting) {
                    // 설정 아이템을 클릭했을 때 Profile_EditActivity로 이동
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                } else if (itemID == R.id.search) {
                    // 설정 아이템을 클릭했을 때 Profile_EditActivity로 이동
                    startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
                }

                return false;
            }
        });
    }

    public void img_setting_click(View v){
        Intent intent = new Intent(ProfileActivity.this, Profile_EditActivity.class);
        startActivity(intent);
    }
}