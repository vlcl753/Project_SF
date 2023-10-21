package com.example.sfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Notification extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private ImageView imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        imgBack = findViewById(R.id.img_back); // 이미지뷰를 XML 레이아웃과 연결

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이미지뷰 클릭 시 MainActivity로 이동
                startActivity(new Intent(Notification.this, MainActivity.class));
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
                    startActivity(new Intent(Notification.this, MainActivity.class));
                } else if (itemID == R.id.add) {
                    // 글쓰기 아이템을 클릭했을 때 Post_CreateActivity로 이동
                    startActivity(new Intent(Notification.this, Post_CreateActivity.class));
                } else if (itemID == R.id.noti) {
                    // 알람 아이템을 클릭했을 때 Notification으로 이동
                    startActivity(new Intent(Notification.this, Notification.class));
                } else if (itemID == R.id.setting) {
                    // 설정 아이템을 클릭했을 때 Profile_EditActivity로 이동
                    startActivity(new Intent(Notification.this, Profile_EditActivity.class));
                } else if (itemID == R.id.search) {
                    // 설정 아이템을 클릭했을 때 Profile_EditActivity로 이동
                    startActivity(new Intent(Notification.this, SearchActivity.class));
                }

                return false;
            }
        });

    }
}