package com.poly.photos.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.R;
import com.poly.photos.model.Post;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.PostAdapter;

import java.util.ArrayList;
import java.util.List;

public class PostDetailFragment extends Fragment {
    private View view;
    String postId;
    String publisherId;
    private RecyclerView recyclerView;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private TextView tvTitle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_post_detail, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initActions();
        readPost();
     getName();

    }

    private void initViews() {
        recyclerView = view.findViewById(R.id.recyclerView);
        tvTitle = view.findViewById(R.id.tv_title);

    }

    private void initActions() {

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
    }

    private void getName() {
        SharedPreferences share = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
        publisherId = share.getString("publisherId", "none");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(publisherId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tvTitle.setText("Bài viết của " + user.getName());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readPost() {
        SharedPreferences share = getContext().getSharedPreferences("name", Context.MODE_PRIVATE);
        postId = share.getString("postId", "none");
        publisherId = share.getString("publisherId", "none");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post = snapshot.getValue(Post.class);
                postList.add(post);
                postAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}