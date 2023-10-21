package com.example.sfproject;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.os.Bundle;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    public void goToPostActivity(View view) {
        Intent intent = new Intent(this, PostActivity.class);
        startActivity(intent);
    }
    private BottomNavigationView bottomNavigationView;

    public void goToSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    ViewFlipper v_fllipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int images[] = {
                R.drawable.banner3,
                R.drawable.banner2,
                R.drawable.banner1
        };

        v_fllipper = findViewById(R.id.image_slide);

        for(int image : images) {
            fllipperImages(image);
        }
        bottomNavigationView = findViewById(R.id.bottom_navigationview);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId(); // 아이템 ID를 가져옵니다.
                // 아이템 ID에 따라 액티비티를 시작합니다.
                int itemID = item.getItemId();
                if (itemID == R.id.home) {
                    // 홈 아이템을 클릭했을 때 MainActivity로 이동
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                } else if (itemID == R.id.add) {
                    // 글쓰기 아이템을 클릭했을 때 Post_CreateActivity로 이동
                    startActivity(new Intent(MainActivity.this, Post_CreateActivity.class));
                } else if (itemID == R.id.noti) {
                    // 알람 아이템을 클릭했을 때 Notification으로 이동
                    startActivity(new Intent(MainActivity.this, Notification.class));
                } else if (itemID == R.id.setting) {
                    // 설정 아이템을 클릭했을 때 Profile_EditActivity로 이동
                    startActivity(new Intent(MainActivity.this, Profile_EditActivity.class));
                } else if (itemID == R.id.search) {
                    // 설정 아이템을 클릭했을 때 Profile_EditActivity로 이동
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                }

                return false;
            }
        });





    }

    public void fllipperImages(int image) {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_fllipper.addView(imageView);
        v_fllipper.setFlipInterval(2300);
        v_fllipper.setAutoStart(true);

        // animation
        v_fllipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_fllipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }
}
