package com.poly.photos.NotificationMessage.Retrofit;

import com.poly.photos.NotificationMessage.MyRespone;
import com.poly.photos.NotificationMessage.Sender;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAr5yeljs:APA91bFD5r_wr3Zh3RTavnwEimIRvijeCuHsG9m6e6xrC1btqYlK_MI0OzhyUF6_aNOPWxuFpzqtd_Byo1RMJ03m_Z5eREkDQGsKXNEy5tFkKmHSR3m6W6O1FSgqioV6LpFjMy7huMlG"
            }
    )
    @POST("fcm/send")
    Call<MyRespone> sendNotification(@Body Sender body);
}
