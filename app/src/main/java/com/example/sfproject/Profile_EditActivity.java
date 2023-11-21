package com.example.sfproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
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
}
