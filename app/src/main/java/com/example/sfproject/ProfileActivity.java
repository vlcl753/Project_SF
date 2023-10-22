package com.example.sfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView; // 이미지뷰를 사용하기 위해 추가

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private Button profile_editbtn;
    private BottomNavigationView bottomNavigationView;
    private ImageView profile_post1; // 이미지뷰 선언

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

                // 이미지뷰 클릭 이벤트 설정
                profile_post1 = findViewById(R.id.profile_post1);
        profile_post1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 클릭 이벤트 발생 시 실행할 코드
                // PostActivity로 이동하는 코드를 추가
                Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigationview);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                int itemID = item.getItemId();
                if (itemID == R.id.home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                } else if (itemID == R.id.add) {
                    startActivity(new Intent(ProfileActivity.this, Post_CreateActivity.class));
                } else if (itemID == R.id.noti) {
                    startActivity(new Intent(ProfileActivity.this, Notification.class));
                } else if (itemID == R.id.setting) {
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                } else if (itemID == R.id.search) {
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
