package com.example.sfproject;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    // Firebase 데이터베이스 인스턴스를 가져옵니다.

    ViewFlipper v_fllipper;
    LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);

        // 레이아웃 및 뷰 참조를 가져옵니다
        v_fllipper = findViewById(R.id.image_slide);
        parentLayout = findViewById(R.id.mainPosts);

        // 이미지 슬라이드 설정
        int images[] = {
                R.drawable.banner3,
                R.drawable.banner2,
                R.drawable.banner1
        };
        for(int image : images) {
            fllipperImages(image);
        }

        // 동적으로 게시물 레이아웃 생성
        getNumberOfPosts();


    }

    private void getNumberOfPosts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference postsRef = db.collection("Post");

        postsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int numOfPosts = task.getResult().size();
                    // 여기서 numOfPosts 변수를 이용하여 UI를 업데이트하거나 필요한 작업을 수행할 수 있습니다.
                    // 예를 들어, numOfPosts 값을 사용하여 레이아웃을 동적으로 생성하거나 다른 작업을 수행할 수 있습니다.
                    updateUIWithNumberOfPosts(numOfPosts, task.getResult());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }
    private void updateUIWithNumberOfPosts(int numOfPosts, QuerySnapshot querySnapshot) {
        LinearLayout mainPostsLayout = findViewById(R.id.mainPosts);
        boolean isLastSingle = numOfPosts % 2 != 0; // 마지막 게시물이 한 개인지 여부 확인

        int iterations = isLastSingle ? (numOfPosts - 1) : numOfPosts;

        for (int i = 0; i < iterations; i += 2) {
            LinearLayout rowLayout = createRowLayout();

            // 1번째 게시물 (왼쪽)
            if (i < querySnapshot.size()) {
                // Firestore에서 데이터 가져오기
                String profilePhotoUrl = querySnapshot.getDocuments().get(i).getString("Profile_photo");
                String uName = querySnapshot.getDocuments().get(i).getString("uName");
                String contentImageUrl = querySnapshot.getDocuments().get(i).getString("URL(1)");
                String title = querySnapshot.getDocuments().get(i).getString("title");

                LinearLayout columnLayoutLeft = createColumnLayout(profilePhotoUrl, uName, contentImageUrl, title);
                rowLayout.addView(columnLayoutLeft);
            }

            // 2번째 게시물 (오른쪽)
            if (i + 1 < querySnapshot.size()) {
                // Firestore에서 데이터 가져오기
                String profilePhotoUrl = querySnapshot.getDocuments().get(i + 1).getString("Profile_photo");
                String uName = querySnapshot.getDocuments().get(i + 1).getString("uName");
                String contentImageUrl = querySnapshot.getDocuments().get(i + 1).getString("URL(1)");
                String title = querySnapshot.getDocuments().get(i + 1).getString("title");

                LinearLayout columnLayoutRight = createColumnLayout(profilePhotoUrl, uName, contentImageUrl, title);
                rowLayout.addView(columnLayoutRight);
            } else if (i + 1 == querySnapshot.size() && isLastSingle) {
                // 마지막 게시물이 한 개이고, 현재가 마지막 행인 경우 우측에 빈 칸 추가
                LinearLayout emptyColumnLayout = createEmptyColumnLayout();
                rowLayout.addView(emptyColumnLayout);
            }

            mainPostsLayout.addView(rowLayout);
        }
    }
    // 이미지 슬라이드 설정


    // 좌우 컬럼 레이아웃 생성
    private LinearLayout createEmptyColumnLayout() {
        LinearLayout emptyColumnLayout = new LinearLayout(this);
        emptyColumnLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        emptyColumnLayout.setOrientation(LinearLayout.VERTICAL);
        // 빈 칸으로 남겨줄 공간의 레이아웃입니다.
        return emptyColumnLayout;
    }
    // 좌우 컬럼 레이아웃 생성
    private LinearLayout createRowLayout() {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        return rowLayout;
    }

    private LinearLayout createColumnLayout(String profileImageResource, String profileName,
                                            String contentImageResource, String contentTitle) {
        LinearLayout columnLayout = new LinearLayout(this);
        columnLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        columnLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout postLayout = createPostLayout(contentImageResource, profileImageResource,
                profileName, contentTitle);
        columnLayout.addView(postLayout);

        return columnLayout;
    }

    // 게시물 레이아웃 생성
    private LinearLayout createPostLayout(String contentImageUrl, String profilePhotoUrl,
                                          String uName, String title) {
        LinearLayout postLayout = new LinearLayout(this);
        postLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        postLayout.setOrientation(LinearLayout.VERTICAL);

        // CardView 생성
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.setMargins(dpToPx(12), dpToPx(6), dpToPx(12), dpToPx(6));
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(dpToPx(20));
        cardView.setCardElevation(dpToPx(2)); // 카드 그림자 설정

        // CardView 내부에 게시물 이미지와 프로필 정보를 담을 LinearLayout 추가
        LinearLayout innerLayout = new LinearLayout(this);
        innerLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        innerLayout.setOrientation(LinearLayout.VERTICAL);

        // 게시물 이미지와 프로필 정보를 포함하는 FrameLayout
        FrameLayout imageProfileLayout = new FrameLayout(this);
        FrameLayout.LayoutParams imageProfileParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        imageProfileLayout.setLayoutParams(imageProfileParams);

        // 게시물 이미지
        // 게시물 이미지
        ImageView postImage = new ImageView(this);
        FrameLayout.LayoutParams imageParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                dpToPx(220));
        postImage.setLayoutParams(imageParams);
        postImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

// Firebase Storage에서 이미지를 가져와서 이미지뷰에 설정하기
        if (contentImageUrl != null && !contentImageUrl.isEmpty()) {
            // Firebase Storage에서 이미지를 가져와서 이미지뷰에 설정
            Picasso.get().load(contentImageUrl).into(postImage);
        } else {
            // 만약 이미지 URL이 없는 경우 placeholder 이미지를 설정
            postImage.setImageResource(R.drawable.sjg05);
        }


        // 프로필 이미지와 이름을 담는 FrameLayout
        FrameLayout profileLayout = new FrameLayout(this);
        FrameLayout.LayoutParams profileParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        profileParams.gravity = Gravity.BOTTOM | Gravity.START;
        profileParams.leftMargin = dpToPx(10);
        profileLayout.setLayoutParams(profileParams);

        // 프로필 이미지를 담는 MaterialCardView
        MaterialCardView materialCardView = new MaterialCardView(this);
        FrameLayout.LayoutParams materialCardParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        materialCardView.setLayoutParams(materialCardParams);
        materialCardView.setStrokeWidth(dpToPx(3));
        materialCardView.setStrokeColor(Color.WHITE);
        materialCardView.setRadius(dpToPx(20));
        materialCardView.setCardElevation(0);

// 프로필 이미지
        ImageView profileImageView = new ImageView(this);
        FrameLayout.LayoutParams profileImageParams = new FrameLayout.LayoutParams(
                dpToPx(35),
                dpToPx(35));
        profileImageView.setLayoutParams(profileImageParams);

// Firebase Storage에서 프로필 이미지를 가져와서 이미지뷰에 설정하기
        if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
            // Firebase Storage에서 이미지를 가져와서 이미지뷰에 설정
            Picasso.get().load(profilePhotoUrl).into(profileImageView);
        } else {
            // 만약 이미지 URL이 없는 경우 기본 프로필 이미지를 설정
            profileImageView.setImageResource(R.drawable.default_profile_image);
        }

// 프로필 이미지를 MaterialCardView에 추가
        materialCardView.addView(profileImageView);

        // 사용자 이름을 표시하는 TextView
        TextView userName = new TextView(this);
        FrameLayout.LayoutParams userNameParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        userNameParams.leftMargin = dpToPx(45);
        userName.setLayoutParams(userNameParams);
        userNameParams.gravity = Gravity.CENTER_VERTICAL;
        // 사용자 이름 설정
        userName.setText(uName); // 실제 사용자 이름으로 교체
        userName.setTextColor(Color.WHITE);
        userName.setTypeface(userName.getTypeface(), Typeface.BOLD);

        // 프로필 이미지와 사용자 이름을 profileLayout에 추가
        profileLayout.addView(materialCardView);
        profileLayout.addView(userName);

        // 게시물 이미지와 프로필 정보를 imageProfileLayout에 추가
        imageProfileLayout.addView(postImage);
        imageProfileLayout.addView(profileLayout);

        // imageProfileLayout을 CardView에 추가
        cardView.addView(imageProfileLayout);

        // CardView를 postLayout에 추가
        postLayout.addView(cardView);

        // 게시물 제목을 표시하는 TextView
        TextView titleTextView = new TextView(this);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(dpToPx(20), dpToPx(8), 0, 0);
        titleTextView.setLayoutParams(titleParams);
        // 게시물 제목 설정
        titleTextView.setText(title); // 실제 게시물 제목으로 교체
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);

        // Interaction Layout
        LinearLayout interactionLayout = new LinearLayout(this);
        LinearLayout.LayoutParams interactionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        interactionLayout.setLayoutParams(interactionParams);
        interactionLayout.setOrientation(LinearLayout.HORIZONTAL);
        interactionLayout.setGravity(Gravity.CENTER);

        // 상호작용을 추가하는 부분은 여기에 작성

        // 게시물의 이미지와 프로필 정보, 게시물 제목, Interaction Layout을 postLayout에 추가
        postLayout.addView(titleTextView);
        postLayout.addView(interactionLayout);

        return postLayout;
    }

    // dp를 px로 변환하는 메소드
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    public void fllipperImages(int image) {
        ImageView imageView = new ImageView(this);
        imageView.setBackgroundResource(image);

        v_fllipper.addView(imageView);
        v_fllipper.setFlipInterval(2300);
        v_fllipper.setAutoStart(true);

        // 슬라이드 애니메이션
        v_fllipper.setInAnimation(this, android.R.anim.slide_in_left);
        v_fllipper.setOutAnimation(this, android.R.anim.slide_out_right);
    }
    // 실제 작성된 게시물 수 반환

}
