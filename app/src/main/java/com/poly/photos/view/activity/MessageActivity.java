package com.poly.photos.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.MainActivity;
import com.poly.photos.NotificationMessage.Data;
import com.poly.photos.NotificationMessage.MyRespone;
import com.poly.photos.NotificationMessage.Retrofit.APIService;
import com.poly.photos.NotificationMessage.Retrofit.Client;
import com.poly.photos.NotificationMessage.Sender;
import com.poly.photos.NotificationMessage.Token;
import com.poly.photos.R;
import com.poly.photos.model.Chat;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.MessageAdapter;
import com.poly.photos.view.fragment.ProfileFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvUserName;
    Intent intent;
    String userId;
    private EditText edtMessage;
    private Button ivSend;
    private CircleImageView ivAvartar;
    private RecyclerView rvMessage;
    private MessageAdapter messageAdapter;
    private List<Chat> chatList;
    private FirebaseUser firebaseUser;
    private ValueEventListener seenListener;
    private DatabaseReference databaseReference;
    private APIService apiService;
    private boolean notifi = false;

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
            startActivity(new Intent(MessageActivity.this, ListChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        apiService = Client.getInstance("https://fcm.googleapis.com/").create(APIService.class);
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

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);


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
         databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
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
        hashMap.put("isSeen", "false");
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

        String msg = message;
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if (notifi) {
                    sendNotifiaction(receiver, user.getName(), msg, user.getAvartar());

                } else {
                    notifi = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void sendNotifiaction(String receiver, final String userName, final String message, String avartar){
        intent = getIntent();
    String    userId = intent.getStringExtra("userId");
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(firebaseUser.getUid(), R.drawable.pineapples, userName+": "+message, "Bạn có tin nhắn mới ",
                            userId,avartar );

                    Sender sender = new Sender(data, token.getToken());

                    apiService.sendNotification(sender).enqueue(new Callback<MyRespone>() {
                        @Override
                        public void onResponse(Call<MyRespone> call, Response<MyRespone> response) {
                            if (response.code() == 200){
                                if (response.body().success != 1){
//                                    Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<MyRespone> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkSend() {

        String message = edtMessage.getText().toString();
        if (!message.equals("")) {
            sendMessage(firebaseUser.getUid(), userId, message);
            notifi=true;

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
    private  void currentUser(String userId){
        SharedPreferences.Editor editor= getSharedPreferences("name", MODE_PRIVATE).edit();
        editor.putString("currentUser",userId);
        editor.apply();
    }
    @Override
    protected void onResume() {
        super.onResume();
        status("online");
        currentUser(userId);
    }
    @Override
    protected void onPause() {
        super.onPause();
        currentUser("none");
        databaseReference.removeEventListener(seenListener);

    }

}