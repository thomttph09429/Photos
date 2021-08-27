package com.poly.photos.utils.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.poly.photos.model.Chat;
import com.poly.photos.model.User;
import com.poly.photos.view.activity.MessageActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserViewholder> {

    private Context context;
    private List<User> userList;
    private String lastMessages;

    public ChatUserAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    private FirebaseUser firebaseUser;

    @NonNull
    @Override
    public ChatUserAdapter.ChatUserViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_chat, parent, false);
        return new ChatUserAdapter.ChatUserViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserAdapter.ChatUserViewholder holder, int position) {
        final User user = userList.get(position);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        checkLastMsg(user.getId(), holder.tvLastMsg);
        isSeen(holder.tvUserName, holder.tvLastMsg);

        holder.tvUserName.setText(user.getName());

        Picasso.with(context).load(user.getAvartar()).into(holder.ivAvartar);

        if (user.getStatus().equals("online")) {
            holder.ivOnline.setVisibility(View.VISIBLE);
            holder.ivOffline.setVisibility(View.GONE);

        } else {
            holder.ivOnline.setVisibility(View.GONE);
            holder.ivOffline.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userId", user.getId());
            context.startActivity(intent);

        });
    }

    private void isSeen(TextView tvUsername, TextView tvLastMessage) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getIsSeen().equals("true") ||
                            chat.getSender().equals(firebaseUser.getUid()) && chat.getIsSeen().equals("true")
                    ) {
                        tvUsername.setTextColor(context.getResources().getColor(R.color.background_8));
                        tvLastMessage.setTextColor(context.getResources().getColor(R.color.background_8));
                    }else {
                        tvUsername.setTextColor(context.getResources().getColor(R.color.white));
                        tvLastMessage.setTextColor(context.getResources().getColor(R.color.white));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkLastMsg(String userId, TextView tvLastMessage) {
        lastMessages = "default";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(firebaseUser.getUid())) {
                        lastMessages = chat.getMessage();
                    }
                }
                switch (lastMessages) {
                    case "default":
                        tvLastMessage.setText("Nhắn tin nào");
                        break;

                    default:
                        tvLastMessage.setText(lastMessages);
                        break;
                }
                lastMessages = "default";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ChatUserViewholder extends RecyclerView.ViewHolder {
        CircleImageView ivAvartar, ivOnline, ivOffline;
        TextView tvUserName, tvLastMsg;

        public ChatUserViewholder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            ivAvartar = itemView.findViewById(R.id.iv_avartar);
            ivOnline = itemView.findViewById(R.id.iv_online);
            ivOffline = itemView.findViewById(R.id.iv_offline);
            tvLastMsg = itemView.findViewById(R.id.tv_last_message);

        }
    }
}
