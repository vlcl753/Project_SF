package com.example.sfproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
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

    private ImageView postPic, postPic2, postPic3, imgProfile, menudots;
    private TextView txtPostContent, txtPostDate, txtPostTitle, txtPostName, postContent;
    private EditText editTextComment;
    private Button btnAddComment;
    private String PostKey = null;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = currentUser.getUid();

    //private String currentUserId;
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


        ImageView menudots = findViewById(R.id.menudots);
        menudots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });



        /*
        String postKey = getIntent().getStringExtra("Post_Key");

        Intent intent = getIntent();
        if (intent != null) {
            int imageNumber = intent.getIntExtra("IMAGE_NUMBER", -1);
            PostKey = "Post_" + imageNumber;
            System.out.println("받아버렸다" + imageNumber);
        }
         */


        Intent intent = getIntent();
        PostKey = getIntent().getStringExtra("Post_Key");

        if (intent != null) {
            int imageNumber = intent.getIntExtra("IMAGE_NUMBER", -1);
            PostKey = getIntent().getStringExtra("Post_Key"); // 기존에 선언된 클래스 변수를 사용
            
        }
        if (PostKey != null) {
            // postKey가 존재하는 경우, 이를 이용하여 원하는 작업 수행
            // 예: 해당 postKey를 사용하여 데이터베이스에서 해당 게시물의 정보를 가져오거나 다른 작업 수행
            Log.d("PostActivity", "Received Post_Key: " + PostKey);
        } else {
            // postKey가 존재하지 않는 경우, 에러 처리 또는 다른 작업 수행
            Log.d("PostActivity", "No Post_Key received");
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

    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림창")
                .setMessage("게시물을 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String collectionPath = "Post"; // Post 컬렉션 안에서 삭제할 것인지 지정합니다.
                        String documentIDToDelete = documentID; // 삭제할 문서의 아이디를 여기에 입력하세요

                        // 해당 documentID가 존재하는지 확인하고, 있다면 삭제합니다.
                        db.collection(collectionPath)
                                .document(documentIDToDelete)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                // 해당 documentID를 가진 문서가 존재하면 Writer_User 값을 가져와서 User_UID에 저장합니다.
                                                String userUID = document.getString("Writer_User");
                                                String postKey = document.getString("Post_Key");

                                                if (currentUser != null) {
                                                    String currentUserUID = currentUser.getUid();


                                                    String folderPath = "Profile/" + userUID + "/Profile_photo-" + postKey.substring(5) + ".jpg";

                                                    Log.d("DeleteFile", "현재 파일 위치 " + folderPath);

                                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderPath);

                                                    // Storage에서 파일 삭제
                                                    storageRef.delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    // 파일 삭제 성공 시 실행할 코드
                                                                    Log.d("DeleteFile", "파일 삭제 성공!");

                                                                    // 이후에 삭제 작업을 수행합니다.
                                                                    db.collection(collectionPath)
                                                                            .document(documentIDToDelete)
                                                                            .delete()
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // 삭제 성공 시 실행할 코드
                                                                                    Log.d("TAG", "문서 삭제 성공");
                                                                                    finish();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // 삭제 실패 시 실행할 코드
                                                                                    Log.w("TAG", "문서 삭제 실패", e);
                                                                                }
                                                                            });
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    // 파일 삭제 실패 시 실행할 코드
                                                                    Log.e("DeleteFile", "파일 삭제 실패: " + e.getMessage());
                                                                }
                                                            });
                                                } else {
                                                    // 해당 documentID를 가진 문서가 존재하지 않음
                                                    Log.d("TAG", "해당 documentID를 가진 문서가 존재하지 않습니다.");
                                                }
                                            } else {
                                                Log.d("TAG", "get failed with ", task.getException());
                                            }
                                        }
                                    }
                                });
                    }

                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 취소 버튼을 눌렀을 때 수행할 작업 추가
                        dialog.dismiss();
                    }
                })
                .show();
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