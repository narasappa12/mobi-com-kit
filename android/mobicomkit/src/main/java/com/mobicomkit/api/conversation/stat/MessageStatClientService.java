package com.mobicomkit.api.conversation.stat;

import android.content.Context;

import com.mobicomkit.api.HttpRequestUtils;
import com.mobicomkit.api.MobiComKitClientService;

import net.mobitexter.mobiframework.json.GsonUtils;

/**
 * Created by devashish on 26/12/14.
 */
public class MessageStatClientService extends MobiComKitClientService {

    private static final String TAG = "MessageStatClientService";
    public static final String MESSAGE_STAT_URL =  "/rest/ws/sms/stat/update";


    public MessageStatClientService(Context context) {
        super(context);
    }

    public  String getMessageStatUrl() {
        return getBaseUrl() + MESSAGE_STAT_URL;
    }


    public String sendMessageStat(MessageStat messageStat) {
        return new HttpRequestUtils(context).postData(credentials, getMessageStatUrl(), "application/json", null, GsonUtils.getJsonFromObject(messageStat, MessageStat.class));
    }

}
