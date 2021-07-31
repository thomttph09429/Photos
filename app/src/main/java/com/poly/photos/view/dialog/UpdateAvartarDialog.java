package com.poly.photos.view.dialog;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.poly.photos.R;
import com.poly.photos.model.Upload;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;
import static com.poly.photos.utils.GlobalUtils.PICK_IMAGE_REQUES;

public class UpdateAvartarDialog extends DialogFragment {
    private ImageButton ibUpdateAvartar;
    private ImageView ivPhoto;
    private View view;
    private Uri uriAvartar;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;


    public UpdateAvartarDialog() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_update_avartar, container);
        initViews(view);
        initActions();
        return view;
    }

    private void initActions() {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
    }

    private void initViews(View view) {
        ibUpdateAvartar = view.findViewById(R.id.ib_updateAvartar);
        ivPhoto = view.findViewById(R.id.iv_avartar);

    }

}