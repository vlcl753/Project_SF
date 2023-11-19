package com.example.sfproject;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PostActivity extends AppCompatActivity {

    ImageView postPic, postPic2, postPic3, imgProfile;
    TextView txtPostContent, txtPostDate, txtPostTitle, txtPostName;

    EditText editTextComment;
    Button btnAddComment;
    String PostKey;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    FirebaseFirestore firebaseFirestore;
    SimpleDateFormat sdf =  new SimpleDateFormat("yyyy_MMdd_HHmm_ssSSS");
    String formattedDate;
    CommentAdapter commentAdapter;
    List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // 한국 시간대로 설정
        formattedDate = sdf.format(new Date());


        firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewComments); // 여기에 XML에서 정의한 ID를 넣으세요
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentAdapter);

        btnAddComment = findViewById(R.id.sendButton);
        editTextComment = findViewById(R.id.CommentEdt);

        txtPostTitle = findViewById(R.id.postTitle);
        txtPostContent = findViewById(R.id.postContent);
        txtPostDate = findViewById(R.id.postDate);
        txtPostName = findViewById(R.id.profile_name);

        imgProfile = findViewById(R.id.img_profile);
        postPic = findViewById(R.id.postPic);
        postPic2 = findViewById(R.id.postPic2);
        postPic3 = findViewById(R.id.postPic3);

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentToFirestore();
            }
        });
    }

    private void addCommentToFirestore() {
        String commentContent = editTextComment.getText().toString().trim();

        if (!commentContent.isEmpty()) {
            // Comment 객체 생성
            Comment comment = new Comment(commentContent, currentUserId, "유저 프로필사진 URL", "유저 이름");

            // Firestore에 댓글 추가
            firebaseFirestore.collection("comments")
                    .add(comment)
                    .addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            // 댓글 추가 성공 시
                            editTextComment.setText(""); // 입력란 초기화
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 댓글 추가 실패 시
                            // 실패 처리를 원하는대로 구현
                        }
                    });
        }
    }


    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void openProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}




