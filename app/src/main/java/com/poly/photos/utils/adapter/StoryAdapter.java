package com.poly.photos.utils.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.poly.photos.model.Story;
import com.poly.photos.model.User;
import com.poly.photos.view.activity.StoryActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {
    private Context context;
    private List<Story> storyList;
    private FirebaseUser firebaseUser;

    public StoryAdapter(Context context, List<Story> storyList) {
        this.context = context;
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryAdapter.StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryAdapter.StoryViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull StoryAdapter.StoryViewHolder holder, int position) {
        final Story story = storyList.get(position);
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            userInfor(story.getUserId(), holder.tv_user_name, holder.iv_story_photo);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() != 0){
                } else {
                    Intent intent = new Intent(context, StoryActivity.class);
                    intent.putExtra("userId", story.getUserId());
                    context.startActivity(intent);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_story_photo;
        TextView tv_user_name;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_story_photo = itemView.findViewById(R.id.iv_story_photo);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
        }
    }

    private void userInfor(String userId, TextView tvUserName, CircleImageView inAvartar) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tvUserName.setText(user.getName());
                Picasso.with(context).load(user.getAvartar()).into(inAvartar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void myStory(final boolean click) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(firebaseUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                long timeCurrent = System.currentTimeMillis();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Story story = dataSnapshot.getValue(Story.class);
                    if (timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()) {
                        count++;
                    }
                }
                if (click) {
                    if (count > 0) {
                        Intent intent = new Intent(context, StoryActivity.class);
                        intent.putExtra("userId", firebaseUser.getUid());
                        context.startActivity(intent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}
