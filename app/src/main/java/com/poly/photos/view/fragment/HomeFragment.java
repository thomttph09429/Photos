package com.poly.photos.view.fragment;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.poly.photos.R;
import com.poly.photos.model.Story;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.PostAdapter;
import com.poly.photos.model.Post;
import com.poly.photos.utils.adapter.StoryAdapter;
import com.poly.photos.view.activity.AddStoryActivity;
import com.poly.photos.view.activity.MessageActivity;
import com.poly.photos.view.dialog.PostDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private RecyclerView recyclerView;
    private View view;
    private PostAdapter postAdapter;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private List<Post> postList;
    private List<String> followingList;
    private ShimmerFrameLayout shimmerFrameLayout;
    private CircleImageView ivAvartar, ivAvartarStory;
    private Button btnPost;
    private LinearLayout createStory;
    private List<Story> storyList;
    private StoryAdapter storyAdapter;
    private  RecyclerView rvStory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_home, container, false);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkFollowing();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initAction();
        getAvartar();
    }


    private void initAction() {
        postList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //hien thi item cuoi len dau
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        postAdapter = new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        btnPost.setOnClickListener(this);
        createStory.setOnClickListener(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        //recycleview_story
        storyList= new ArrayList<>();
        LinearLayoutManager layoutStory = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        rvStory.setLayoutManager(layoutStory);
        rvStory.setHasFixedSize(true);
        storyAdapter = new StoryAdapter(getContext(), storyList);
        rvStory.setAdapter(storyAdapter);


    }

    private void initViews() {

        recyclerView = view.findViewById(R.id.rv_post);
        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayout);
        btnPost = view.findViewById(R.id.btn_post);
        ivAvartar = view.findViewById(R.id.iv_avartar);
        createStory = view.findViewById(R.id.llcreate_story);
        ivAvartarStory = view.findViewById(R.id.iv_avartar_story);
        rvStory = view.findViewById(R.id.rv_story);
    }

    private void getAvartar() {
        DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());

        profile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.with(getContext()).load(user.getAvartar()).into(ivAvartar);
                Picasso.with(getContext()).load(user.getAvartar()).into(ivAvartarStory);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void showPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id : followingList) {
                        if (post.getPublisher().equals(id)) {
                            postList.add(post);

                        }
                    }
//                    postList.add(post);
                }
                postAdapter.notifyDataSetChanged();

                recyclerView.setVisibility(View.VISIBLE);

                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.hideShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });

    }

    private void checkFollowing() {
        followingList = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child("following");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    followingList.add(dataSnapshot.getKey());
                }
                showPost();
                readStory();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void readStory(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long timecurrent = System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("", 0, 0, "",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));
                for (String id : followingList) {
                    int countStory = 0;
                    Story story = null;
                    for (DataSnapshot snapshot : dataSnapshot.child(id).getChildren()) {
                        story = snapshot.getValue(Story.class);
                        storyList.add(story);

                        if (timecurrent > story.getTimeStart() && timecurrent < story.getTimeEnd()) {
                            countStory++;
                        }
                    }
//                    if (countStory > 0){
//                        storyList.add(story);
//                    }
                }

                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
            case R.id.llcreate_story:
                Intent intent=new Intent(getContext(), AddStoryActivity.class);
                startActivity(intent);


                break;
            default:
                break;
        }

    }
}