package com.example.sfproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirestore;
    private EditText mEtEmail, mEtPwd, mEtName;
    private Button mBtnRegister;
    private String profileImageUrl;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtName = findViewById(R.id.et_name);
        mBtnRegister = findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                String strName = mEtName.getText().toString();

                mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userId = mFirebaseAuth.getCurrentUser().getUid();

                            UserAccount account = new UserAccount();
                            account.setIdToken(userId);
                            account.setEmailId(strEmail);
                            account.setPassword(strPwd);
                            account.setName(strName);

                            // Firestore에 계정 정보 저장
                            mFirestore.collection("Profile").document(userId)
                                    .set(account)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show();
                                            Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(loginIntent);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(RegisterActivity.this, "Firestore에 계정 정보 저장 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void onProfileImageClick(View view) {
        chooseImageFromGallery();
    }

    private void chooseImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // 이미지뷰에 이미지 설정
            ImageView imageView = findViewById(R.id.profileImageView);
            imageView.setImageURI(selectedImageUri);

            // Firebase Storage에 이미지 업로드
            uploadImageToFirebase(selectedImageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        if (userId != null) {
            final String imageName = userId + ".jpg";

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference().child("profile_images").child(imageName);

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // 이미지 업로드 성공 시 이미지 URL을 얻어오기
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri downloadUri) {
                                            // 이미지 URL을 가져옴
                                            String profileImageUrl = downloadUri.toString();

                                            // Firestore에 이미지 URL 저장
                                            DocumentReference userDocRef = mFirestore.collection("UserAccount").document(userId);
                                            userDocRef.update("profileImageUrl", profileImageUrl)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // 이미지 URL 업데이트 성공
                                                            Toast.makeText(RegisterActivity.this, "프로필 사진이 업로드되었습니다", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // 이미지 URL 업데이트 실패
                                                            Toast.makeText(RegisterActivity.this, "프로필 사진 URL 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 이미지 URL을 가져오지 못함
                                            Toast.makeText(RegisterActivity.this, "프로필 사진 URL을 가져오는데 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 이미지 업로드 실패
                            Toast.makeText(RegisterActivity.this, "프로필 사진 업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(RegisterActivity.this, "Firebase 사용자 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }



}
