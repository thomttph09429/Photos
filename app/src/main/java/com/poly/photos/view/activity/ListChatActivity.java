package com.poly.photos.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.poly.photos.NotificationMessage.Token;
import com.poly.photos.R;
import com.poly.photos.model.ChatList;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.ChatUserAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private DatabaseReference reference;
    private CircleImageView ivAvartar;
    ChatUserAdapter chatUserAdapter;
    List<ChatList> userList;
    List<User> mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat);
        initViews();
        initActions();
        getAvartar();
        addUser();
        updateToken(FirebaseInstanceId.getInstance().getToken());

    }

    private void initActions() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ListChatActivity.this));
        userList = new ArrayList<>();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rv_user_chat);
        ivAvartar=findViewById(R.id.iv_avartar);
    }

    private void chatList() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatlist : userList) {
                        if (user.getId().equals(chatlist.getId())) {
                            mUser.add(user);
                        }
                    }
                }
                chatUserAdapter = new ChatUserAdapter(ListChatActivity.this, mUser);
                recyclerView.setAdapter(chatUserAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void addUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatList").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatList chatList = dataSnapshot.getValue(ChatList.class);
                    userList.add(chatList);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private  void  updateToken(String tokens){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokens);
        reference.child(firebaseUser.getUid()).setValue(token);
    }

    private void getAvartar() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                Picasso.with(getApplicationContext()).load(user.getAvartar()).into(ivAvartar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}