package com.example.sfproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ListResult;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileActivity extends AppCompatActivity {

    private Button profile_editbtn;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int NUM_COLUMNS = 3;
    private String userName; // 사용자 이름을 저장할 변수

    private FirebaseFirestore firestore;
    private static final String COLLECTION_NAME = "Profile";
    String USER_UID = "ikZZTQIEEAetiZgPSFumXU1Cv3I3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("/Profile/" + USER_UID);

        firestore = FirebaseFirestore.getInstance();
        // 데이터 갯수 가져오기
        getTotalItemsCount();

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
        loadFirebaseImage_profile(imgProfile, "Profile_photo.jpg");

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
        loadFirebaseImage_profile(imgProfile, "Profile_photo.jpg");

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
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentLayout.setLayoutParams(parentLayoutParams);
        parentLayout.setOrientation(LinearLayout.VERTICAL);
        parentLayout.setGravity(Gravity.CENTER);

        LinearLayout rowLayout = null;
        for (int i = 0; i < imagePaths.size(); i++) {
            if (i % NUM_COLUMNS == 0) {
                rowLayout = new LinearLayout(this);
                LinearLayout.LayoutParams rowLayoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rowLayout.setLayoutParams(rowLayoutParams);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                parentLayout.addView(rowLayout);
            }
            rowLayout.addView(createCardView_1(imagePaths.get(i)));
        }

        int emptySpaces = NUM_COLUMNS - (imagePaths.size() % NUM_COLUMNS);
        for (int i = 0; i < emptySpaces; i++) {
            rowLayout.addView(createEmptyCardView());
        }

        cardView.addView(parentLayout);
        ViewGroup rootView = findViewById(R.id.profile_post);
        rootView.addView(cardView);
    }

    private void getImagesFromStorage(int totalPosts) {
        final List<String> imagePaths = new ArrayList<>();
        for (int i = 1; i <= totalPosts; i++) {
            imagePaths.add("Profile_photo-" + i + ".jpg");
        }

        createCardViews(imagePaths);
    }

    private void getTotalItemsCount() {
        storageRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<StorageReference> items = listResult.getItems();
                int totalPosts = items.size();
                Log.d("StorageItemCount", "데이터 갯수: " + totalPosts);
                getImagesFromStorage(totalPosts);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 데이터 갯수 가져오기 실패 시 처리할 작업을 여기에 추가하세요.
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

        // Firebase Storage에서 이미지 로드
        loadFirebaseImage(imageView, imagePath);

        imageView.setClickable(true);
        imageView.setFocusable(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        cardView.addView(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시 ProfileActivity로 이동하는 코드 추가
                Intent intent = new Intent(ProfileActivity.this, Profile_EditActivity.class);
                startActivity(intent);
            }
        });

        return cardView;
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