package net.mobitexter.mobiframework.commons.core.utils;

import android.content.Context;
import android.telephony.PhoneNumberUtils;

import net.mobitexter.mobiframework.people.contact.Contact;

/**
 * Created by devashish on 2/12/14.
 */
public class Support {
    public static final String SUPPORT_INTENT_KEY = "SUPPORT_INTENT_KEY";
    private static final String SUPPORT_PHONE_NUMBER_METADATA = "com.mobicomkit.support.phone.number";
    private String SUPPORT_PHONE_NUMBER;

    public Support(Context context) {
        this.SUPPORT_PHONE_NUMBER = Utils.getMetaDataValue(context, SUPPORT_PHONE_NUMBER_METADATA);
    }

    public String getSupportNumber() {
        return SUPPORT_PHONE_NUMBER;
    }

    public boolean isSupportNumber(String contactNumber) {
        return PhoneNumberUtils.compare(getSupportNumber(), contactNumber);
    }

    public Contact getSupportContact() {
        Contact contact = new Contact();
        contact.setFirstName("Support");
        contact.setLastName("");
        contact.setFullName("Support");
        contact.setContactNumber(getSupportNumber());
        contact.setFormattedContactNumber(getSupportNumber());
        return contact;
    }
}
