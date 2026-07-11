package com.smartroad.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.smartroad.util.SessionManager;

/**
 * Structural scaffold for future push notifications (e.g. hazard status
 * changes). Captures/stores the FCM token and logs incoming messages only —
 * no notification channel, display, or server token-upload is implemented
 * yet. Wire those up once notifications are actually prioritized.
 */
public class SmartRoadFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "SmartRoadFcm";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "FCM token refreshed");
        new SessionManager(getApplicationContext()).saveFcmToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "FCM message received: " + remoteMessage.getMessageId());
    }
}
