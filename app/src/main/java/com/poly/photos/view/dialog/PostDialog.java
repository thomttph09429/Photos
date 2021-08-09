package com.poly.photos.view.dialog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.poly.photos.MainActivity;
import com.poly.photos.R;
import com.poly.photos.model.Post;
import com.poly.photos.utils.ProgressBarDialog;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.poly.photos.utils.GlobalUtils.PICK_IMAGE_REQUES;

public class PostDialog extends DialogFragment implements View.OnClickListener {
    private EditText edtWrite;
    private LinearLayout shareLocation, sharePhoto;
    private ImageView btnPost, ivPhoto, ivClose;
    private Uri uriImage;
    private View view;
    private StorageReference storageRef;
    //    private DatabaseReference mDatabaseRef;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private FirebaseAuth auth;


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
        if (view == null) {
            view = inflater.inflate(R.layout.dialog_post, container);

        }

        initViews();
        initAction();
        return view;
    }

    private void initViews() {
        edtWrite = view.findViewById(R.id.edt_write);
        shareLocation = view.findViewById(R.id.lnl_share_location);
        sharePhoto = view.findViewById(R.id.lnl_share_photo);
        btnPost = view.findViewById(R.id.btn_post);
        ivPhoto = view.findViewById(R.id.iv_photo);
        ivClose = view.findViewById(R.id.iv_close);
    }


    public void initAction() {
        edtWrite.setOnClickListener(this);
        sharePhoto.setOnClickListener(this);
        shareLocation.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        storageRef = FirebaseStorage.getInstance().getReference("Posts");
        storageReference = FirebaseStorage.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

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
                upLoadFile();
                break;
            case R.id.iv_close:
                ivPhoto.setVisibility(View.GONE);
                ivClose.setVisibility(View.GONE);
                break;
            default:
                break;
        }


    }


    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void showProgress() {
        ProgressBarDialog.getInstance(getContext()).showDialog("Please wait", getContext());

    }

    private void dimissProgress() {
        ProgressBarDialog.getInstance(getContext()).closeDialog();

    }

    private void upLoadFile() {
        showProgress();
        if (uriImage != null) {
            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(uriImage));

            uploadTask = fileReference.putFile(uriImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");

                        String postid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("description", edtWrite.getText().toString());
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", miUrlOk);
                        hashMap.put("publisher", auth.getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);
                        dimissProgress();
                        dismiss();
                        Toast.makeText(getActivity(), "Oke rồi", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getContext(), "gòi xong", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUES && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriImage = data.getData();
            ivPhoto.setVisibility(View.VISIBLE);
            ivClose.setVisibility(View.VISIBLE);
            Picasso.with(getContext()).load(uriImage).fit().centerCrop().into(ivPhoto);
        }
    }

}