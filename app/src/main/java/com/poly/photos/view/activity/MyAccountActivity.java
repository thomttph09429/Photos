package com.poly.photos.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.poly.photos.MainActivity;
import com.poly.photos.R;
import com.poly.photos.utils.GlobalUtils;
import com.poly.photos.view.activity.LoginActivity;
import com.poly.photos.view.dialog.ResetPwDialog;
import com.squareup.picasso.Picasso;

import static com.poly.photos.utils.GlobalUtils.MY_CAMERA_REQUEST_CODE;
import static com.poly.photos.utils.GlobalUtils.PICK_IMAGE_REQUES;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogout, btnResetPw;
    private TextView tvEmai, tvPhone, tvName, btnResendCode, tvMsg;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String userID;
    private ImageView ivAvartar;
    private ImageButton ib_select_avartar;
    private Uri uriAvartar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initViews();
        initAction();
        showInfor();
        setTitle("Account infor");

    }

    public void initViews() {
        btnLogout = findViewById(R.id.btn_logout);
        tvEmai = findViewById(R.id.tv_email);
        tvName = findViewById(R.id.tv_name);
        tvPhone = findViewById(R.id.tv_phone);
        btnResendCode = findViewById(R.id.tv_resend_code);
        tvMsg = findViewById(R.id.tv_verify_msg);
        btnResetPw = findViewById(R.id.btn_reset);
        ivAvartar = findViewById(R.id.iv_avartar);
        ib_select_avartar = findViewById(R.id.ib_select_avartar);
    }

    public void initAction() {

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        btnLogout.setOnClickListener(this);
        btnResendCode.setOnClickListener(this);
        btnResetPw.setOnClickListener(this);

    }


    public void showInfor() {
        FirebaseUser user = auth.getCurrentUser();
        userID = auth.getCurrentUser().getUid();
        if (!user.isEmailVerified()) {
            btnResendCode.setVisibility(View.VISIBLE);
            tvMsg.setVisibility(View.VISIBLE);

        }
        DocumentReference docReference = firestore.collection("users").document(userID);
        docReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                tvEmai.setText(value.getString("email"));
                tvName.setText(value.getString("name"));
                tvPhone.setText(value.getString("phone"));


            }
        });


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                ResetPwDialog dialog = new ResetPwDialog(this);
                dialog.show();
                break;
            case R.id.btn_logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();


                break;
            case R.id.tv_resend_code:
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
                break;
            case R.id.ib_select_avartar:
            default:
                break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUES && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriAvartar = data.getData();
            Picasso.with(MyAccountActivity.this).load(uriAvartar).into(ivAvartar);
        }
    }


}