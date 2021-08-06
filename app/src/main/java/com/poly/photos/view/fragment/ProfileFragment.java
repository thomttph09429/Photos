package com.poly.photos.view.fragment;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.poly.photos.R;
import com.poly.photos.model.Post;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.MyPhotoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {
    private TextView tvAllPhoto, tvAllFollows, tvAllFollowing, tvName;
    private FirebaseUser firebaseUser;

    private ImageView ivCover;
    private CircleImageView ivAvartar;
    private View view;
    String profileid;
    private List<Post> postList;
    private MyPhotoAdapter myPhotoAdapter;
    private RecyclerView rcMyPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences prefs = getContext().getSharedPreferences("name", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        Log.e("thanh cong", "fff"+profileid);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initAction();
        showImage();
        nPost();
        getMyPost();

    }

    public void initAction() {
        postList = new ArrayList<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rcMyPhoto.setLayoutManager(layoutManager);
        myPhotoAdapter = new MyPhotoAdapter(postList, getContext());
        rcMyPhoto.setAdapter(myPhotoAdapter);


    }

    public void initViews() {
        tvName = view.findViewById(R.id.tv_name);
        ivAvartar = view.findViewById(R.id.iv_avartar);
        ivCover = view.findViewById(R.id.iv_cover);
        rcMyPhoto = view.findViewById(R.id.rc_all_photo);
        tvAllFollowing = view.findViewById(R.id.tv_all_following);
        tvAllFollows = view.findViewById(R.id.tv_all_follows);
        tvAllPhoto = view.findViewById(R.id.tv_all_photo);

    }

    private void showImage() {
            DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users").child(profileid);

            profile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    tvName.setText(user.getName());
                    if (user.getAvartar().equals("default")) {
                        ivAvartar.setImageResource(R.drawable.sky);
                    } else {
                        Picasso.with(getContext()).load(user.getAvartar()).into(ivAvartar);

                    }
                    Picasso.with(getContext()).load(user.getCover()).fit().centerCrop().into(ivCover);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }



    private void nPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        i++;
                    }
                }
                tvAllPhoto.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMyPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dsnapshot : snapshot.getChildren()) {
                    Post post = dsnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}