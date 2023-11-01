package com.example.sfproject;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class BaseActivity extends AppCompatActivity {
    private static final int NUM_COLUMNS = 3;
    private int totalPosts = 20; // 게시물의 총 갯수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // CardView 동적 생성
        CardView cardView = createCardView();

        // 전체 레이아웃 동적 생성
        LinearLayout parentLayout = new LinearLayout(this);
        LinearLayout.LayoutParams parentLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentLayout.setLayoutParams(parentLayoutParams);
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.setGravity(Gravity.CENTER);

        // 게시물의 총 갯수를 기준으로 이미지를 배치
        LinearLayout rowLayout = null;
        for (int i = 0; i < totalPosts; i++) {
            if (i % NUM_COLUMNS == 0) {
                // 새로운 줄 시작
                rowLayout = new LinearLayout(this);
                LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rowLayout.setLayoutParams(rowLayoutParams);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                parentLayout.addView(rowLayout);
            }
            rowLayout.addView(createCardView_1(R.drawable.sjg01));
        }

        // 남은 빈 칸을 빈 CardView로 채우기
        int emptySpaces = NUM_COLUMNS - (totalPosts % NUM_COLUMNS);
        for (int i = 0; i < emptySpaces; i++) {
            rowLayout.addView(createEmptyCardView());
        }

        // 생성한 전체 레이아웃을 CardView에 추가
        cardView.addView(parentLayout);

        // CardView를 원하는 뷰 그룹에 추가
        ViewGroup rootView = findViewById(R.id.profile_post); // 여기서 "profile_post"는 CardView를 추가할 LinearLayout의 ID입니다.
        rootView.addView(cardView);
    }

    private CardView createCardView() {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardView.setLayoutParams(layoutParams);
        cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
        cardView.setCardElevation(0);
        return cardView;
    }

    private CardView createEmptyCardView() {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPixels(105), dpToPixels(105));
        layoutParams.setMargins(dpToPixels(10), dpToPixels(10), dpToPixels(10), dpToPixels(10));
        cardView.setLayoutParams(layoutParams);
        cardView.setCardElevation(0);
        cardView.setRadius(dpToPixels(20));
        return cardView;
    }

    private CardView createCardView_1(int imageResId) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPixels(105), dpToPixels(105));
        layoutParams.setMargins(dpToPixels(10), dpToPixels(10), dpToPixels(10), dpToPixels(10));
        cardView.setLayoutParams(layoutParams);
        cardView.setCardElevation(0);
        cardView.setRadius(dpToPixels(20));

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

    private int dpToPixels(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
