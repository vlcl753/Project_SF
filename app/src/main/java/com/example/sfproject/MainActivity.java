package com.example.sfproject;

import android.widget.ImageView;
import android.os.Bundle;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import com.google. android.material.navigation.NavigationBarView;
public class MainActivity extends AppCompatActivity {
    MainActivity mainActivity;
    Post_CreateActivity post_createActivity;
    ProfileActivity profileActivity;

    ViewFlipper v_fllipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int images[] = {
                R.drawable.test1,
                R.drawable.test2,
                R.drawable.test1
        };

        v_fllipper = findViewById(R.id.image_slide);

        for(int image : images) {
            fllipperImages(image);
        }
    }

    public void fllipperImages(int image) {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_fllipper.addView(imageView);
        v_fllipper.setFlipInterval(4000);
        v_fllipper.setAutoStart(true);

        // animation
        v_fllipper.setInAnimation(this,android.R.anim.slide_in_left);
        v_fllipper.setOutAnimation(this,android.R.anim.slide_out_right);
    }
}
