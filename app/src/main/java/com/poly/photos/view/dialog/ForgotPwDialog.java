package com.poly.photos.view.dialog;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.poly.photos.R;

public class ForgotPwDialog extends Dialog implements View.OnClickListener {
    public EditText edtEmail;
    public Button btnOk, btnCancel;
    private final Activity context;

    public ForgotPwDialog(Context context) {
        super(context);
        this.context = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_forgot_pw);
        edtEmail = findViewById(R.id.edt_email);
        btnOk = findViewById(R.id.btn_ok);
        btnCancel = findViewById(R.id.btn_cancel);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                String email = edtEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    edtEmail.setError("Can not be empty.");
                } else {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Reset link sent to your email", Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Error! Rest link is not sent", Toast.LENGTH_LONG).show();

                        }
                    });

                }
            default:
                break;

        }


    }
}