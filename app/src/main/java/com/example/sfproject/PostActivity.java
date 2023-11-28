package com.example.sfproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PostActivity extends AppCompatActivity {

    private ImageView postPic, postPic2, postPic3, imgProfile;
    private TextView txtPostContent, txtPostDate, txtPostTitle, txtPostName, postContent;
    private EditText editTextComment;
    private Button btnAddComment;
    private String PostKey = null;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private String currentUserId;
    private FirebaseFirestore firebaseFirestore;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmm_ssSSS");
    private String formattedDate;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private String USER_UID = null;
    private FirebaseFirestore firestore;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private String documentID = null;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        String postKey = getIntent().getStringExtra("Post_Key");

        Intent intent = getIntent();
        if (intent != null) {
            int imageNumber = intent.getIntExtra("IMAGE_NUMBER", -1);
            PostKey = "Post_" + imageNumber;
            System.out.println("받아버렸다" + imageNumber);
        }

        LinearLayout postLinearLayout = findViewById(R.id.post_LL);

        firestore = FirebaseFirestore.getInstance();
        setPostTitleFromFirestore();

        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Post")
                .whereEqualTo("Post_Key", PostKey)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                documentID = document.getId();
                                USER_UID = document.getString("Writer_User");
                                Log.d("TAG", "Post_1을 가진 문서의 Write_UID: " + USER_UID);
                                Log.d("TAG", "Post_1을 가진 문서의 document : " + documentID);

                                Timestamp timestamp = document.getTimestamp("Date");
                                if (timestamp != null) {
                                    Date date = timestamp.toDate();
                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                                    String formattedDate = formatter.format(date);

                                    TextView postDate = findViewById(R.id.postDate);
                                    postDate.setText(formattedDate);
                                }
                                Log.d("TAG", "Post_1을 가진 문서의 Date : " + formattedDate);
                            }
                        } else {
                            Log.d("TAG", "문서 조회 실패");
                        }

                        storage = FirebaseStorage.getInstance();
                        storageRef = storage.getReference().child("/Profile/" + USER_UID);

                        ImageView imgProfile = findViewById(R.id.img_profile);
                        loadFirebaseImage_profile(imgProfile, "Profile_Photo.jpg");

                        if (USER_UID != null && !USER_UID.isEmpty()) {
                            firestore.collection("Profile")
                                    .document(USER_UID)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String userName = documentSnapshot.getString("name");
                                            txtPostName.setText(userName);
                                        } else {
                                            Log.d("TAG", "해당 문서 없음");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("TAG", "문서를 가져오는 중 오류가 발생했습니다", e);
                                    });
                        } else {
                            Log.d("TAG", "USER_UID가 null 또는 비어 있습니다.");
                        }

                        fetchAndDisplayImages();
                        displayCommentsForPost(PostKey);
                    }
                });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = FirebaseAuth.getInstance().getUid();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        formattedDate = sdf.format(new Date());

        firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = findViewById(R.id.recyclerViewComments);
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
        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentToFirestore();
            }
        });
    }

    private void addCommentToFirestore() {
        String commentContent = editTextComment.getText().toString().trim();

        if (firebaseUser != null && !commentContent.isEmpty()) {
            String uid = firebaseUser.getUid();

            firebaseFirestore.collection("Profile")
                    .whereEqualTo("idToken", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uname = document.getString("name");
                                String uimg = document.getString("profileImageUrl");

                                Comment comment = new Comment(commentContent, uid, uimg, uname, PostKey);

                                firebaseFirestore.collection("comments")
                                        .add(comment)
                                        .addOnSuccessListener(documentReference -> {
                                            editTextComment.setText("");
                                        })
                                        .addOnFailureListener(e -> {
                                            // 댓글 추가 실패 시 처리
                                            // 실패 처리를 원하는대로 구현
                                        });
                            }
                        } else {
                            // 처리할 내용이 없을 경우
                        }
                    });
        }
    }

    private void fetchAndDisplayImages() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("MainPost_images/" + documentID);

        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    ImageView imageView = new ImageView(PostActivity.this);
                    Glide.with(PostActivity.this)
                            .load(uri)
                            .into(imageView);

                    LinearLayout postLinearLayout = findViewById(R.id.post_LL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, 20);
                    imageView.setLayoutParams(layoutParams);
                    postLinearLayout.addView(imageView);
                }).addOnFailureListener(exception -> {
                    Log.e("ImageLoad", "(fetchAndDisplayImages) 이미지 로드 실패: " + exception.getMessage());
                });
            }
        }).addOnFailureListener(exception -> {
            Log.e("FetchImages", "이미지 목록 가져오기 실패: " + exception.getMessage());
        });
    }

    private void loadFirebaseImage_profile(ImageView imageView, String imagePath) {
        StorageReference imageRef = storageRef.child(imagePath);

        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(PostActivity.this)
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(e -> {
            // 이미지 로드 실패 시 처리
            Log.e("ImageLoad", "(loadFirebaseImage_profile)이미지 로드 실패: " + e.getMessage());
        });
    }

    private void setPostTitleFromFirestore() {
        postContent = findViewById(R.id.postContent);

        firestore.collection("Post")
                .whereEqualTo("Post_Key", PostKey)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String postTitle = document.getString("title");
                        txtPostTitle.setText(postTitle);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "Error getting documents", e);
                });

        firestore.collection("Post")
                .whereEqualTo("Post_Key", PostKey)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String contentPost = document.getString("content");
                        postContent.setText(contentPost);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "Error getting documents", e);
                });
    }

    private void displayCommentsForPost(String postKey) {
        firestore.collection("comments")
                .whereEqualTo("postKey", postKey)
                .orderBy("timestamp", Query.Direction.DESCENDING) // timestamp 필드를 기준으로 내림차순 정렬
                // .orderBy("timestamp", Query.Direction.DESCENDING) = 최신 댓글 위에
                // .orderBy("timestamp", Query.Direction.ASCENDING) = 최신 댓글 아래
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.w("CommentData", "Listen failed.", e);
                        return;
                    }

                    List<Comment> comments = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Comment comment = document.toObject(Comment.class);
                        comments.add(comment);
                    }

                    // setCommentList 메서드 호출
                    commentAdapter.setCommentList(comments);
                });
    }

    public void goToMainActivity(View view) {
        finish();
    }

    public void openProfileActivity(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}