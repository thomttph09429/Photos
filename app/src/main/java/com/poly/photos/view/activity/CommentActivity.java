package com.poly.photos.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.poly.photos.model.Comment;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.CommentAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton ibSend;
    private CircleImageView ivAvartar;
    private EditText edtComment;
    String postId;
    String publisherId;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private List<Comment> commentList;
    private CommentAdapter commentAdapter;
    private RecyclerView rc_comment;
    private ShimmerFrameLayout shimmerFrameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        overridePendingTransition(R.anim.activity_slide_from_bottom, R.anim.stay);

        initViews();
        initActions();
        getAvartar();
        readComment();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        publisherId = intent.getStringExtra("publisherId");

    }

    private void initViews() {
        ibSend = findViewById(R.id.ib_send);
        edtComment = findViewById(R.id.edt_comment);
        rc_comment = findViewById(R.id.rc_comment);
        ivAvartar = findViewById(R.id.iv_avartar);
        rc_comment = findViewById(R.id.rc_comment);
        shimmerFrameLayout = findViewById(R.id.item_comment);

    }

    private void initActions() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        commentList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rc_comment.setHasFixedSize(true);
        rc_comment.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(this, commentList, postId);
        rc_comment.setAdapter(commentAdapter);
        ibSend.setOnClickListener(this);


    }

    private void getAvartar() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.with(CommentActivity.this).load(user.getAvartar()).into(ivAvartar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments")
                .child(postId);
        String commentid = reference.push().getKey();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("publisher", firebaseUser.getUid());
        hashMap.put("comment", edtComment.getText().toString());
        hashMap.put("commentid", commentid);

        reference.push().setValue(hashMap);
//        edtComment.setText("");
    }

    private void addNotification(){
        if (!firebaseUser.getUid().equals(publisherId)){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(publisherId);
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("userId", firebaseUser.getUid());
            hashMap.put("text","???? b??nh lu???n "+ edtComment.getText().toString());
            hashMap.put("postId",postId);
            hashMap.put("isPost", true);
            reference.push().setValue(hashMap);
        }

    }





    private void readComment() {
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        Log.e("thanh cong ", "" + postId);
        if (postId != null) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    commentList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Comment comment = snapshot.getValue(Comment.class);
                        commentList.add(comment);

                    }

                    commentAdapter.notifyDataSetChanged();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rc_comment.setVisibility(View.VISIBLE);
                            shimmerFrameLayout.stopShimmer();
                            shimmerFrameLayout.hideShimmer();
                            shimmerFrameLayout.setVisibility(View.GONE);


                        }
                    }, 1000);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

    private void sendComment() {
        if (edtComment.getText().toString().equals("")) {
            Toast.makeText(CommentActivity.this, "Vi???t g?? ???? ??i", Toast.LENGTH_SHORT).show();
        } else {
            addComment();
            readComment();
            addNotification();

        }
        edtComment.setText("");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.stay, R.anim.activity_slide_to_bottom);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_send:
                sendComment();
        }

    }
}