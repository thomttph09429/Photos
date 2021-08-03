package com.poly.photos.view.dialog;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.poly.photos.R;

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