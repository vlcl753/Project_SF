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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    // Firebase 데이터베이스 인스턴스를 가져옵니다.

    ViewFlipper v_fllipper;
    LinearLayout parentLayout;

    private BottomNavigationView bottomNavigationView;

    public void goToSearchActivity(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else if (itemID == R.id.search) {
                    // 설정 아이템을 클릭했을 때 Profile_SearchActivity로 이동
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                }

                return false;
            }
        });

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
    // Update UI with posts and profile information
    private void updateUIWithNumberOfPosts(int numOfPosts, QuerySnapshot querySnapshot) {
        LinearLayout mainPostsLayout = findViewById(R.id.mainPosts);
        boolean isLastSingle = numOfPosts % 2 != 0; // Check if the last post is single

        int iterations = isLastSingle ? (numOfPosts - 1) : numOfPosts;

        for (int i = 0; i < iterations; i += 2) {
            LinearLayout rowLayout = createRowLayout();

            // 1st post (left)
            if (i < querySnapshot.size()) {
                String writeUID = querySnapshot.getDocuments().get(i).getString("Writer_User");
                fetchProfileDataAndUpdateUI(writeUID, i, querySnapshot, rowLayout);
            }

            // 2nd post (right)
            if (i + 1 < querySnapshot.size()) {
                String writeUID = querySnapshot.getDocuments().get(i + 1).getString("Writer_User");
                fetchProfileDataAndUpdateUI(writeUID, i + 1, querySnapshot, rowLayout);
            } else if (i + 1 == querySnapshot.size() && isLastSingle) {
                LinearLayout emptyColumnLayout = createEmptyColumnLayout();
                rowLayout.addView(emptyColumnLayout);
            }

            mainPostsLayout.addView(rowLayout);
        }
    }

    // Fetch profile data and update UI
    private void fetchProfileDataAndUpdateUI(String postKey, int postIndex, QuerySnapshot querySnapshot, LinearLayout rowLayout) {
        String writerUser = querySnapshot.getDocuments().get(postIndex).getString("Writer_User");
        if (writerUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference profileRef = db.collection("Profile").document(writerUser);

            profileRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String profileImageUrl = document.getString("profileImageUrl");
                            String name = document.getString("name");
                            String contentImageUrl = querySnapshot.getDocuments().get(postIndex).getString("URL(1)");
                            String title = querySnapshot.getDocuments().get(postIndex).getString("title");
                            String fetchedPostKey = querySnapshot.getDocuments().get(postIndex).getString("Post_Key");

                            // fetchedPostKey 값을 createColumnLayout 메서드로 전달
                            LinearLayout columnLayout = createColumnLayout(fetchedPostKey, profileImageUrl, name, contentImageUrl, title);
                            rowLayout.addView(columnLayout);

                            columnLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 게시물을 클릭했을 때 PostActivity로 postKey 전달
                                    Intent intent = new Intent(MainActivity.this, PostActivity.class);
                                    intent.putExtra("Post_Key", fetchedPostKey);
                                    startActivity(intent);
                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "Error fetching profile data: ", task.getException());
                    }
                }
            });
        } else {
            Log.d(TAG, "Writer User ID is null");
        }
    }






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

    private LinearLayout createColumnLayout(String postKey, String profileImageResource, String profileName,
                                            String contentImageResource, String contentTitle) {
        LinearLayout columnLayout = new LinearLayout(this);
        columnLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1));
        columnLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout postLayout = createPostLayout(postKey, contentImageResource, profileImageResource, profileName, contentTitle); // postKey 값을 createPostLayout로 전달
        columnLayout.addView(postLayout);

        // 게시물 레이아웃 클릭 이벤트 핸들러 설정
        columnLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 게시물을 클릭했을 때 PostActivity로 postKey 전달
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                intent.putExtra("Post_Key", postKey);
                startActivity(intent);
            }
        });

        return columnLayout;
    }

    // 게시물 레이아웃 생성
    private LinearLayout createPostLayout(String postKey, String contentImageUrl, String profilePhotoUrl,
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
