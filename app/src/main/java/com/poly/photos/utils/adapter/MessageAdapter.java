package com.poly.photos.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.poly.photos.R;
import com.poly.photos.model.Chat;
import com.poly.photos.model.User;
import com.poly.photos.utils.GlobalUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.poly.photos.utils.GlobalUtils.MSG_LEFT;
import static com.poly.photos.utils.GlobalUtils.MSG_RIGHT;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private Context context;
    private List<Chat> chatList;
    private String urlAvartar;
    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chatList, String urlAvartar) {
        this.context = context;
        this.chatList = chatList;
        this.urlAvartar = urlAvartar;
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.showMessage.setText(chat.getMessage());
        if (urlAvartar.equals("default")) {
            holder.ivAvartar.setImageResource(R.drawable.portrait);
        }else {
            Picasso.with(context).load(urlAvartar).into(holder.ivAvartar);

        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

   public      TextView showMessage;
  public       ImageView ivAvartar;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
             showMessage= itemView.findViewById(R.id.tv_message);
             ivAvartar=itemView.findViewById(R.id.iv_avartar);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }

    }

}
