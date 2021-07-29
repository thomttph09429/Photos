package com.poly.photos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MyAccountActivity extends AppCompatActivity {
    Button btnLogout, btnResetPw;
    TextView tvEmai, tvPhone, tvName, btnResendCode, tvMsg;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initViews();
        initAction();
        showInfor();
         btnResetPw.setOnClickListener((View v)->{
             ResetPwDialog dialog= new ResetPwDialog(this);
             dialog.show();

         });

        btnLogout.setOnClickListener((View v) -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            finish();

        });
    }

    public void initViews() {
        btnLogout = findViewById(R.id.btn_logout);
        tvEmai = findViewById(R.id.tv_email);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        btnResendCode = findViewById(R.id.tv_resend_code);
        tvMsg = findViewById(R.id.tv_verify_msg);
        btnResetPw = findViewById(R.id.btn_reset);
    }

    public void initAction() {

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

    }

    public void showInfor() {
        FirebaseUser user = auth.getCurrentUser();
        userID = auth.getCurrentUser().getUid();
        if (!user.isEmailVerified()) {
            btnResendCode.setVisibility(View.VISIBLE);
            tvMsg.setVisibility(View.VISIBLE);
            btnResendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification email has been sent", Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Reristor", "Onfailure: Email not sent" + e.getMessage());

                        }
                    });
                }
            });
        }

        DocumentReference docReference = firestore.collection("users").document(userID);
        docReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                tvEmai.setText(value.getString("email"));
                tvPhone.setText(value.getString("phone"));
                tvName.setText(value.getString("name"));

            }
        });
    }


}