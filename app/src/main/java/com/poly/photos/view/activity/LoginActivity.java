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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.poly.photos.MainActivity;
import com.poly.photos.R;
import com.poly.photos.view.dialog.ForgotPwDialog;

public class LoginActivity extends AppCompatActivity {
    EditText edtPass, edtEmail;
    Button btnLogin;
    TextView btnRegister, btnForgot;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initViews();
        initAction();


        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPwDialog dialog = new ForgotPwDialog(LoginActivity.this);
                dialog.show();


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Email is require.");
                    return;
                } else if (TextUtils.isEmpty(pass)) {
                    edtPass.setError("Password is require.");
                    return;
                }
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            edtEmail.setText("");
                            edtPass.setText("");
                        } else {
                            Log.e("Loginactivity", "login false" + task.getException().getMessage());
                        }
                        finish();
                    }
                });
            }


        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                edtEmail.setText("");
                edtPass.setText("");

                finish();
            }
        });
//
    }

    public void initAction() {
        auth = FirebaseAuth.getInstance();


    }

    public void initViews() {
        edtPass = findViewById(R.id.edt_pass);
        edtEmail = findViewById(R.id.edt_email);
        btnRegister = findViewById(R.id.tv_create);
        btnLogin = findViewById(R.id.btn_login);
        btnForgot = findViewById(R.id.tv_forgotpw);
    }
}