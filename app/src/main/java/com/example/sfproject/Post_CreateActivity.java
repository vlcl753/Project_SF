package com.example.sfproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Post_CreateActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private EditText titleEditText, contentEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        titleEditText = findViewById(R.id.title_et);
        contentEditText = findViewById(R.id.content_et);

        Button regButton = findViewById(R.id.reg_button);
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();

                // 현재 날짜와 시간을 기반으로한 문서 이름 생성
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmm_ssSSS");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                String documentName = "MainPost-" + sdf.format(new Date());

                // Firestore에 저장할 데이터 맵 생성
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("content", content);

                // Firebase Firestore에 사용자가 정의한 문서 이름으로 데이터 쓰기
                db.collection("Post")
                        .document(documentName)
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

                finish();
            }



        ////
        });
    }
}
