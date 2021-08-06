package com.poly.photos.view.dialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.poly.photos.R;

public class ResetPwDialog extends Dialog implements View.OnClickListener {
    private final Activity context;
    EditText edtNewPw;
    Button btnOk, btnCancel;
    FirebaseUser user;
    FirebaseAuth auth;

    public ResetPwDialog(Context context) {
        super(context);
        this.context = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reset_pw);

        initViews();
        initActions();
    }

    private void initActions() {
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

    }

    private void initViews() {
        edtNewPw = findViewById(R.id.edt_new_pw);
        btnOk = findViewById(R.id.btn_ok);
        btnCancel = findViewById(R.id.btn_cancel);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel:
                dismiss();
                break;
            case R.id.btn_ok:
                String newPw = edtNewPw.getText().toString().trim();

                if (TextUtils.isEmpty(newPw)) {
                    edtNewPw.setError("Can not be empty.");
                } else {
                    user.updatePassword(newPw).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), "Password reset successfully.", Toast.LENGTH_LONG).show();
                            dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Password reset failed."+e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    });
                }
            default:
                break;

        }
    }
}