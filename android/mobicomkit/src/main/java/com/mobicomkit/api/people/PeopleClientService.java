package com.mobicomkit.api.people;

import android.content.Context;

import com.mobicomkit.api.HttpRequestUtils;
import com.mobicomkit.api.MobiComKitClientService;

import net.mobitexter.mobiframework.json.GsonUtils;

/**
 * Created by devashish on 27/12/14.
 */
public class PeopleClientService extends MobiComKitClientService {
    private HttpRequestUtils httpRequestUtils;
    public static final String GOOGLE_CONTACT_URL = "/rest/ws/user/session/contact/google/list";
    public static final String PLATFORM_CONTACT_URL = "/rest/ws/user/session/contact/google/list";



    public PeopleClientService(Context context) {
        super(context);
        this.httpRequestUtils = new HttpRequestUtils(context);
    }

    public  String getGoogleContactUrl() {
        return getBaseUrl() + GOOGLE_CONTACT_URL;
    }

    public  String getPlatformContactUrl() {
        return getBaseUrl() + PLATFORM_CONTACT_URL;
    }


    public String getGoogleContacts(int page) {
        return httpRequestUtils.getResponse(credentials,getGoogleContactUrl() + "?page=" + page, "application/json", "application/json");
    }

    public String getContactsInCurrentPlatform() {
        return httpRequestUtils.getResponse(credentials, getPlatformContactUrl() + "?mtexter=true", "application/json", "application/json");
    }

    public void addContacts(String url, ContactList contactList, boolean completed) throws Exception {
        String requestString = GsonUtils
                .getJsonWithExposeFromObject(contactList, ContactList.class);
        if (completed) {
            url = url + "?completed=true";
        }
        httpRequestUtils.postData(credentials, url, "application/json", null, requestString);
    }
}
