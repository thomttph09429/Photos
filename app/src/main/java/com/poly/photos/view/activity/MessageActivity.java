package com.poly.photos.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.MainActivity;
import com.poly.photos.R;
import com.poly.photos.model.Chat;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.MessageAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvUserName;
    Intent intent;
    String userId;
    private EditText edtMessage;
    private ImageView ivSend;
    private  CircleImageView ivAvartar;
    private RecyclerView rvMessage;
    private MessageAdapter messageAdapter;
    private List<Chat> chatList;
    private FirebaseUser firebaseUser;
    private ValueEventListener seenListener;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        initViews();
        initAction();
        getInfor();
        seenMessage(userId);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        });
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        ivSend = findViewById(R.id.iv_send);
        edtMessage = findViewById(R.id.edt_message);
        rvMessage = findViewById(R.id.rv_message);
        ivAvartar = findViewById(R.id.iv_avartar);

    }

    private void initAction() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        ivSend.setOnClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        rvMessage.setHasFixedSize(true);
        rvMessage.setLayoutManager(layoutManager);
//        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);


    }

    private void getInfor() {
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                tvUserName.setText(user.getName());
                Picasso.with(getApplicationContext()).load(user.getAvartar()).into(ivAvartar);
                readMessage(firebaseUser.getUid(), userId, user.getAvartar());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seenMessage(final String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isSeen", false);
        reference.child("Chats").push().setValue(hashMap);
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userId)
                .child(firebaseUser.getUid());
        chatRefReceiver.child("id").setValue(firebaseUser.getUid());

    }

    private void checkSend() {
        String message = edtMessage.getText().toString();
        if (!message.equals("")) {
            sendMessage(firebaseUser.getUid(), userId, message);

        } else {
        }
        edtMessage.setText("");
    }

    private void readMessage(String myId, String userId, String urlAvartar) {
        chatList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(myId) && chat.getSender().equals(userId)
                            || chat.getReceiver().equals(userId) && chat.getSender().equals(myId)) {
                        chatList.add(chat);

                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this, chatList, urlAvartar);
                    rvMessage.setAdapter(messageAdapter);
                }
//                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_send:
                checkSend();
                break;
        }
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReference.removeEventListener(seenListener);
        status("offline");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MessageActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}