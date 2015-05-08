package com.mobicomkit.uiwidgets.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.mobicomkit.MobiComKitConstants;
import com.mobicomkit.client.ui.R;
import com.mobicomkit.communication.message.Message;
import com.mobicomkit.notification.NotificationService;

import net.mobitexter.mobiframework.json.GsonUtils;
import net.mobitexter.mobiframework.people.contact.Contact;
import net.mobitexter.mobiframework.people.contact.ContactUtils;

/**
 * Created by adarsh on 3/5/15.
 */
public class MTNotificationBroadcastReceiver extends BroadcastReceiver {


    private static final String TAG = "MTBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        
        String action = intent.getAction();
        Message message = null;
        String smsXmlString = intent.getStringExtra(MobiComKitConstants.MESSAGE_JSON_INTENT);
        Log.i(TAG, "Received broadcast, action: " + action + ", sms: " + message);
        if (!TextUtils.isEmpty(smsXmlString)) {
            message = (Message) GsonUtils.getObjectFromJson(smsXmlString, Message.class);
        }
        NotificationService notificationService =
                new NotificationService(R.drawable.ic_launcher,context,R.string.wearable_action_label,R.string.wearable_action_title,R.drawable.ic_action_send);

        Log.i(TAG, "Received broadcast, action: " + action + ", sms: " + message);
        Contact contact = ContactUtils.getContact(context, message.getContactIds());

        notificationService.notifyUser(contact,message);

    }
}