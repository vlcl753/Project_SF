package com.example.sfproject;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile_EditActivity extends AppCompatActivity {
    private EditText nicknameEditText;
    private FirebaseFirestore db;
    private DocumentReference profileRef;
    private StorageReference imageRef; // imageRef를 전역 변수로 이동
    private String imagePath = "/Profile/ikZZTQIEEAetiZgPSFumXU1Cv3I3/Profile_photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        ImageView imgProfile = findViewById(R.id.img_profile);

        // Firebase Storage에 있는 이미지의 StorageReference 가져오기
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imageRef = storageRef.child(imagePath);

        // 이미지 로드
        loadFirebaseImage_profile(imgProfile);

        nicknameEditText = findViewById(R.id.nickname_edit);

        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profile").document("ikZZTQIEEAetiZgPSFumXU1Cv3I3");

        // Firestore에서 프로필 정보 가져오기
        profileRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                // 에러 처리
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String nickname = documentSnapshot.getString("name");
                if (nickname != null) {
                    nicknameEditText.setText(nickname);
                }
            }
        });
    }

    private void loadFirebaseImage_profile(ImageView imageView) {
        // 이미지의 다운로드 URL을 얻어오기
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Glide를 사용하여 이미지 로드
            Glide.with(Profile_EditActivity.this)
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(e -> {
            // 이미지 로드 실패 시 처리
            Log.e("ImageLoad", "이미지 로드 실패: " + e.getMessage());
        });
    }

    public void yes_click(View v) {
        // "Yes" 버튼이 클릭되었을 때의 동작

        String newNickname = nicknameEditText.getText().toString();

        // Firestore에 닉네임 업데이트
        profileRef.update("name", newNickname)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 성공적으로 업데이트되면 액티비티 종료
                        finish();
                    } else {
                        // 실패 시 추가 처리
                    }
                });
    }

    public void no_click(View v) {
        // "No" 버튼이 클릭되었을 때의 동작
        finish();
    }
}
