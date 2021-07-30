package com.poly.photos.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.poly.photos.R;
import com.poly.photos.view.dialog.PostDialog;


public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button btnPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnPost = view.findViewById(R.id.btn_post);
        btnPost.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_post:
                PostDialog post = new PostDialog();
                post.show(getFragmentManager(),"dialog");

                break;
            default:
                break;
        }

    }
}