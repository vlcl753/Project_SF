package com.example.sfproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ListResult;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private Button profile_editbtn;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int NUM_COLUMNS = 3;
    private String userName; // 사용자 이름을 저장할 변수

    private FirebaseFirestore firestore;
    private static final String COLLECTION_NAME = "Profile";

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String USER_UID = currentUser.getUid();

    //String USER_UID ="teESJRTiV1Z7wO5eoA7SUkyI5U83";

    private TextView Profile_follow_num;
    private TextView Profile_following_num;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
                    finish();
                } else if (itemID == R.id.add) {
                    // 글쓰기 아이템을 클릭했을 때 Post_CreateActivity로 이동
                    startActivity(new Intent(ProfileActivity.this, Post_CreateActivity.class));
                    finish();
                } else if (itemID == R.id.setting) {
                    // 설정 아이템을 클릭했을 때 Profile_EditActivity로 이동
                    startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
                    finish();
                } else if (itemID == R.id.noti) {
                    // 알람 아이템을 클릭했을 때 Notification으로 이동
                    startActivity(new Intent(ProfileActivity.this, Notification.class));
                    finish();
                } else if (itemID == R.id.search) {
                    // 설정 아이템을 클릭했을 때 Profile_SearchActivity로 이동
                    startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
                    finish();
                }

                return false;
            }
        });

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("/Profile/" + USER_UID);

        firestore = FirebaseFirestore.getInstance();
        // 데이터 갯수 가져오기
        getTotalItemsCount();


        ImageView imgLogout = findViewById(R.id.img_logout);

        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 로그아웃 처리
                FirebaseAuth.getInstance().signOut();

                // 로그인 화면으로 이동 또는 필요한 다른 작업 수행
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // 현재 액티비티 종료
            }
        });

        Profile_follow_num = findViewById(R.id.textView_follow_num);
        Profile_following_num = findViewById(R.id.textView_following_num);

        DocumentReference follow_dc = firestore.collection("Profile").document(USER_UID);

        follow_dc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 문서 데이터에서 필요한 값을 가져오기
                        String follow_num = String.valueOf(document.getLong("follow"));
                        String following_num = String.valueOf(document.getLong("following"));
                        // 필요한 작업 수행
                        Profile_follow_num.setText(follow_num);
                        Profile_following_num.setText(following_num);
                    } else {
                        // 문서가 없는 경우 처리
                    }
                } else {
                    // 오류 처리
                }
            }
        });


        profile_editbtn = findViewById(R.id.profile_editbtn);
        profile_editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, Profile_EditActivity.class);
                startActivity(intent);
            }
        });

        loadUserName();
        // Firebase Storage에서 이미지 로드
        ImageView imgProfile = findViewById(R.id.img_profile);
        loadFirebaseImage_profile(imgProfile, "Profile_Photo.jpg");

        ImageView imgReport = findViewById(R.id.img_report);
        imgReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, reportActivity.class);
                startActivity(intent);
            }
        });

    }


    // ...


    @Override
    protected void onResume() {
        super.onResume();
        // 사용자 이름 다시 불러오기
        loadUserName();

        // 프로필 이미지 다시 불러오기
        ImageView imgProfile = findViewById(R.id.img_profile);
        loadFirebaseImage_profile(imgProfile, "Profile_Photo.jpg");

        // 기타 데이터를 다시 불러오는 작업을 추가할 수 있습니다.
        // 예를 들어, 사용자의 다른 정보를 불러올 수 있습니다.
    }

    private void loadUserName() {
        firestore.collection(COLLECTION_NAME)
                .document(USER_UID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // 사용자 이름을 가져와서 TextView에 설정
                            userName = documentSnapshot.getString("name");
                            TextView profileNameTextView = findViewById(R.id.profile_name);
                            profileNameTextView.setText(userName);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 사용자 이름 로드 실패 시 처리
                        Log.e("UserNameLoad", "사용자 이름 로드 실패: " + e.getMessage());
                    }
                });
    }


    private void loadFirebaseImage_profile(ImageView imageView, String imagePath) {
        // Firebase Storage에 있는 이미지의 StorageReference 가져오기
        StorageReference imageRef = storageRef.child(imagePath);

        // 이미지의 다운로드 URL을 얻어오기
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Glide를 사용하여 이미지 로드
                Glide.with(ProfileActivity.this)
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 이미지 로드 실패 시 처리
                Log.e("ImageLoad", "이미지 로드 실패: " + e.getMessage());
            }
        });
    }


    private void createCardViews(List<String> imagePaths) {
        CardView cardView = createCardView();

        LinearLayout parentLayout = new LinearLayout(this);
        LinearLayout.LayoutParams parentLayoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentLayout.setLayoutParams(parentLayoutParams);
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.setGravity(Gravity.CENTER);

        int numRows = (int) Math.ceil((double) imagePaths.size() / NUM_COLUMNS);

        for (int row = 0; row < numRows; row++) {
            LinearLayout rowLayout = new LinearLayout(this);
            LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLayout.setLayoutParams(rowLayoutParams);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int col = 0; col < NUM_COLUMNS; col++) {
                int index = row * NUM_COLUMNS + col;
                if (index < imagePaths.size()) {
                    rowLayout.addView(createCardView_1(imagePaths.get(index)));
                } else {
                    rowLayout.addView(createEmptyCardView());
                }
            }

            parentLayout.addView(rowLayout);
        }

        cardView.addView(parentLayout);
        ViewGroup rootView = findViewById(R.id.profile_post);
        rootView.addView(cardView);
    }

    private void getImagesFromStorage(List<Integer> imageNumbers) {
        final List<String> imagePaths = new ArrayList<>();
        for (int number : imageNumbers) {
            imagePaths.add("Profile_photo-" + number + ".jpg");
        }

        createCardViews(imagePaths);
    }

    private void getTotalItemsCount() {
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<Integer> imageNumbers = new ArrayList<>();
                for (StorageReference item : listResult.getItems()) {
                    String itemName = item.getName();
                    if (itemName.startsWith("Profile_photo-")) {
                        String[] parts = itemName.split("-");
                        String numWithExtension = parts[1];
                        String[] numParts = numWithExtension.split("\\.");
                        int number = Integer.parseInt(numParts[0]);
                        imageNumbers.add(number);
                    }
                }


                getImagesFromStorage(imageNumbers);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
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

    private CardView createCardView_1(String imagePath) {
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                dpToPixels(105), dpToPixels(105));
        layoutParams.setMargins(dpToPixels(10), dpToPixels(10), dpToPixels(10), dpToPixels(10));
        cardView.setLayoutParams(layoutParams);
        cardView.setCardElevation(0);
        cardView.setRadius(dpToPixels(20));

        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        imageView.setLayoutParams(params);

        loadFirebaseImage(imageView, imagePath);

        imageView.setClickable(true);
        imageView.setFocusable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        cardView.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int imageNumber = extractImageNumber(imagePath);
                Intent intent = new Intent(ProfileActivity.this, PostActivity.class);
                intent.putExtra("IMAGE_NUMBER", imageNumber);
                startActivity(intent);
            }
        });

        return cardView;
    }

    private int extractImageNumber(String imagePath) {
        // 파일 이름에서 숫자 부분 추출
        String[] parts = imagePath.split("-");
        String numWithExtension = parts[1];
        String[] numParts = numWithExtension.split("\\.");
        return Integer.parseInt(numParts[0]);
    }

    private void loadFirebaseImage(ImageView imageView, String imagePath) {
        // Firebase Storage에 있는 이미지의 StorageReference 가져오기
        StorageReference imageRef = storageRef.child(imagePath);

        // 이미지의 다운로드 URL을 얻어오기
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Glide를 사용하여 이미지 로드
                Glide.with(ProfileActivity.this)
                        .load(uri)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 이미지 로드 실패 시 처리
                Log.e("ImageLoad", "이미지 로드 실패: " + e.getMessage());
            }
        });
    }

    private int dpToPixels(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}