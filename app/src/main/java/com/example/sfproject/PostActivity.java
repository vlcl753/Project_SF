package com.example.sfproject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PostActivity extends AppCompatActivity {
    private EditText commentEditText;
    private Button addCommentButton;
    //private RecyclerView commentRecyclerView;
    private CommentAdapter commentAdapter;
    private ArrayList<Comment> commentsList;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        commentEditText = findViewById(R.id.Comments);
        addCommentButton = findViewById(R.id.sendButton);
        // commentRecyclerView = findViewById(R.id.recyclerViewComments);

        commentsList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentsList);
        //commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //commentRecyclerView.setAdapter(commentAdapter);

        // Load existing comments
        loadComments();

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentText = commentEditText.getText().toString();
                if (!commentText.isEmpty()) {
                    addComment(commentText);
                    commentEditText.setText("");
                }
            }
        });
    }

    private void loadComments() {
        db.collection("comments")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        // Handle the error
                        return;
                    }

                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            Comment comment = documentChange.getDocument().toObject(Comment.class);
                            commentsList.add(comment);
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void addComment(String commentText) {
        Map<String, Object> commentData = new HashMap<>();
        commentData.put("content", commentText);
        commentData.put("authorName", auth.getCurrentUser().getDisplayName());

        // You can add the timestamp here if needed

        db.collection("comments")
                .add(commentData)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            DocumentReference documentReference = task.getResult();
                            // Comment added successfully
                        } else {
                            // Handle the error
                        }
                    }
                });
    }
}