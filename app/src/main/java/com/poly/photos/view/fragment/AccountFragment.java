package com.poly.photos.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.poly.photos.R;
import com.poly.photos.utils.GlobalUtils;
import com.poly.photos.utils.ProgressBarDialog;
import com.poly.photos.view.activity.LoginActivity;
import com.poly.photos.view.activity.MyAccountActivity;
import com.poly.photos.view.dialog.ResetPwDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.poly.photos.utils.GlobalUtils.MY_CAMERA_UPDATE_COVERPHOTO;
import static com.poly.photos.utils.GlobalUtils.PICK_IMAGE_REQUES;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private Button btnLogout, btnResetPw;
    private TextView tvEmai, tvPhone, tvName, btnResendCode, tvMsg;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String userID;
    private ImageView ivCover;
    private CircleImageView ivAvartar;
    private ImageButton ib_select_avartar, btnUpdateAvartar;
    private Uri uriAvartar;
    private Uri uriCover;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initAction();
        showInfor();
        showImage();
    }

    public void initAction() {

        btnLogout.setOnClickListener(this);
        btnResendCode.setOnClickListener(this);
        btnResetPw.setOnClickListener(this);
        ib_select_avartar.setOnClickListener(this);
        btnUpdateAvartar.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void initViews() {
        btnLogout = view.findViewById(R.id.btn_logout);
        tvEmai = view.findViewById(R.id.tv_email);
        tvName = view.findViewById(R.id.tv_name);
        tvPhone = view.findViewById(R.id.tv_phone);
        btnResendCode = view.findViewById(R.id.tv_resend_code);
        tvMsg = view.findViewById(R.id.tv_verify_msg);
        btnResetPw = view.findViewById(R.id.btn_reset);
        ivAvartar = view.findViewById(R.id.iv_avartar);
        ivCover = view.findViewById(R.id.iv_cover);
        ib_select_avartar = view.findViewById(R.id.ib_select_avartar);
        btnUpdateAvartar = view.findViewById(R.id.btn_update_photo);
    }

    @Override
    public void onPause() {
        super.onPause();
        showInfor();
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
                chooseUpdate();
                break;
            case R.id.btn_update_photo:
                upLoadImage();
                break;
            default:
                break;
        }
    }

    private void chooseUpdate() {

        PopupMenu popupMenu = new PopupMenu(getContext(), ib_select_avartar);
        popupMenu.getMenuInflater().inflate(R.menu.choose, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.update_avartar:

                            chooseAvartar();
                            break;

                        case R.id.update_cover:
                            chooseCoverPhoto();
                            break;


                        default:
                            break;
                    }
                    return true;
                }

        );
        popupMenu.show();
    }

    private void chooseAvartar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUES);

    }

    private void chooseCoverPhoto() {
        Intent cover = new Intent();
        cover.setType("image/*");
        cover.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(cover, GlobalUtils.MY_CAMERA_UPDATE_COVERPHOTO);

    }

    public void showInfor() {
        FirebaseUser user = auth.getCurrentUser();
        if (user!=null){
            userID = auth.getCurrentUser().getUid();
            if (!user.isEmailVerified()) {
                btnResendCode.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.VISIBLE);
            }
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String phone = snapshot.child("phone").getValue(String.class);

                        tvEmai.setText(email);
                        tvPhone.setText(phone);
                        tvName.setText(name);


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



    }
    private void showImage() {
        if (auth.getCurrentUser() != null) {
            StorageReference profile = storageReference.child("photo").child(auth.getCurrentUser().getUid() + "avartar.jpg");
            ;
            profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext()).load(uri)
                            .into(ivAvartar);
                }
            });
            StorageReference cover = storageReference.child("photo").child(auth.getCurrentUser().getUid() + "cover.jpg");
            cover.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext()).load(uri)
                            .fit().centerCrop()
                            .into(ivCover);
                }
            });
        }

    }

    private void dimissProgress() {
        ProgressBarDialog.getInstance(getContext()).closeDialog();
    }

    private void showProgress() {
        ProgressBarDialog.getInstance(getContext()).showDialog("Please wait..", getContext());
    }

    private void upLoadImage() {
        showProgress();

        if (uriCover != null) {
            StorageReference fileReference = storageReference.child("photo").child(auth.getCurrentUser().getUid() + "cover.jpg");
            fileReference.putFile(uriCover);
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    btnUpdateAvartar.setVisibility(View.INVISIBLE);
                    dimissProgress();
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (uriAvartar != null) {

            StorageReference fileReference = storageReference.child("photo").child(auth.getCurrentUser().getUid() + "avartar.jpg");
            fileReference.putFile(uriAvartar);
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    btnUpdateAvartar.setVisibility(View.INVISIBLE);
                    dimissProgress();
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MY_CAMERA_UPDATE_COVERPHOTO && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            uriCover = data.getData();
            Picasso.with(getContext()).load(uriCover).fit().centerCrop().into(ivCover);
            btnUpdateAvartar.setVisibility(View.VISIBLE);


        }
        if (requestCode == PICK_IMAGE_REQUES && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriAvartar = data.getData();
            Picasso.with(getContext()).load(uriAvartar).into(ivAvartar);
            btnUpdateAvartar.setVisibility(View.VISIBLE);


        }
    }
}