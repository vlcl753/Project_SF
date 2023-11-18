package com.example.sfproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class reportActivity extends AppCompatActivity {

    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private Button postReportBtn;

    FirebaseFirestore db;
    String USER_UID = "ikZZTQIEEAetiZgPSFumXU1Cv3I3";
    DocumentReference profileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        checkBox1 = findViewById(R.id.title_et_report1);
        checkBox2 = findViewById(R.id.title_et_report2);
        checkBox3 = findViewById(R.id.title_et_report3);
        checkBox4 = findViewById(R.id.title_et_report4);
        postReportBtn = findViewById(R.id.post_report_btn);

        db = FirebaseFirestore.getInstance();
        profileRef = db.collection("Profile").document(USER_UID);

        postReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int reportCount;

                if (checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked() || checkBox4.isChecked()) {
                    reportCount = 1;
                } else {
                    reportCount = 0;
                }

                // 현재의 report 값을 가져와서 reportCount 만큼 증가시켜 업데이트
                profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            int currentReportValue = documentSnapshot.getLong("report") != null ?
                                    Math.toIntExact((long) documentSnapshot.get("report")) : 0;
                            int updatedReportValue = currentReportValue + reportCount;

                            // Firestore의 report 필드 업데이트
                            profileRef.update("report", updatedReportValue)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // 업데이트 성공 시 처리할 작업
                                            Log.d("FirestoreUpdate", "Report 값 업데이트 완료");
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // 실패 시 처리할 작업
                                            Log.e("FirestoreUpdate", "Report 값 업데이트 실패: " + e.getMessage());
                                        }
                                    });
                        } else {
                            Log.d("FirestoreUpdate", "Document does not exist");
                        }
                    }
                });
            }
        });
    }
}
