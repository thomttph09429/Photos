package com.poly.photos.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.poly.photos.R;
import com.poly.photos.model.Notification;
import com.poly.photos.utils.adapter.NotificationAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private FirebaseUser firebaseUser;
    private LinearLayout notifi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_notification, container, false);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initActions();
        readNotification();

    }

    private void initViews() {
        recyclerView = view.findViewById(R.id.rc_notification);
        notifi = view.findViewById(R.id.notifi);

    }

    private void initActions() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(getContext(), notificationList);
        recyclerView.setAdapter(notificationAdapter);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


    }


    private void readNotification() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationList.clear();
                if (snapshot.getChildrenCount()>=1){
                    recyclerView.setVisibility(View.VISIBLE);
                    notifi.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification notification = dataSnapshot.getValue(Notification.class);
                        notificationList.add(notification);
                        Log.e("thanh cong", " thong bao la " + notification.getPostId());

                    }
                    Collections.reverse(notificationList);
                    notificationAdapter.notifyDataSetChanged();

                }else {
                    recyclerView.setVisibility(View.GONE);
                    notifi.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}