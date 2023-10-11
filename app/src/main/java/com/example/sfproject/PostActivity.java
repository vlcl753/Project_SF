package com.example.sfproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import org.w3c.dom.Comment;

public class PostActivity extends AppCompatActivity {
    private EditText editTextComment;
    private Button buttonPostComment;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;

    // Firebase
    private DatabaseReference commentsRef;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        editTextComment = findViewById(R.id.Comments);
        buttonPostComment = findViewById(R.id.buttonPostComment);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);

        // Firebase 초기화
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        commentsRef = firebaseDatabase.getReference("comments");
        firebaseAuth = FirebaseAuth.getInstance();

        // RecyclerView 설정
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(); // 이 부분에서 댓글 목록을 표시하는 어댑터를 만들어야 합니다.
        recyclerViewComments.setAdapter(commentAdapter);

        buttonPostComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        // Firebase에서 댓글 데이터를 읽어옵니다.
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 데이터 변경 시 댓글 목록을 업데이트합니다.
                commentAdapter.updateComments(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 에러 처리
            }
        });
    }

    private void postComment() {
        String commentText = editTextComment.getText().toString().trim();
        if (!commentText.isEmpty()) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            String timestamp = String.valueOf(System.currentTimeMillis()); // 현재 시간을 timestamp로 사용

            DatabaseReference newCommentRef = commentsRef.push();
            Comment newComment = new Comment(commentText, userId, timestamp);
            newCommentRef.setValue(newComment);

            editTextComment.setText(""); // 입력 필드 비우기
        }
    }
}
