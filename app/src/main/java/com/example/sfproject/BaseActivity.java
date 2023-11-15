package com.example.sfproject;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ListResult;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int NUM_COLUMNS = 3;

    String USER_UID = "ikZZTQIEEAetiZgPSFumXU1Cv3I3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("/Profile/" + USER_UID);

        // 데이터 갯수 가져오기
        getTotalItemsCount();
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
                Glide.with(BaseActivity.this)
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

    private int dpToPixels(float dp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
