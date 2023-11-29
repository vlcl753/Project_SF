package com.example.sfproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

public class Profile_EditActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText nicknameEditText;
    private FirebaseFirestore db;
    private DocumentReference profileRef;
    private StorageReference imageRef;

    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String User_UID = currentUser.getUid();

    private String imagePath = "/Profile/" + User_UID + "/Profile_Photo.jpg" ;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);


        ImageView imgProfile = findViewById(R.id.img_profile);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imageRef = storageRef.child(imagePath);
        loadFirebaseImage_profile(imgProfile);

        nicknameEditText = findViewById(R.id.nickname_edit);

        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profile").document(User_UID);

        profileRef.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String nickname = documentSnapshot.getString("name");
                if (nickname != null) {
                    nicknameEditText.setText(nickname);
                }
            }
        });

        TextView profileNameEdit = findViewById(R.id.profile_name_edit);

        profileNameEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        TextView profileDelTextView = findViewById(R.id.profile_del);

        profileDelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopup();
            }
        });

    }

    private void loadFirebaseImage_profile(ImageView imageView) {
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(Profile_EditActivity.this)
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(e -> {
            Log.e("ImageLoad", "이미지 로드 실패: " + e.getMessage());
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            setProfileImage(selectedImageUri);
        }
    }

    private void setProfileImage(Uri imageUri) {
        ImageView imgProfile = findViewById(R.id.img_profile);

        Glide.with(Profile_EditActivity.this)
                .load(imageUri)
                .into(imgProfile);
    }

    public void yes_click(View v) {
        String newNickname = nicknameEditText.getText().toString();

        if (selectedImageUri != null) {
            uploadImageToFirebaseStorage(newNickname);
        } else {
            updateNicknameOnly(newNickname);
        }
    }

    private void updateNicknameOnly(String newNickname) {
        profileRef.update("name", newNickname)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        finish();
                    } else {
                        Log.e("FirestoreUpdate", "Firestore 업데이트 실패: " + task.getException().getMessage());
                    }
                });
    }

    private void uploadImageToFirebaseStorage(String newNickname) {
        StorageReference newImageRef = FirebaseStorage.getInstance().getReference().child(imagePath);
        newImageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    newImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profileRef.update("profileImageUrl", uri.toString())
                                .addOnCompleteListener(this, task -> {
                                    if (task.isSuccessful()) {
                                        updateNicknameOnly(newNickname); // 닉네임 업데이트
                                    } else {
                                        // Handle failure
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e("ImageUpload", "이미지 업로드 실패: " + e.getMessage());
                });
    }

    public void no_click(View v) {
        finish();
    }


    private void showPopup() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("회원 탈퇴 하시겠습니까?");

        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteProfileAndPosts(User_UID);

                Log.d("DeleteUser", "여기까지!");
                System.out.println("여기까지");

                FirebaseAuth.getInstance().signOut();

                currentUser.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("DeleteUser", "사용자 삭제 성공!");
                            } else {
                                Log.e("DeleteUser", "사용자 삭제 실패: " + task.getException().getMessage());
                            }
                        });

                Intent intent = new Intent(Profile_EditActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }



    private void deleteProfileAndPosts(String userUID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection("Profile").document(userUID)
                .delete()
                .addOnSuccessListener(aVoid -> Log.d("Delete", "프로필 문서 삭제 완료"))
                .addOnFailureListener(e -> Log.e("Delete", "프로필 문서 삭제 실패: " + e.getMessage()));

        db.collection("Post")
                .whereEqualTo("Write_UID", userUID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String postKey = document.getId(); // Post_Key 가져오기
                            db.collection("Post").document(postKey)
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Delete", "포스트 문서 삭제 완료"))
                                    .addOnFailureListener(e -> Log.e("Delete", "포스트 문서 삭제 실패: " + e.getMessage()));
                        }
                    } else {
                        Log.e("Delete", "Error getting documents: ", task.getException());
                    }
                });

        db.collection("comments")
                .whereEqualTo("uid", userUID)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            db.collection("comments").document(document.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> Log.d("Delete", "코멘트 문서 삭제 완료"))
                                    .addOnFailureListener(e -> Log.e("Delete", "코멘트 문서 삭제 실패: " + e.getMessage()));
                        }
                    } else {
                        Log.e("Delete", "Error getting documents: ", task.getException());
                    }
                });

        String folderPath = "Profile/" + User_UID;

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderPath);

        storageRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        fileRef.delete()
                                .addOnSuccessListener(aVoid -> {

                                    Log.d("DeleteFile", "파일 삭제 성공!");
                                })
                                .addOnFailureListener(e -> {

                                    Log.e("DeleteFile", "파일 삭제 실패: " + e.getMessage());
                                });
                    }

                })
                .addOnFailureListener(e -> {

                    Log.e("ListFiles", "파일 목록 가져오기 실패: " + e.getMessage());
                });


    }



}


