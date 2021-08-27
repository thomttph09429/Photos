package com.poly.photos.utils.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.photos.R;
import com.poly.photos.model.Post;
import com.poly.photos.view.activity.PostDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class MyPhotoAdapter extends RecyclerView.Adapter<MyPhotoAdapter.ImageViewHolder> {
    private List<Post> postList;
    private Context context;

    public MyPhotoAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPhotoAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);


        return new ImageViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull MyPhotoAdapter.ImageViewHolder holder, int position) {
        final Post post = postList.get(position);
        holder.ivPostImage.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.with(context).load(post.getPostimage()).fit().centerInside().into(holder.ivPostImage);
        holder.itemView.setOnClickListener(v -> {
            Intent intent= new Intent(context, PostDetailActivity.class);
            intent.putExtra("postId",post.getPostid());
            intent.putExtra("publisherId", post.getPublisher());
            context.startActivity(intent);


        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostImage;


        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.iv_post_image);

        }
    }
}

