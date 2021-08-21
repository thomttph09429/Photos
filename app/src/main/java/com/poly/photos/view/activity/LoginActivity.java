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
import com.poly.photos.utils.ProgressBarDialog;
import com.poly.photos.view.dialog.ForgotPwDialog;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
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

    }

    public void initAction() {
        auth = FirebaseAuth.getInstance();
        btnForgot.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);


    }

    public void initViews() {
        edtPass = findViewById(R.id.edt_pass);
        edtEmail = findViewById(R.id.edt_email);
        btnRegister = findViewById(R.id.tv_create);
        btnLogin = findViewById(R.id.btn_login);
        btnForgot = findViewById(R.id.tv_forgotpw);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.tv_forgotpw:
                ForgotPwDialog dialog = new ForgotPwDialog(LoginActivity.this);
                dialog.show();

                break;
            case R.id.tv_create:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                edtEmail.setText("");
                edtPass.setText("");

                finish();
                break;
            default:
                break;
        }
    }

    private void dimissProgress() {
        ProgressBarDialog.getInstance(this).closeDialog();
    }

    private void showProgress() {
        ProgressBarDialog.getInstance(this).showDialog("Please wait..", this);
    }

    private void login() {
        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
            Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
        }else {
showProgress();

            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        dimissProgress();
                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        dimissProgress();
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

    }
}