package com.poly.photos.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.photos.R;
import com.poly.photos.model.Upload;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewholder> {
    private Context context;
    private List<Upload> uploadList;

    public PostAdapter(Context context, List<Upload> uploadList) {
        this.context = context;
        this.uploadList = uploadList;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);
        return new PostViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewholder holder, int position) {
        Upload upload = uploadList.get(position);
        holder.tvState.setText(upload.getName());
        Picasso.with(context).load(upload.getImageUrl()).placeholder(R.drawable.avartar).fit().centerCrop().into(holder.ivPhoto);
        Picasso.with(context).load(upload.getImagAvartar()).fit().centerCrop()
                .placeholder(R.drawable.portrait)
                .into(holder.ivAvartar);
        holder.itemView.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
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
