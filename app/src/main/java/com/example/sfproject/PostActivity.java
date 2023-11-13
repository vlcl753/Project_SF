package com.example.sfproject;


import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PostActivity extends AppCompatActivity {

    ImageView postPic,postPic2,postPic3,imgProfile;
    TextView txtPostContent,txtPostDate,txtPostTitle,txtPostName;

    EditText editTextComment;
    Button btnAddComment;
    String PostKey;

    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    RecyclerView RvComment;
    CommentAdapter commentAdapter;
    List<Comment> listComment;
    static String COMMENT_KEY = "Comment" ;

    public void goToMainActivity(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void openProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    /*    RvComment = findViewById(R.id.recyclerViewComments);
        postPic = findViewById(R.id.postPic);
        postPic2 = findViewById(R.id.postPic2);
        postPic3 = findViewById(R.id.postPic3);
        imgProfile = findViewById(R.id.img_profile);

        txtPostTitle = findViewById(R.id.postTitle);
        txtPostContent = findViewById(R.id.postContent);
        txtPostDate = findViewById(R.id.postDate);
        txtPostName = findViewById(R.id.profile_name);

        editTextComment = findViewById(R.id.Comments);
        btnAddComment = findViewById(R.id.sendButton);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnAddComment.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                btnAddComment.setVisibility(View.INVISIBLE);
                DatabaseReference commentReference = firebaseDatabase.getReference("Comment").child(PostKey).push();
                String comment_content = editTextComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content,uid,uimg,uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("댓글 작성");
                        editTextComment.setText("");
                        btnAddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("댓글 작성 실패 : " +e.getMessage());
                    }
                });

            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String postImage = extras.getString("postImage");
            Glide.with(this).load(postImage).into(postPic);

            String postImage2 = extras.getString("postImage2");
            Glide.with(this).load(postImage2).into(postPic2);

            String postImage3 = extras.getString("postImage3");
            Glide.with(this).load(postImage3).into(postPic3);

            String postTitle = extras.getString("title");
            txtPostTitle.setText(postTitle);

            String userpostImage = extras.getString("userPhoto");
            Glide.with(this).load(userpostImage).into(imgProfile);

            String postDescription = extras.getString("description");
            txtPostContent.setText(postDescription);

            // get post id
            PostKey = extras.getString("postKey");

            String date = timestampToString(extras.getLong("postDate"));
            txtPostDate.setText(date);
        }


        iniRvComment();
    }

    private void iniRvComment() {
        RvComment.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference commentRef = firebaseDatabase.getReference(COMMENT_KEY).child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {

                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);

                }

                commentAdapter = new CommentAdapter(getApplicationContext(), listComment);
                RvComment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

            private void showMessage (String message){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    private String timestampToString(long time) {

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd",calendar).toString();
        return date;

     */
    }
}



