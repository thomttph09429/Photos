package com.poly.photos.view.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.poly.photos.R;
import com.poly.photos.view.dialog.PostDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class CreatePostFragment extends Fragment implements View.OnClickListener {
    private ImageView ivAvartar;
    private Button btnPost;
    private View view;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_create_post, container, false);
        initViews();
        initAction();
        getAvartar();
        return view;

    }

    private void initViews() {
        btnPost = view.findViewById(R.id.btn_post);
        ivAvartar = view.findViewById(R.id.iv_avartar);
    }

    private void initAction() {
         auth= FirebaseAuth.getInstance();
        btnPost.setOnClickListener(this);
        storageReference= FirebaseStorage.getInstance().getReference();
    }

    private void getAvartar() {
        StorageReference avartar = storageReference.child("users" + auth.getCurrentUser().getUid() + "profile.jpg");
        avartar.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri)
                        .fit().centerCrop()
                        .into(ivAvartar, new Callback() {
                            @Override
                            public void onSuccess() {
                                Bitmap imageBitmap = ((BitmapDrawable) ivAvartar.getDrawable()).getBitmap();
                                RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getActivity().getResources(), imageBitmap);
                                imageDrawable.setCircular(true);
                                imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                                ivAvartar.setImageDrawable(imageDrawable);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_post:
                PostDialog post = new PostDialog();
                post.show(getFragmentManager(), "dialog");

                break;
            default:
                break;
        }
    }
}