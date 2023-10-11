package com.example.sfproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class Post_CreateActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Button customButton, regButton , customButton_popup;
    private EditText titleEditText, contentEditText;
    private List<Uri> selectedImageUris = new ArrayList<>();
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);

        FirebaseApp.initializeApp(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();

        titleEditText = findViewById(R.id.title_et);
        contentEditText = findViewById(R.id.content_et);
        customButton = findViewById(R.id.customButton);
        regButton = findViewById(R.id.reg_button);
        customButton_popup = findViewById(R.id.customButton_popup);

        customButton_popup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Post_CreateActivity.this, Post_Create_popup.class);
                startActivity(intent);
            }
        });
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = titleEditText.getText().toString();
                String content = contentEditText.getText().toString();
                Map<String, Object> data = new HashMap<>();
                data.put("title", title);
                data.put("content", content);

                for (int i = 0; i < selectedImageUris.size(); i++) {
                    Uri imageUri = selectedImageUris.get(i);
                    uploadImageToStorage(imageUri, i, data);
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void uploadImageToStorage(Uri imageUri, final int index, final Map<String, Object> postData) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmm_ssSSS");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        final String documentName = "MainPost-" + sdf.format(new Date()) + "-" + index;

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            StorageReference imageRef = storageRef.child("MainPost_images/" + "Post-1/  "+ documentName + ".jpg");
            UploadTask uploadTask = imageRef.putBytes(imageData);

            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // 이미지 업로드 성공 시 이미지 다운로드 URL을 가져오기
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    postData.put("imageUrl(" + (index + 1) + ")", imageUrl);

                    if (index == selectedImageUris.size() - 1) {
                        // 마지막 이미지까지 업로드되었으면 Firestore에 데이터 저장
                        saveDataToFirestore(documentName, postData);
                    }
                });
            }).addOnFailureListener(e -> {
                // 이미지 업로드 실패 처리
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToFirestore(String documentName, Map<String, Object> postData) {
        db.collection("Post")
                .document(documentName)
                .set(postData)
                .addOnSuccessListener(aVoid -> {
                    // 성공적으로 저장되었을 때 처리
                    // 예: Toast 메시지 또는 성공 다이얼로그 표시
                    finish();
                })
                .addOnFailureListener(e -> {
                    // 저장 실패시 처리
                    // 예: 오류 다이얼로그 표시
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // 여러 이미지 선택 시
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImageUris.add(imageUri);
                    insertImageIntoEditText(imageUri);
                }
            } else if (data.getData() != null) {
                // 단일 이미지 선택 시
                Uri imageUri = data.getData();
                selectedImageUris.add(imageUri);
                insertImageIntoEditText(imageUri);
            }
        }
    }

    private void insertImageIntoEditText(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            SpannableString spannableString = new SpannableString(" ");
            ImageSpan imageSpan = new ImageSpan(this, bitmap);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            int cursorPosition = contentEditText.getSelectionStart();
            contentEditText.getText().insert(cursorPosition, spannableString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
