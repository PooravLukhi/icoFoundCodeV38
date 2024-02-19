package com.icofound.Notification;

import com.icofound.Model.MyResponse;
import com.icofound.Model.NotificationSender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA25dxtTU:APA91bEi6V7_IdMEU7jxUxd2pZYlJftpt-dsk2Bb81g_ffFEfg2l6VEUeDx1BJ-OEBebarPmgc2WcnhI9JV-SCGCAej4NQC5Mcyd1a3NIRsqWAMGR-X2BjAfQkF-Qj8C0eD-XMg8vJ0Y" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);

}
