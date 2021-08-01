package com.poly.photos.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.poly.photos.R;
import com.poly.photos.model.Upload;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

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
        Picasso.with(context).load(upload.getImagAvartar())
               .placeholder(R.drawable.portrait)
                .into(holder.ivAvartar, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap imageBitmap = ((BitmapDrawable) holder.ivAvartar.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        holder.ivAvartar.setImageDrawable(imageDrawable);
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class PostViewholder extends RecyclerView.ViewHolder {
        TextView tvState;
        ImageView ivPhoto;
        ImageView ivAvartar;

        public PostViewholder(View itemView) {
            super(itemView);
            tvState = itemView.findViewById(R.id.tv_state);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            ivAvartar = itemView.findViewById(R.id.iv_avartar);

        }
    }
}
