package com.example.sfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        FirebaseApp.initializeApp(this);

        // Firestore 인스턴스 생성
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("MainPost").document("MainPost-1");

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 문서가 존재하는 경우 데이터 가져오기
                        String content = document.getString("content");
                        String date = document.getString("date");
                        String title = document.getString("title");
                        String userName = document.getString("userName");

                        // TextView 업데이트
                        TextView dataTextView = findViewById(R.id.dataTextView);
                        dataTextView.setText("Content: " + content + "\nDate: " + date + "\nTitle: " + title + "\nUserName: " + userName);
                    } else {
                        // 문서가 존재하지 않는 경우 처리
                        Log.d("FirestoreData", "문서가 존재하지 않습니다.");
                    }
                } else {
                    // 오류 처리
                    Log.d("FirestoreData", "데이터를 불러오는 중 오류가 발생했습니다.");
                }
            }
        });
    }
}
