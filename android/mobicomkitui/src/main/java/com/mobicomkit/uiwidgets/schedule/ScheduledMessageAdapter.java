package com.mobicomkit.uiwidgets.schedule;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.mobitexter.mobiframework.commons.image.ImageUtils;
import net.mobitexter.mobiframework.people.contact.Contact;
import net.mobitexter.mobiframework.people.contact.ContactUtils;
import net.mobitexter.mobiframework.commons.image.ImageLoader;

import com.mobicomkit.uiwidgets.R;
import com.mobicomkit.api.conversation.Message;

import java.text.DateFormat;
import java.util.*;


public class ScheduledMessageAdapter extends ArrayAdapter<Message> {
    private ImageLoader mImageLoader;
    public ScheduledMessageAdapter(Context context, int textViewResourceId, List<Message> smsList) {
        super(context, textViewResourceId, smsList);
        mImageLoader = new ImageLoader(getContext(), ImageUtils.getLargestScreenDimension((Activity) getContext())) {
            @Override
            protected Bitmap processBitmap(Object data) {
                return loadContactPhoto((Uri) data, getImageSize());
            }
        };
        mImageLoader.setLoadingImage(R.drawable.ic_contact_picture_180_holo_light);
        mImageLoader.setImageFadeIn(false);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = convertView;

        if (customView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            customView = inflater.inflate(R.layout.mobicom_message_row_view, null);
            ImageView sentOrReceived = (ImageView) customView.findViewById(R.id.sentOrReceivedIcon);
            sentOrReceived.setVisibility(View.GONE);
            TextView unreadSmsTxtView = (TextView) customView.findViewById(R.id.unreadSmsCount);
            unreadSmsTxtView.setVisibility(View.GONE);
        }
        Message messageListItem = getItem(position);
        if (messageListItem != null) {
            TextView smReceivers = (TextView) customView.findViewById(R.id.smReceivers);
            TextView smTime = (TextView) customView.findViewById(R.id.createdAtTime);
            //TextView createdAtTime = (TextView) customView.findViewById(R.id.createdAtTime);
            TextView scheduledMessage = (TextView) customView.findViewById(R.id.message);
            ImageView contactImage = (ImageView) customView.findViewById(R.id.contactImage);


            if (smReceivers != null) {
                List<String> items = Arrays.asList(messageListItem.getTo().split("\\s*,\\s*"));
                Contact contact1 = ContactUtils.getContact(getContext(), items.get(0));
                String contactinfo = TextUtils.isEmpty(contact1.getFirstName()) ? contact1.getContactNumber() : contact1.getFirstName();
                if (items.size() > 1) {
                    Contact contact2 = ContactUtils.getContact(getContext(), items.get(1));
                    contactinfo = contactinfo + " , " + (TextUtils.isEmpty(contact2.getFirstName()) ? contact2.getContactNumber() : contact2.getFirstName());

                }
                smReceivers.setText(contactinfo.length() > 22 ? contactinfo.substring(0, 22) + "..." : contactinfo);

                String contactId = ContactUtils.getContactId(items.get(0), getContext().getContentResolver());
                //Todo: Check if contactId is working or not.
                Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
                mImageLoader.loadImage(contactUri, contactImage);
            }

            if (smTime != null) {
                Calendar calendarForCurrent = Calendar.getInstance();
                Calendar calendarForScheduled = Calendar.getInstance();
                Date currentDate = new Date();
                Date date = new Date(messageListItem.getScheduledAt());
                calendarForCurrent.setTime(currentDate);
                calendarForScheduled.setTime(date);
                boolean sameDay = calendarForCurrent.get(Calendar.YEAR) == calendarForScheduled.get(Calendar.YEAR) &&
                        calendarForCurrent.get(Calendar.DAY_OF_YEAR) == calendarForScheduled.get(Calendar.DAY_OF_YEAR);

                String formattedDate = sameDay ? DateFormat.getTimeInstance().format(date) : DateFormat.getDateInstance().format(date);
                smTime.setText(formattedDate);
            }

            if (scheduledMessage != null) {
                scheduledMessage.setText(messageListItem.getMessage());
            }
        }
        return customView;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private Bitmap loadContactPhoto(Uri contactUri, int imageSize) {
        if (getContext() == null) {
            return null;
        }
        return ContactUtils.loadContactPhoto(contactUri, imageSize, (Activity) getContext());
    }
}
