package com.poly.photos.utils.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewholder> {
    private Context context;
    private List<Comment> commentList;
    private String postId;
    private FirebaseUser firebaseUser;


    public CommentAdapter(Context context, List<Comment> commentList, String postId) {
        this.context = context;
        this.commentList = commentList;
        this.postId = postId;
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewholder holder, int position) {
        final Comment comment = commentList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        getUserInfo(holder.ivAvartar, holder.tvUserName, comment.getPublisher());
        holder.tvComment.setText(comment.getComment());


    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewholder extends RecyclerView.ViewHolder {
        CircleImageView ivAvartar;
        TextView tvUserName, tvComment;

        public CommentViewholder(@NonNull View itemView) {
            super(itemView);
            ivAvartar = itemView.findViewById(R.id.iv_avartar);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }
    }

    private void getUserInfo(final ImageView imageView, final TextView username, String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Picasso.with(context).load(user.getAvartar()).into(imageView);
                username.setText(user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
