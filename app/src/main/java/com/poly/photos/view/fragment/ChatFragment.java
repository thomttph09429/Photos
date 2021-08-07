package com.poly.photos.view.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.R;
import com.poly.photos.model.Chat;
import com.poly.photos.model.ChatList;
import com.poly.photos.model.User;
import com.poly.photos.utils.adapter.ChatUserAdapter;
import com.poly.photos.utils.adapter.UserAdapter;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private FirebaseUser firebaseUser;
    private  DatabaseReference reference;
    ChatUserAdapter chatUserAdapter;
    List<ChatList> userList;
    List<User> mUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initActions();
        addUser();

    }

    private void initActions() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userList = new ArrayList<>();
    }

    private void initViews() {
        recyclerView = view.findViewById(R.id.rv_user_chat);
    }


    private void chatList() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for (ChatList chatlist : userList){
                        if (user.getId().equals(chatlist.getId())){
                            mUser.add(user);
                        }
                    }
                }
                chatUserAdapter = new ChatUserAdapter(getContext(), mUser);
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

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        menu.clear();
//        inflater.inflate(R.menu.toolbar_chat, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }


}