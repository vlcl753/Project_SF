package com.example.sfproject;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // CardView와 LinearLayout 동적 생성
        CardView cardView = createCardView();
        LinearLayout linearLayout = createLinearLayout();

        // LinearLayout에 CardView 추가
        linearLayout.addView(createCardView_1(R.drawable.sjg01));
        linearLayout.addView(createCardView_1(R.drawable.sjg01));
        linearLayout.addView(createCardView_1(R.drawable.sjg01));
        linearLayout.addView(createCardView_1(R.drawable.sjg01));
        // 생성한 LinearLayout을 CardView에 추가
        cardView.addView(linearLayout);

        // CardView를 원하는 뷰 그룹에 추가
        ViewGroup rootView = findViewById(R.id.profile_post); // 여기서 "profile_post"는 CardView의 ID입니다.
        rootView.addView(cardView);

    }

    private static final int CARD_VIEW_RADIUS_DP = 8;
    private CardView createCardView() {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(layoutParams);

        // 카드뷰 속성 설정
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardView.setCardElevation(0); // 그림자 높이 설정

        return cardView;
    }


    private LinearLayout createLinearLayout() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(getResources().getColor(R.color.white)); // 배경색 설정

        return linearLayout;
    }

    private CardView createCardView_1(int imageResId) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPixels(105), dpToPixels(105)); // 너비와 높이를 dp 단위로 설정
        layoutParams.setMargins(dpToPixels(10), dpToPixels(10), dpToPixels(10), dpToPixels(10)); // 마진을 dp 단위로 설정
        cardView.setLayoutParams(layoutParams);

        // 카드뷰 속성 설정
        cardView.setCardElevation(0); // 그림자 높이 설정
        cardView.setRadius(dpToPixels(20)); // 테두리의 곡률 설정

        // ImageView 생성 및 설정
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);
        imageView.setImageResource(imageResId);
        imageView.setClickable(true);
        imageView.setFocusable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // ImageView를 CardView에 추가
        cardView.addView(imageView);

        return cardView;
    }

    // dp 값을 픽셀로 변환하는 메서드
    private int dpToPixels(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }



    private static final int CARD_VIEW_MARGIN_DP = 8;
    private ImageView createProfileImageView() {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        int marginPixels = (int) (CARD_VIEW_MARGIN_DP * getResources().getDisplayMetrics().density);
        params.setMargins(marginPixels, marginPixels, marginPixels, marginPixels);
        imageView.setLayoutParams(params);
        imageView.setImageResource(R.drawable.sjg01);
        imageView.setClickable(true);
        imageView.setFocusable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // 클릭 이벤트 등 다른 설정이 필요한 경우 여기에서 추가하세요.

        return imageView;
    }

}