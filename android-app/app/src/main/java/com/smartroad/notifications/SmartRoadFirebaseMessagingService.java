package com.smartroad.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.smartroad.util.SessionManager;

/**
 * Handles Firebase Cloud Messaging lifecycle events for SmartRoad: captures
 * the FCM registration token and receives incoming push messages.
 */
public class SmartRoadFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "SmartRoadFcm";

    // onNewToken(String) is flagged deprecated by the resolved firebase-messaging
    // BOM version with no documented replacement signature.
    @SuppressWarnings("deprecation")
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
