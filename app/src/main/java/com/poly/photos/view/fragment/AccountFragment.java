package com.poly.photos.view.fragment;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.poly.photos.R;
import com.poly.photos.view.activity.LoginActivity;
import com.poly.photos.view.activity.MyAccountActivity;
import com.poly.photos.view.dialog.ResetPwDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;
import static com.poly.photos.utils.GlobalUtils.PICK_IMAGE_REQUES;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private Button btnLogout, btnResetPw;
    private TextView tvEmai, tvPhone, tvName, btnResendCode, tvMsg;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String userID;
    private ImageView ivAvartar;
    private ImageButton ib_select_avartar, btnUpdateAvartar;
    private Uri uriAvartar;
    private ProgressBar progressBar;
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
        ib_select_avartar = view.findViewById(R.id.ib_select_avartar);
        btnUpdateAvartar = view.findViewById(R.id.btn_updateAvartar);
        progressBar = view.findViewById(R.id.progress_bar);
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
                openFileChooser();
                break;
            case R.id.btn_updateAvartar:
                upLoadImage();
                break;
            default:
                break;
        }
    }

    public void showInfor() {
        FirebaseUser user = auth.getCurrentUser();
        userID = auth.getCurrentUser().getUid();
        if (!user.isEmailVerified()) {
            btnResendCode.setVisibility(View.VISIBLE);
            tvMsg.setVisibility(View.VISIBLE);
        }

            DocumentReference docReference = firestore.collection("users").document(userID);
            docReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                   if (auth.getCurrentUser()!= null){
                       tvEmai.setText(value.getString("email"));
                       tvName.setText(value.getString("name"));
                       tvPhone.setText(value.getString("phone"));
                   }

                }
            });


    }

    private void showImage() {
        if (auth.getCurrentUser()!= null){
            StorageReference profile = storageReference.child("users" + auth.getCurrentUser().getUid() + "profile.jpg");
            profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.with(getContext()).load(uri)
                            .fit().centerCrop()
                            .into(ivAvartar, new Callback() {
                                @Override
                                public void onSuccess() {
                                    Bitmap imageBitmap = ((BitmapDrawable) ivAvartar.getDrawable()).getBitmap();
                                    RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                                    imageDrawable.setCircular(true);
                                    imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                    ivAvartar.setImageDrawable(imageDrawable);
                                    btnUpdateAvartar.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                }
            });
        }

    }

    private void upLoadImage() {

        if (uriAvartar != null) {
            StorageReference fileReference = storageReference.child("users" + auth.getCurrentUser().getUid() + "profile.jpg");
            fileReference.putFile(uriAvartar);
            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    btnUpdateAvartar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUES);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUES && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriAvartar = data.getData();
            Picasso.with(getContext()).load(uriAvartar).into(ivAvartar);
            btnUpdateAvartar.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(uriAvartar)
                    .fit().centerCrop()
                    .into(ivAvartar, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap imageBitmap = ((BitmapDrawable) ivAvartar.getDrawable()).getBitmap();
                            RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                            imageDrawable.setCircular(true);
                            imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                            ivAvartar.setImageDrawable(imageDrawable);
                            btnUpdateAvartar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }
}