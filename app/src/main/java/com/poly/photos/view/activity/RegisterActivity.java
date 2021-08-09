package com.poly.photos.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.poly.photos.MainActivity;
import com.poly.photos.R;
import com.poly.photos.model.User;
import com.poly.photos.utils.ProgressBarDialog;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtName, edtEmail, edtPass;
    private Button btnRegister;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    FirebaseUser user;
    private TextView btnLogin;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
        initActive();


        if (auth.getCurrentUser() != null ) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();

        }
    }

    public void findViews() {
        edtEmail = findViewById(R.id.edt_email);
        edtName = findViewById(R.id.edt_name);
        edtPass = findViewById(R.id.edt_pass);
        btnLogin = findViewById(R.id.tv_login);
        btnRegister = findViewById(R.id.btn_register);

    }

    public void initActive() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login:
                login();
                break;
            case R.id.btn_register:
                register();
                break;
            default:
                break;
        }

    }

    private void login() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();

    }

    private void dimissProgress() {
        ProgressBarDialog.getInstance(this).closeDialog();
    }

    private void showProgress() {
        ProgressBarDialog.getInstance(this).showDialog("Please wait..", this);
    }

    private void register() {


        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        String name = edtName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            edtEmail.setError("Name is require.");
            return;
        } else if (TextUtils.isEmpty(email)) {
            edtPass.setError("Email is require.");
            return;
        } else if (TextUtils.isEmpty(pass)) {
            edtPass.setError("Password is require.");
            return;
        }
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                showProgress();
                if (task.isSuccessful()) {

                    FirebaseUser user = auth.getCurrentUser();
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            dimissProgress();
                            Toast.makeText(RegisterActivity.this, "Verification email has been sent", Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();


                        }
                    });


                    userID = auth.getCurrentUser().getUid();
                    Map<String, Object> muser = new HashMap<>();
                    muser.put("name", name);
                    muser.put("email", email);
                    muser.put("id", userID);
                    muser.put("avartar","https://firebasestorage.googleapis.com/v0/b/appchat-2f812.appspot.com/o/ic_lol.png?alt=media&token=72e2379e-f4db-406c-818b-9d2b91d48f46");
                    muser.put("cover","https://firebasestorage.googleapis.com/v0/b/appchat-2f812.appspot.com/o/ic_lol.png?alt=media&token=72e2379e-f4db-406c-818b-9d2b91d48f46");

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                    ref.setValue(muser).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("register", "create success" + e.getMessage());

                        }
                    });


                    Toast.makeText(RegisterActivity.this, "Please confirm to continue!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();

                } else {
                    Log.e("Reristor", "create  failse" + task.getException().getMessage());
                }

            }
        });

    }
}