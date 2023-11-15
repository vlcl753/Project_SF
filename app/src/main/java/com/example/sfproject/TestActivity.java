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

public class TestActivity extends AppCompatActivity {

    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        storage = FirebaseStorage.getInstance();

        // Firebase Storage에서 이미지 파일 경로
        String filePath = "/Profile/ikZZTQIEEAetiZgPSFumXU1Cv3I3/Profile_photo-3.jpg";
        StorageReference imageRef = storage.getReference().child(filePath);

        // 이미지 다운로드 성공 또는 실패 여부에 대한 리스너 설정
        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // 이미지 다운로드 성공 시 작업을 수행할 수 있습니다.
                Log.d("ImageDownload", "이미지 다운로드 성공: " + uri.toString());

                // ImageView 생성
                ImageView imageView = new ImageView(TestActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                imageView.setLayoutParams(params);

                // Glide를 사용하여 이미지 로드
                Glide.with(TestActivity.this)
                        .load(uri) // 다운로드한 이미지의 Uri를 사용하여 로드합니다.
                        .placeholder(R.drawable.sjg01) // 로딩 중에 보일 이미지
                        .error(R.drawable.ijh_img1) // 이미지 로딩 실패 시 보일 이미지
                        .into(imageView);

                // CardView 생성
                CardView cardView = createCardView();
                cardView.addView(imageView);

                // CardView를 원하는 뷰 그룹에 추가
                ViewGroup rootView = findViewById(R.id.CdV); // 여기서 "profile_post"는 CardView를 추가할 LinearLayout의 ID입니다.
                rootView.addView(cardView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 이미지 다운로드 실패 시 작업을 수행할 수 있습니다.
                Log.e("ImageDownload", "이미지 다운로드 실패: " + e.getMessage());
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
}
