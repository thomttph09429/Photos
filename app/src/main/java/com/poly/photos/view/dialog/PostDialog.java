package com.poly.photos.view.dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;


import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.poly.photos.MainActivity;
import com.poly.photos.R;
import com.poly.photos.utils.GlobalUtils;
import com.poly.photos.view.Upload;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.poly.photos.utils.GlobalUtils.PICK_IMAGE_REQUES;

public class PostDialog extends DialogFragment implements View.OnClickListener {
    private EditText edtWrite;
    private LinearLayout shareLocation, sharePhoto;
    private ImageView btnPost, ivPhoto;
    private ProgressBar progressBar;
    private Uri uriImage;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;


    public PostDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_post, container);
        edtWrite = view.findViewById(R.id.edt_write);
        shareLocation = view.findViewById(R.id.lnl_share_location);
        sharePhoto = view.findViewById(R.id.lnl_share_photo);
        btnPost = view.findViewById(R.id.btn_post);
        progressBar = view.findViewById(R.id.progress_bar);
        ivPhoto = view.findViewById(R.id.iv_photo);
        initAction();
        return view;
    }


    public void initAction() {
        edtWrite.setOnClickListener(this);
        sharePhoto.setOnClickListener(this);
        shareLocation.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lnl_share_location:
                Toast.makeText(getContext(), "success", Toast.LENGTH_LONG).show();
                break;
            case R.id.lnl_share_photo:
                openFileChooser();

                break;
            case R.id.btn_post:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    upLoadFile();
                }
                break;
        }


    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void upLoadFile() {
        if (uriImage != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(uriImage));
            fileReference.putFile(uriImage)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Upload upload = new Upload(edtWrite.getText().toString().trim(),
                                            url);
                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_LONG).show();
                                    dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);

                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        }else {
            Toast.makeText(getContext(),"No file selected", Toast.LENGTH_SHORT).show();

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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUES && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriImage = data.getData();
            Picasso.with(getContext()).load(uriImage).into(ivPhoto);
        }
    }

}