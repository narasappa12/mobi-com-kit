package com.mobicomkit.client.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobicomkit.broadcast.MobiComKitBroadcastReceiver;
import com.mobicomkit.GeneralConstants;
import com.mobicomkit.client.ui.MobiComKitApplication;
import com.mobicomkit.client.ui.R;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import com.mobicomkit.client.ui.message.MessageIntentService;
import com.mobicomkit.client.ui.message.conversation.ConversationFragment;
import com.mobicomkit.client.ui.message.conversation.QuickConversationFragment;
import com.mobicomkit.broadcast.BroadcastService;
import com.mobicomkit.communication.message.Message;
import com.mobicomkit.communication.message.MobiComMessageService;
import com.mobicomkit.instruction.InstructionUtil;
import com.mobicomkit.user.MobiComUserPreference;
import com.mobicomkit.user.UserClientService;
import com.mobicomkit.userinterface.MobiComActivity;
import net.mobitexter.mobiframework.commons.core.utils.Support;
import net.mobitexter.mobiframework.people.activity.PeopleActivity;


/**
 * Created by devashish on 8/3/14.
 */

//http://gmariotti.blogspot.in/2013/05/working-with-slidingpanelayout.html
public class SlidingPaneActivity extends MobiComActivity {

    private static final String TAG = "SlidingPaneActivity";

    private LocationRequest locationRequest;

    private ProgressDialog resProgressBar;

    protected GoogleApiClient googleApiClient;

    private static int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_OK = -1;

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onStop() {
        /*if (mLocationClient != null) {
            mLocationClient.disconnect();
        }*/
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        title = MobiComKitApplication.TITLE;
        HOME_BUTTON_ENABLED = true;
        MobiComUserPreference userPreferences = MobiComUserPreference.getInstance(this);

        setContentView(R.layout.sliding_pane);

        mActionBar = getSupportActionBar();
        slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);

        slidingPaneLayout.setPanelSlideListener(new SliderListener());
        slidingPaneLayout.openPane();

        slidingPaneLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new FirstLayoutListener());

        quickConversationFragment = (QuickConversationFragment) getSupportFragmentManager().findFragmentById(R.id.quick_conversation_fragment_pane);
        conversationFragment = (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation_fragment_pane);

        mobiComKitBroadcastReceiver = new MobiComKitBroadcastReceiver(quickConversationFragment, conversationFragment);
        InstructionUtil.showInfo(this, R.string.info_message_sync, BroadcastService.INTENT_ACTIONS.INSTRUCTION.toString());

        SharedPreferences prefs = getSharedPreferences("net.mobitexter", Context.MODE_PRIVATE);
        if (prefs.getBoolean(GeneralConstants.SHARED_PREFERENCE_VERSION_UPDATE_KEY, false)) {
            new UserClientService(this).updateCodeVersion(GeneralConstants.APP_CODE_VERSION, userPreferences.getDeviceKeyString());
            prefs.edit().remove(GeneralConstants.SHARED_PREFERENCE_VERSION_UPDATE_KEY).commit();
        }

        /*googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();*/

        checkForStartNewConversation(getIntent());

       /* AppRater appRater = new AppRater();
        appRater.appLaunched(this);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /*
       * The action bar up action should open the slider if it is currently
       * closed, as the left pane contains content one level up in the
       * navigation hierarchy.
       */
        if (item.getItemId() == android.R.id.home && !slidingPaneLayout.isOpen()) {
            slidingPaneLayout.openPane();
            return true;
        }

        //Note:  using if-else and not switch as resource constants in library projects are not final
        Intent intent;
        int i = item.getItemId();
        if (i == R.id.start_new) {
            startContactActivityForResult();
        } else if (i == R.id.dial) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + (Support.isSupportNumber(currentOpenedContactNumber) ? GeneralConstants.MOBITEXTER_SUPPORT_DIAL_PHONE_NUMBER : currentOpenedContactNumber)));
            startActivity(callIntent);
        } else if (i == R.id.shareOptions) {
            intent = new Intent(Intent.ACTION_SEND);
            String textToShare = this.getResources().getString(R.string.invite_message);
            intent.setAction(Intent.ACTION_SEND)
                    .setType("text/plain").putExtra(Intent.EXTRA_TEXT, textToShare);
            startActivity(Intent.createChooser(intent, "Share Via"));
        } else if (i == R.id.refresh) {
            new MobiComMessageService(this, MessageIntentService.class).syncMessagesWithServer();
        } else if (i == R.id.support) {
            openConversationFragment(Support.getSupportContact());
        } else if (i == R.id.deleteConversation) {
            conversationFragment.deleteConversationThread();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mobicom_basic_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void startContactActivityForResult() {
        startContactActivityForResult(null, null);
    }

    @Override
    public void startContactActivityForResult(Message sms, String message) {
        final Activity activity = this;
        Intent intent = new Intent(this, PeopleActivity.class);

       /* ArrayList<String> mtContactIds = (ArrayList<String>) ContactService.getMTContactIds(activity);
        if (mtContactIds == null || mtContactIds.isEmpty()) {
            if (!UserPreferences.getInstance(activity).isMobiTexterContactSyncCompleted()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContactService.updateMobiTexterUsers(activity);
                    }
                }).start();
            } else {
                intent.putExtra(MobiTexterContactsListFragment.SYNC_STATUS, false);
            }
        }
        intent.putStringArrayListExtra(MobiTexterContactsListFragment.MT_CONTACTS,  mtContactIds);*/

        super.startContactActivityForResult(intent, sms, message);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == LOCATION_SERVICE_ENABLE) {
            if (((LocationManager) getSystemService(Context.LOCATION_SERVICE))
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                googleApiClient.connect();
            } else {
                Toast.makeText(SlidingPaneActivity.this, R.string.unable_to_fetch_location, Toast.LENGTH_LONG).show();
            }
            return;
        } /*else if (resultCode == MultimediaOptionFragment.REQUEST_CODE_ATTACH_PHOTO ||
                resultCode == MultimediaOptionFragment.REQUEST_CODE_TAKE_PHOTO && intent != null) {*/

        if (requestCode == REQUEST_CODE_CONTACT_GROUP_SELECTION) {
            if (resultCode == RESULT_OK) {
                checkForStartNewConversation(intent);
            }
        }
    }

    public void processLocation() {

    }

    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this,
                CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
    }

}
