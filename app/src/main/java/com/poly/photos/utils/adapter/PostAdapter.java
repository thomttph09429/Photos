package com.poly.photos.utils.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.R;
import com.poly.photos.model.Post;
import com.poly.photos.model.User;
import com.poly.photos.view.activity.CommentActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewholder> {
    private Context context;
    private List<Post> postList;
    private List<String> followingList;
    private FirebaseUser firebaseUser;
    private Activity activity;


    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostAdapter.PostViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewholder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = postList.get(position);
        if (post.getDescription().equals("")) {
            holder.tvState.setVisibility(View.GONE);
        } else {
            holder.tvState.setVisibility(View.VISIBLE);
            holder.tvState.setText(post.getDescription());

        }
        Picasso.with(context).load(post.getPostimage()).fit().centerCrop().into(holder.ivPhoto);

        publisherInfor(holder.ivAvartar, holder.tvUserName, post.getPublisher());

//       Navigation.createNavigateOnClickListener(R.id.action_home_to_action_comment).onClick(holder.iv_comment);
//        Navigation.findNavController(view).navigate(R.id.action_home_to_action_comment, bundle);

        holder.iv_comment.setOnClickListener(v ->
        {
            Intent intent = new Intent(context, CommentActivity.class);
            intent.putExtra("postId", post.getPostid());
            intent.putExtra("publisherId", post.getPublisher());
            context.startActivity(intent);
        });

        holder.ivPhoto.setOnClickListener(v -> {

            SharedPreferences.Editor editor = context.getSharedPreferences("name", MODE_PRIVATE).edit();
            editor.putString("postId", post.getPostid());
            editor.putString("publisherId", post.getPublisher());
            editor.apply();
            Navigation.createNavigateOnClickListener(R.id.action_detail_post).onClick(holder.ivPhoto);

        });

        holder.tvUserName.setOnClickListener(v -> {

            if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                Navigation.createNavigateOnClickListener(R.id.action_account).onClick(holder.iv_comment);

            } else {
                SharedPreferences.Editor editor = context.getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();
                Navigation.createNavigateOnClickListener(R.id.action_profile).onClick(holder.iv_comment);

            }

        });
        holder.ivAvartar.setOnClickListener(v -> {

            if (post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                Navigation.createNavigateOnClickListener(R.id.action_account).onClick(holder.iv_comment);

            } else {
                SharedPreferences.Editor editor = context.getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.putString("profileid", post.getPublisher());
                editor.apply();
                Navigation.createNavigateOnClickListener(R.id.action_profile).onClick(holder.iv_comment);

            }

        });

        holder.iv_share.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.delete:
                       final      String id = post.getPostid();
                            FirebaseDatabase.getInstance().getReference("Posts").child(id).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                deleteNotifications(id, firebaseUser.getUid());
                                            }
                                        }
                                    });
                            return true;
                        case R.id.edit:
                        default:
                            return false;

                    }
                }
            });
            popupMenu.inflate(R.menu.post);
            if (!post.getPublisher().equals(firebaseUser.getUid())){
                popupMenu.getMenu().findItem(R.id.edit).setVisible(false);
                popupMenu.getMenu().findItem(R.id.delete).setVisible(false);
            }
            popupMenu.show();
        });

        getComment(post.getPostid(), holder.iv_comment);

        nComment(holder.tv_comment, post.getPostid());
        isLikes(post.getPostid(), holder.iv_like);
        nLike(holder.tv_like, post.getPostid());
        holder.iv_like.setOnClickListener(v -> {
            if (holder.iv_like.getTag().equals("like")) {
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                addNotificationLove(post.getPublisher(), post.getPostid());
            } else {
                FirebaseDatabase.getInstance().getReference().child("Likes")
                        .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
            }

        });

        isCare(post.getPostid(), holder.iv_care);
        nCare(holder.tv_care, post.getPostid());
        holder.iv_care.setOnClickListener(v -> {
            if (holder.iv_care.getTag().equals("care")) {
                FirebaseDatabase.getInstance().getReference().child("Cares")
                        .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                addNotificationCare(post.getPublisher(), post.getPostid());

            } else {
                FirebaseDatabase.getInstance().getReference().child("Cares")
                        .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
            }

        });


    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewholder extends RecyclerView.ViewHolder {
        TextView tvState, tvUserName, tv_like, tv_comment, tv_care;
        ImageView ivPhoto, iv_like, iv_comment, iv_share, iv_care;
        LinearLayout lSelect;
        CircleImageView ivAvartar;

        public PostViewholder(View itemView) {
            super(itemView);
            tvState = itemView.findViewById(R.id.tv_state);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivAvartar = itemView.findViewById(R.id.iv_avartar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            iv_like = itemView.findViewById(R.id.iv_like);
            tv_like = itemView.findViewById(R.id.tv_like);
            iv_care = itemView.findViewById(R.id.iv_care);
            tv_care = itemView.findViewById(R.id.tv_care);
            iv_comment = itemView.findViewById(R.id.iv_comment);
            tv_comment = itemView.findViewById(R.id.tv_comment);
            iv_share = itemView.findViewById(R.id.iv_more);
            lSelect = itemView.findViewById(R.id.select);

        }
    }

    private void deleteNotifications(final String postId, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("postId").getValue().equals(postId)) {
                        snapshot.getRef().removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(context, "Đã xóa!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfor(ImageView ivAvartar, TextView tvUsername, String userId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tvUsername.setText(user.getName());
                Picasso.with(context).load(user.getAvartar()).into(ivAvartar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isLikes(String postId, ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_love_love);
                    imageView.setTag("liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_love);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nLike(TextView tvLike, String postId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Likes")
                .child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvLike.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nComment(TextView tvComment, String postId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvComment.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void isCare(String postId, ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cares").child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_care_care);
                    imageView.setTag("cared");
                } else {
                    imageView.setImageResource(R.drawable.ic_care);
                    imageView.setTag("care");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nCare(TextView tvCare, String postId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Cares")
                .child(postId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvCare.setText(snapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getComment(String postId, ImageView comment) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Comments")
                .child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void addNotificationLove(String userId, String postId) {
        if (!firebaseUser.getUid().equals(userId)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userId", firebaseUser.getUid());
            hashMap.put("text", "đã thả tim vào ảnh của bạn");
            hashMap.put("postId", postId);
            hashMap.put("isPost", true);
            reference.push().setValue(hashMap);
        }

    }

    private void addNotificationCare(String userId, String postId) {
        if (!firebaseUser.getUid().equals(userId)) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userId", firebaseUser.getUid());
            hashMap.put("text", "đã cười ha ha về ảnh của bạn:))");
            hashMap.put("postId", postId);
            hashMap.put("isPost", true);
            reference.push().setValue(hashMap);
        }
    }
}
