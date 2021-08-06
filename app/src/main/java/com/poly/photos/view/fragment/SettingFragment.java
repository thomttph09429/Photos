package com.poly.photos.view.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.poly.photos.R;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.MyPhotoAdapter;
import com.poly.photos.view.activity.LoginActivity;
import com.poly.photos.view.dialog.ResetPwDialog;

import java.util.ArrayList;


public class SettingFragment extends Fragment implements View.OnClickListener {
    private Button btnLogout, btnResetPw;
    private TextView tvEmai, tvPhone, tvName, btnResendCode, tvMsg;
    private View view;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initAction();
        showInfor();
    }

    public void initAction() {

        auth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(this);
        btnResetPw.setOnClickListener(this);
    }

    public void initViews() {
        btnLogout = view.findViewById(R.id.btn_logout);
        tvEmai = view.findViewById(R.id.tv_email);
        tvName = view.findViewById(R.id.tv_name);
        tvPhone = view.findViewById(R.id.tv_phone);
        btnResetPw = view.findViewById(R.id.btn_reset);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_reset:
                ResetPwDialog dialog = new ResetPwDialog(getContext());
                dialog.show();
                break;
            case R.id.btn_logout:
                auth.signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

                break;
//            case R.id.tv_resend_code:
//                FirebaseUser firebaseUser = auth.getCurrentUser();
//                firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(v.getContext(), "Verification email has been sent", Toast.LENGTH_LONG).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.e("Reristor", "Onfailure: Email not sent" + e.getMessage());
//                    }
//                });
//                break;
            default:
                break;
        }
    }
    public void showInfor() {
        if (auth.getCurrentUser()!=null){

//            if (!firebaseUser.isEmailVerified()) {
//                btnResendCode.setVisibility(View.VISIBLE);
//                tvMsg.setVisibility(View.VISIBLE);
//            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user= snapshot.getValue(User.class);
                    tvName.setText(user.getName());
                    tvEmai.setText(user.getEmail());
                    tvPhone.setText(user.getPhone());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }
}