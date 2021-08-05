package com.poly.photos.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.R;
import com.poly.photos.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewholder> {

    private Context context;
    private List<User> userList;
    private FirebaseUser firebaseUser;
    private boolean isFragment;

    public UserAdapter(Context context, List<User> userList, boolean isFragment) {
        this.context = context;
        this.userList = userList;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public UserAdapter.UserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.UserViewholder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewholder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = userList.get(position);

        holder.tvName.setText(user.getName());
        Picasso.with(context).load(user.getAvartar()).placeholder(R.drawable.portrait).into(holder.ivAvartar);


        holder.btnFollow.setVisibility(View.VISIBLE);
        isFollowing(user.getId(), holder.btnFollow);
        if (user.getId() .equals( firebaseUser.getUid())) {
            holder.btnFollow.setVisibility(View.GONE);

        }





        holder.btnFollow.setOnClickListener(v -> {
            if (holder.btnFollow.getText().toString().equals("follow")) {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                        .child("Follows").child(firebaseUser.getUid()).setValue(true);
            }else {
                FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                        .child("following").child(user.getId()).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                        .child("Follows").child(firebaseUser.getUid()).removeValue();
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class UserViewholder extends RecyclerView.ViewHolder {
        TextView tvName;
        CircleImageView ivAvartar;
        Button btnFollow;

        public UserViewholder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_user_name);
            ivAvartar = itemView.findViewById(R.id.iv_avartar);
            btnFollow = itemView.findViewById(R.id.btn_follow);

        }
    }

    private void isFollowing(String userId, Button button) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userId).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
