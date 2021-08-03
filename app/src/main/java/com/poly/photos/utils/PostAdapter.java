package com.poly.photos.utils;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.poly.photos.R;
import com.poly.photos.model.Post;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewholder> {
    private Context context;
    private List<Post> postList;
    private  List<String> followingList;



    public PostAdapter(Context context, List<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewholder holder, int position) {
        Post upload = postList.get(position);
        holder.tvState.setText(upload.getDescription());
        Picasso.with(context).load(upload.getPostimage()).placeholder(R.drawable.portrait).fit().centerCrop().into(holder.ivPhoto);
        Picasso.with(context).load(upload.getAvartar()).fit().centerCrop()
                .placeholder(R.drawable.portrait)
                .into(holder.ivAvartar);
        holder.itemView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class PostViewholder extends RecyclerView.ViewHolder {
        TextView tvState;
        ImageView ivPhoto;
        CircleImageView ivAvartar;

        public PostViewholder(View itemView) {
            super(itemView);
            tvState = itemView.findViewById(R.id.tv_state);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivAvartar = itemView.findViewById(R.id.iv_avartar);

        }
    }
}
