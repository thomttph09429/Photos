package com.poly.photos.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.R;
import com.poly.photos.model.User;
import com.poly.photos.utils.UserAdapter;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {
    private View view;
    private UserAdapter userAdapter;
    List<User> userList;
    RecyclerView recyclerView;
    EditText edtSearchUser;
    FirebaseAuth auth;
//    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readUser();
        initViews();
        initActions();
        edtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                readUser();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUser(s.toString().toLowerCase());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initActions() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        auth=FirebaseAuth.getInstance();
        userList=new ArrayList<>();
        userAdapter= new UserAdapter(getContext(),userList,true);
        recyclerView.setAdapter(userAdapter);


    }

    private void initViews() {
        recyclerView = view.findViewById(R.id.rv_list_user);
        edtSearchUser = view.findViewById(R.id.edt_search_user);


    }
    private  void searchUser(String str){
        Query query= FirebaseDatabase.getInstance().getReference("users").orderByChild("name").startAt(str).endAt(str+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();;

                for (DataSnapshot data: snapshot.getChildren()){
                     User user = data.getValue(User.class);
                     userList.add(user);

                }
                userAdapter.notifyDataSetChanged();


            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private  void readUser(){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (edtSearchUser.getText().toString().equals("")){

                    userList.clear();
                    for (DataSnapshot data: snapshot.getChildren()){
                        User user = data.getValue(User.class);
                        userList.add(user);


                    }
                    userAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}