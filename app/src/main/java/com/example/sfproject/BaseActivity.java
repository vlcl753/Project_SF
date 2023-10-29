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
        ImageView imageView = createProfileImageView();

        // LinearLayout에 ImageView 추가
        linearLayout.addView(imageView);

        // 생성한 LinearLayout을 CardView에 추가
        cardView.addView(linearLayout);

        // CardView를 원하는 뷰 그룹에 추가
        ViewGroup rootView = findViewById(R.id.profile_post); // 여기서 "profile_post"는 CardView의 ID입니다.
        rootView.addView(cardView);

    }

    private static final int CARD_VIEW_RADIUS_DP = 8;
    private CardView createCardView() {
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 배경색 설정
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));

        // 테두리 반지름 값 설정 (픽셀 단위)
        int cardViewRadiusPixels = (int) (CARD_VIEW_RADIUS_DP * getResources().getDisplayMetrics().density);
        cardView.setRadius(cardViewRadiusPixels);

        // 그림자 설정
        cardView.setCardElevation(0);

        // CardView에 ID 설정
        cardView.setId(R.id.profile_post);

        // CardView 내부에 추가할 LinearLayout 생성 및 설정
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        // 여기에 원하는 레이아웃 구성 요소들을 추가할 수 있습니다.

        // CardView에 LinearLayout 추가
        cardView.addView(linearLayout);

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
