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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.ListResult;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestore;

public class TestActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private String uid = "ikZZTQIEEAetiZgPSFumXU1Cv3I3"; // 해당 유저의 UID로 교체
    private String collectionName = "Profile"; // 컬렉션 이름으로 교체

    private TextView Profile_follow_num;
    private TextView Profile_following_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Profile_follow_num = findViewById(R.id.textView_follow_num);
        Profile_following_num = findViewById(R.id.textView_following_num);

        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(collectionName).document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
    }
}