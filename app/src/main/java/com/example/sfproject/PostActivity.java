package com.example.sfproject;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class PostActivity extends AppCompatActivity {

    ImageView postPic, postPic2, postPic3, imgProfile;
    TextView txtPostContent, txtPostDate, txtPostTitle, txtPostName, postContent;

    EditText editTextComment;
    Button btnAddComment;



    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;
    String currentUserId;
    FirebaseFirestore firebaseFirestore;
    SimpleDateFormat sdf =  new SimpleDateFormat("yyyy_MMdd_HHmm_ssSSS");
    String formattedDate;
    CommentAdapter commentAdapter;
    List<Comment> commentList;

    private String USER_UID=null;
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
            int imageNumber = intent.getIntExtra("IMAGE_NUMBER", -1); // -1은 기본값, 값이 없을 때 반환될 값 설정
            // 가져온 imageNumber를 PostKey 변수에 할당하거나 활용
            postKey = "Post_" + imageNumber; // 예시로 PostKey를 설정하는 방식
            System.out.println("받아버렸다"+imageNumber);
        }

        //PostKey ="Post_13";


        LinearLayout postLinearLayout = findViewById(R.id.post_LL);



        Log.d("TAG", "실행");




        firestore = FirebaseFirestore.getInstance();
        setPostTitleFromFirestore(postKey);

        firestore = FirebaseFirestore.getInstance();
        firestore.collection("Post")
                .whereEqualTo("Post_Key", postKey)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                documentID = document.getId(); // 문서의 이름(문서 ID) 가져오기
                                USER_UID = document.getString("Writer_User"); // Write_UID 필드의 데이터 가져오기
                                Log.d("TAG", "Post_1을 가진 문서의 Write_UID: " + USER_UID);
                                Log.d("TAG", "Post_1을 가진 문서의 document : " + documentID);

                                Timestamp timestamp = document.getTimestamp("Date");
                                if (timestamp != null) {
                                    Date date = timestamp.toDate();

                                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // 한국 시간대 설정
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

                        firestore.collection("Profile")
                                .document(USER_UID) // USER_UID에 해당하는 document 가져오기
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String userName = documentSnapshot.getString("name");
                                        txtPostName.setText(userName); // profile_name TextView에 이름 설정
                                    } else {
                                        Log.d("TAG", "No such document");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.d("TAG", "Error getting document", e);
                                });

                        fetchAndDisplayImages();
                    }
                });









        /* 댓글 */
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = FirebaseAuth.getInstance().getUid();

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
        /*
        postPic = findViewById(R.id.postPic);
        postPic2 = findViewById(R.id.postPic2);
        postPic3 = findViewById(R.id.postPic3);

         */

        /* 댓글 */

        btnAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCommentToFirestore();
            }
        });
    }



    /* 댓글 */
    private void addCommentToFirestore() {
        String commentContent = editTextComment.getText().toString().trim();

        // firebaseUser가 null이 아닌지 확인
        if (firebaseUser != null && !commentContent.isEmpty()) {
            String uid = firebaseUser.getUid();

            // Profile 컬렉션에서 idToken과 일치하는 문서 가져오기
            firebaseFirestore.collection("Profile")
                    .whereEqualTo("idToken", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String uname = document.getString("name");
                                String uimg = document.getString("profileImageUrl");

                                // Comment 객체 생성
                                Comment comment = new Comment(commentContent, uid, uimg, uname);

                                // Firestore에 댓글 추가
                                firebaseFirestore.collection("comments")
                                        .add(comment)
                                        .addOnSuccessListener(documentReference -> {
                                            // 댓글 추가 성공 시
                                            editTextComment.setText(""); // 입력란 초기화
                                        })
                                        .addOnFailureListener(e -> {
                                            // 댓글 추가 실패 시
                                            // 실패 처리를 원하는대로 구현
                                        });
                            }
                        } else {
                        }
                    });
        }

        /* 댓글 */

    }




    private void fetchAndDisplayImages() {
        // Firebase Storage의 폴더에 대한 참조
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("MainPost_images/" + documentID);

        // 해당 경로의 모든 파일 목록 가져오기
        storageRef.listAll().addOnSuccessListener(listResult -> {
            for (StorageReference item : listResult.getItems()) {
                // 각 이미지에 대한 다운로드 URL 가져오기
                item.getDownloadUrl().addOnSuccessListener(uri -> {
                    // 이미지를 나타내는 ImageView 생성
                    ImageView imageView = new ImageView(PostActivity.this);

                    // Glide 등을 사용하여 이미지 로드
                    Glide.with(PostActivity.this)
                            .load(uri)
                            .into(imageView);

                    // 이미지를 post_LL 레이아웃에 추가
                    LinearLayout postLinearLayout = findViewById(R.id.post_LL);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    layoutParams.setMargins(0, 0, 0, 20); // 이미지 간격 조정
                    imageView.setLayoutParams(layoutParams);
                    postLinearLayout.addView(imageView);
                }).addOnFailureListener(exception -> {
                    // 이미지 로드 실패 시 처리
                    Log.e("ImageLoad", "(fetchAndDisplayImages) 이미지 로드 실패: " + exception.getMessage());
                });
            }
        }).addOnFailureListener(exception -> {
            // 파일 목록 가져오기 실패 시 처리
            Log.e("FetchImages", "이미지 목록 가져오기 실패: " + exception.getMessage());
        });
    }






    private void loadFirebaseImage_profile(ImageView imageView, String imagePath) {
        StorageReference imageRef = storageRef.child(imagePath);


        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(PostActivity.this)
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // 이미지 로드 실패 시 처리
                Log.e("ImageLoad", "(loadFirebaseImage_profile)이미지 로드 실패: " + e.getMessage());
            }
        });
    }



    private void setPostTitleFromFirestore(String postKey) {
        postContent = findViewById(R.id.postContent);

        firestore.collection("Post")
                .whereEqualTo("Post_Key", postKey)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String postTitle = document.getString("title");
                        txtPostTitle.setText(postTitle); // postTitle TextView에 Title 설정
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "Error getting documents", e);
                });

        firestore.collection("Post")
                .whereEqualTo("Post_Key", postKey)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String Contentpost = document.getString("content");
                        postContent.setText(Contentpost);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "Error getting documents", e);
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




