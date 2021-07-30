package com.poly.photos.view.dialog;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.poly.photos.R;

public class PostDialog extends DialogFragment implements View.OnClickListener {
    private EditText edtWrite;
    private LinearLayout shareLocation, sharePhoto;
    private ImageView btnPost;

    public PostDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_post, container);
        edtWrite = view.findViewById(R.id.edt_write);
        shareLocation = view.findViewById(R.id.lnl_share_location);
        sharePhoto = view.findViewById(R.id.lnl_share_photo);
        btnPost =  view.findViewById(R.id.btn_post);
        initAction();
        return view;
    }


    public void initAction() {
        edtWrite.setOnClickListener(this);
        sharePhoto.setOnClickListener(this);
        shareLocation.setOnClickListener(this);
        btnPost.setOnClickListener(this);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lnl_share_location:
                Toast.makeText(getContext(), "success", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_post:
                Toast.makeText(getContext(), "hh", Toast.LENGTH_LONG).show();
                break;
        }


    }
}