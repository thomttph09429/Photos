package com.poly.photos.utils.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.poly.photos.model.Notification;
import com.poly.photos.model.Post;
import com.poly.photos.model.User;
import com.poly.photos.view.activity.LoginActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.NotificationViewHolder holder, int position) {
        final Notification notification = notificationList.get(position);
        getUserInfor(holder.ivAvartar, holder.tvUsername, notification.getUserId());
        holder.tvNotifi.setText(notification.getText());
        holder.ivDelete.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.inflate(R.menu.delete_notifi);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete:
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(notification.getUserId());
                        reference.removeValue().addOnSuccessListener(command -> {
                            Toast.makeText(context, "Delete successfully!", Toast.LENGTH_SHORT).show();

                        });

                        return true;
                    default:
                        return false;

                }
            });
            popupMenu.show();

        });
        holder.itemView.setOnClickListener(v -> {
            if (notification.getPostId().equals("")) {
                SharedPreferences.Editor editor = context.getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.putString("profileid", notification.getUserId());
                editor.apply();
                Navigation.createNavigateOnClickListener(R.id.action_profile).onClick(holder.itemView);

            } else {

                SharedPreferences.Editor editor = context.getSharedPreferences("name", MODE_PRIVATE).edit();
                editor.putString("postId", notification.getPostId());
                editor.apply();
                Navigation.createNavigateOnClickListener(R.id.action_detail_post).onClick(holder.itemView);

            }


        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivAvartar;
        TextView tvUsername, tvNotifi;
        ImageView ivPhoto, ivDelete;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvartar = itemView.findViewById(R.id.iv_avartar);
            tvUsername = itemView.findViewById(R.id.tv_user_name);
            tvNotifi = itemView.findViewById(R.id.tv_notifi);
            ivPhoto = itemView.findViewById(R.id.iv_post_image);
            ivDelete = itemView.findViewById(R.id.iv_delete);

        }
    }

    private void getUserInfor(CircleImageView ivAvartar, TextView tvUsername, String PublisherId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("users").child(PublisherId);
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


}
