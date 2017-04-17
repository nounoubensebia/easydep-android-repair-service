package com.example.nouno.easydep_repairservice.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.nouno.easydep_repairservice.Activities.AssistanceRequestInfoActivity;
import com.example.nouno.easydep_repairservice.Activities.MainActivity;
import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nouno on 15/04/2017.
 */

public class FireBaseMessangingService extends FirebaseMessagingService {

    private static final String TITLE_NEW_REQUEST = "new_request";
    private static final String TITLE_NEW_USER_IN_QUEUE = "new_user_in_queue";
    private static final String TITLE_NEW_INTERVENTION="new_intervention";
    private static final String TITLE_INTERVENTION_CANCELED = "intervention_canceled";
    private LocalBroadcastManager broadcaster;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.i("MESSAGE",remoteMessage.getData().get("data"));
        handleMessage(remoteMessage.getData().get("data"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    private void handleMessage(String msg)
    {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String title = jsonObject.getString("title");
            if (title.equals(TITLE_NEW_REQUEST))
            {
                showNewRequestNotification(msg);
            }
            if (title.equals(TITLE_NEW_INTERVENTION))
            {
                showNewInterventionNotification(msg);
            }

            if (title.equals(TITLE_NEW_USER_IN_QUEUE))
            {
                showNewUserInQueueNotification(msg);
            }
            if (title.equals(TITLE_INTERVENTION_CANCELED))
            {
                showInterventionCanceledNotification(msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showInterventionCanceledNotification(String msg)
    {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            long id = jsonObject.getLong("assistance_request_id");
            String carowner_fullname = jsonObject.getString("carowner_fullname");
            Intent i = new Intent(this,MainActivity.class);
            showNotification("Intervention annulée",carowner_fullname+" a annulé la demande d'intervention",(int)id,i);
            broadcaster.sendBroadcast(new Intent("new_queue_element"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showNewRequestNotification(String msg)
    {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            Intent i = new Intent(getApplicationContext(),AssistanceRequestInfoActivity.class);
            long id= jsonObject.getLong("assistance_request_id");
            i.putExtra("assistanceRequestId",id);
            i.putExtra("flag",AssistanceRequest.FLAG_ESTIMATE_REQUEST);
            showNotification("Nouvelle demande","Vous venez de recevoir une nouvelle demande de devis",(int)id,i);
            i = new Intent("new_request");
            broadcaster.sendBroadcast(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showNewInterventionNotification(String msg)
    {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String carOwnerFullname = jsonObject.getString("car_owner_fullname");
            long assistanceRequestId = jsonObject.getLong("assistance_request_id");
            Intent i = new Intent(getApplicationContext(),AssistanceRequestInfoActivity.class);
            i.putExtra("assistanceRequestId",assistanceRequestId);
            i.putExtra("flag",AssistanceRequest.FLAG_INTERVENTION);
            showNotification("Nouvelle intervention",carOwnerFullname+" attend votre intervention",(int)assistanceRequestId,i);
            i = new Intent("new_queue_element");
            broadcaster.sendBroadcast(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showNewUserInQueueNotification(String msg)
    {
        try {
            JSONObject jsonObject = new JSONObject(msg);
            String carOwnerFullname = jsonObject.getString("car_owner_fullname");
            long assistanceRequestId = jsonObject.getLong("assistance_request_id");
            Intent i = new Intent(getApplicationContext(),AssistanceRequestInfoActivity.class);
            i.putExtra("assistanceRequestId",assistanceRequestId);
            i.putExtra("flag",AssistanceRequest.FLAG_IN_QUEUE);
            showNotification("Nouvel utilisateur en file d'attente",carOwnerFullname+" a accepté votre devis et est maintenant en file d'attente",(int)assistanceRequestId,i);
            i = new Intent("new_queue_element");
            broadcaster.sendBroadcast(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void showNotification(String title, String message, int id, Intent intent) {
        Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultRingtoneUri)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(pendingIntent);


        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(id, builder.build());

    }
}
