package com.poly.photos.view.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.poly.photos.R;
import com.poly.photos.model.Post;
import com.poly.photos.model.User;
import com.poly.photos.utils.GlobalUtils;
import com.poly.photos.utils.ProgressBarDialog;
import com.poly.photos.utils.adapter.MyPhotoAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.poly.photos.utils.GlobalUtils.MY_CAMERA_UPDATE_COVERPHOTO;
import static com.poly.photos.utils.GlobalUtils.PICK_IMAGE_REQUES;


public class AccountFragment extends Fragment implements View.OnClickListener {
    private TextView tvAllPhoto, tvAllFollows, tvAllFollowing, tvName;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private String userID;
    private ImageView ivCover;
    private CircleImageView ivAvartar;
    private ImageButton ib_select_avartar, btnUpdateAvartar, ibEdit;
    private Uri uriAvartar;
    private Uri uriCover;
    private View view;
    private StorageReference storageRef;
    private StorageTask uploadTask;
    private String profileid;
    private List<Post> postList;
    private MyPhotoAdapter myPhotoAdapter;
    private RecyclerView rcMyPhoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null)
            view = inflater.inflate(R.layout.fragment_account, container, false);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews();
        initAction();
        showImage();
        nPost();
        getMyPost();
        SharedPreferences prefs = getContext().getSharedPreferences("name", MODE_PRIVATE);
        profileid = prefs.getString("profileid", "none");
        Log.e("thanh cong", "thnah cong" + profileid);
    }

    public void initAction() {


        postList = new ArrayList<>();
        ib_select_avartar.setOnClickListener(this);
        btnUpdateAvartar.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef = FirebaseStorage.getInstance().getReference("uploads");
        LinearLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        rcMyPhoto.setLayoutManager(layoutManager);
        myPhotoAdapter = new MyPhotoAdapter(postList, getContext());
        rcMyPhoto.setAdapter(myPhotoAdapter);


    }

    public void initViews() {
        ibEdit = view.findViewById(R.id.ib_edit);
        tvName = view.findViewById(R.id.tv_name);
        ivAvartar = view.findViewById(R.id.iv_avartar);
        ivCover = view.findViewById(R.id.iv_cover);
        ib_select_avartar = view.findViewById(R.id.ib_select_avartar);
        btnUpdateAvartar = view.findViewById(R.id.btn_update_photo);
        rcMyPhoto = view.findViewById(R.id.rc_all_photo);
        tvAllFollowing = view.findViewById(R.id.tv_all_following);
        tvAllFollows = view.findViewById(R.id.tv_all_follows);
        tvAllPhoto = view.findViewById(R.id.tv_all_photo);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ib_select_avartar:
                chooseUpdate();
                break;
            case R.id.btn_update_photo:
                upLoadImage();
                break;
            default:
                break;
        }
    }

    private void chooseUpdate() {

        PopupMenu popupMenu = new PopupMenu(getContext(), ib_select_avartar);
        popupMenu.getMenuInflater().inflate(R.menu.choose, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.update_avartar:

                            chooseAvartar();
                            break;

                        case R.id.update_cover:
                            chooseCoverPhoto();
                            break;


                        default:
                            break;
                    }
                    return true;
                }

        );
        popupMenu.show();
    }

    private void chooseAvartar() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUES);

    }

    private void chooseCoverPhoto() {
        Intent cover = new Intent();
        cover.setType("image/*");
        cover.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(cover, GlobalUtils.MY_CAMERA_UPDATE_COVERPHOTO);

    }


    private void showImage() {
        if (auth.getCurrentUser() != null) {
            DatabaseReference profile = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());

            profile.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    tvName.setText(user.getName());
                    if (user.getAvartar().equals("default")) {
                        ivAvartar.setImageResource(R.drawable.sky);
                    } else {
                        Picasso.with(getContext()).load(user.getAvartar()).into(ivAvartar);

                    }
                    Picasso.with(getContext()).load(user.getCover()).fit().centerCrop().into(ivCover);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

    }

    private void dimissProgress() {
        ProgressBarDialog.getInstance(getContext()).closeDialog();
    }

    private void showProgress() {
        ProgressBarDialog.getInstance(getContext()).showDialog("Please wait..", getContext());
    }


    private void upLoadImage() {

        if (uriAvartar != null) {
            showProgress();
            StorageReference fileReference = storageRef.child(auth.getCurrentUser().getUid() + "avartar.jpg");

            uploadTask = fileReference.putFile(uriAvartar);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("avartar", "" + miUrlOk);
                        reference.updateChildren(map1);
                        dimissProgress();
                        btnUpdateAvartar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        }
        if (uriCover != null) {
            StorageReference fileReference = storageRef.child(auth.getCurrentUser().getUid() + "cover.jpg");

            uploadTask = fileReference.putFile(uriCover);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(auth.getCurrentUser().getUid());
                        HashMap<String, Object> map1 = new HashMap<>();
                        map1.put("cover", "" + miUrlOk);
                        reference.updateChildren(map1);
                        btnUpdateAvartar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();


                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }
    }

    private void nPost() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        i++;
                    }
                }
                tvAllPhoto.setText("" + i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMyPost() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dsnapshot : snapshot.getChildren()) {
                    Post post = dsnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileid)) {
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == MY_CAMERA_UPDATE_COVERPHOTO && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            uriCover = data.getData();
            Picasso.with(getContext()).load(uriCover).fit().centerCrop().into(ivCover);
            btnUpdateAvartar.setVisibility(View.VISIBLE);


        }
        if (requestCode == PICK_IMAGE_REQUES && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            uriAvartar = data.getData();
            Picasso.with(getContext()).load(uriAvartar).into(ivAvartar);
            btnUpdateAvartar.setVisibility(View.VISIBLE);


        }
    }
}