package com.example.sfproject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile_EditActivity extends AppCompatActivity {
    private EditText nicknameEditText;
    private FirebaseFirestore db;
    private DocumentReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        nicknameEditText = findViewById(R.id.nickname_edit);

        // Firestore 인스턴스를 가져옴
        db = FirebaseFirestore.getInstance();
        // "/Profile/ikZZTQIEEAetiZgPSFumXU1Cv3I3" 경로의 문서 참조를 가져옴
        profileRef = db.collection("Profile").document("ikZZTQIEEAetiZgPSFumXU1Cv3I3");

        // Firestore의 "nickname" 값이 변경되면 업데이트된 값을 EditText에 설정
        profileRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                // 에러 처리
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String nickname = documentSnapshot.getString("nickname");
                if (nickname != null) {
                    nicknameEditText.setText(nickname);
                }
            }
        });
    }

    public void yes_click(View v) {
        // "Yes" 버튼이 클릭되었을 때의 동작
        String newNickname = nicknameEditText.getText().toString();

        // "/Profile/ikZZTQIEEAetiZgPSFumXU1Cv3I3" 경로의 "nickname" 값을 업데이트
        profileRef.update("nickname", newNickname)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 성공적으로 업데이트된 경우
                        finish();
                    } else {
                        // 업데이트 중 에러 발생 시 동작
                    }
                });
    }

    public void no_click(View v) {
        // "No" 버튼이 클릭되었을 때의 동작
        finish();
    }
}