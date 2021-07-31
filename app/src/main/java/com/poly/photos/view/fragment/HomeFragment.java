package com.poly.photos.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.R;
import com.poly.photos.utils.PostAdapter;
import com.poly.photos.view.Upload;
import com.poly.photos.view.dialog.PostDialog;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button btnPost;
    private RecyclerView recyclerView;
    private View view;
    private PostAdapter postAdapter;
    private ProgressBar progressBar;
    private DatabaseReference databaseRef;
    private List<Upload> uploadList;
    private ShimmerFrameLayout shimmerFrameLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initAction();
        showPost();
    }


    private void initAction() {
        btnPost.setOnClickListener(this);
        databaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        uploadList = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initViews() {
        btnPost = view.findViewById(R.id.btn_post);
        recyclerView = view.findViewById(R.id.rv_post);
        progressBar = view.findViewById(R.id.progress_bar);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);

    }


    private void showPost() {


        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Upload upload = dataSnapshot.getValue(Upload.class);
                    uploadList.add(upload);

                }

                postAdapter = new PostAdapter(getContext(), uploadList);
                recyclerView.setAdapter(postAdapter);

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);


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