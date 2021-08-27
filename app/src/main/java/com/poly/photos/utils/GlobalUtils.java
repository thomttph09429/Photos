package com.poly.photos.utils;

import android.app.Activity;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalUtils {
    public static final int PICK_IMAGE_REQUES = 100;
    public static final int MY_CAMERA_UPDATE_COVERPHOTO = 200;
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;

    public static String getDateAndTime() {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM dd hh:mm a");
        String dateAndTime = formatter.format(date);
        return dateAndTime;
    }

}
