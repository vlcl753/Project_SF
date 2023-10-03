package com.example.sfproject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        Button saveButton = findViewById(R.id.saveButton);
        final TextView textView = findViewById(R.id.textView);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textData = textView.getText().toString();

                // Firestore에 저장할 데이터 맵 생성
                Map<String, Object> data = new HashMap<>();
                data.put("content", textData);

                // Firestore에 데이터 쓰기
                db.collection("MainPost")
                        .document("NewDocumentID") // 새 문서 ID 또는 기존 문서 ID를 지정할 수 있습니다.
                        .set(data)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // 성공적으로 저장되었을 때 처리
                                // 예: Toast 메시지 또는 성공 다이얼로그 표시
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // 저장 실패시 처리
                                // 예: 오류 다이얼로그 표시
                            }
                        });
            }
        });
    }
}