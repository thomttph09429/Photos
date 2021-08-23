package com.poly.photos.view.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.poly.photos.R;
import com.poly.photos.model.User;
import com.poly.photos.view.dialog.PostDialog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreatePostFragment extends Fragment implements View.OnClickListener {
    private CircleImageView ivAvartar;
    private Button btnPost;
    private View view;
    private StorageReference storageReference;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_create_post, container, false);

        return view;

    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initAction();
        getAvartar();

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
        if (auth.getCurrentUser() != null) {
            DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

            profile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user.getAvartar().equals("default")) {
                        ivAvartar.setImageResource(R.drawable.sky);
                    } else {
                        Picasso.with(getContext()).load(user.getAvartar()).into(ivAvartar);

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


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