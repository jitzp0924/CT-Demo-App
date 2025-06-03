package com.jitendract.jitdemo;

import static com.bumptech.glide.Glide.init;
import static com.clevertap.android.geofence.CTGeofenceSettings.ACCURACY_HIGH;
import static com.clevertap.android.geofence.CTGeofenceSettings.FETCH_CURRENT_LOCATION_PERIODIC;
import static com.clevertap.android.geofence.CTGeofenceSettings.FETCH_LAST_LOCATION_PERIODIC;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.clevertap.android.geofence.CTGeofenceAPI;
import com.clevertap.android.geofence.CTGeofenceSettings;
import com.clevertap.android.geofence.Logger;
import com.clevertap.android.pushtemplates.PushTemplateNotificationHandler;
import com.clevertap.android.sdk.ActivityLifecycleCallback;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.interfaces.OnInitCleverTapIDListener;
import com.clevertap.android.sdk.pushnotification.CTPushNotificationListener;
import com.clevertap.android.signedcall.enums.SignallingChannel;
import com.clevertap.android.signedcall.enums.VoIPCallStatus;
import com.clevertap.android.signedcall.fcm.SignedCallNotificationHandler;
import com.clevertap.android.signedcall.init.SignedCallAPI;
import com.clevertap.android.signedcall.interfaces.SCNetworkQualityHandler;
import com.clevertap.android.signedcall.interfaces.SCVoIPCallStatusListener;
import com.clevertap.android.signedcall.models.CallDetails;
import com.clevertap.android.signedcall.models.SCCallStatusDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jitendract.jitdemo.CtPE.PEVariables;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class application extends MultiDexApplication implements Application.ActivityLifecycleCallbacks, CTPushNotificationListener {

    public static AppDatabase database;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String cleverTapId;
    CleverTapAPI cleverTapAPI;

    @Override
    public void onCreate() {

        FirebaseApp.initializeApp(this);

        ActivityLifecycleCallback.register(this);
        CleverTapAPI.setDebugLevel(CleverTapAPI.LogLevel.VERBOSE);
        CleverTapUtils.init(this);
        CleverTapAPI cleverTapAPI = CleverTapAPI.getDefaultInstance(getApplicationContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        assert cleverTapAPI != null;
        CleverTapAPI.setNotificationHandler(new PushTemplateNotificationHandler());
        cleverTapAPI.setCTPushNotificationListener(this);
        CleverTapAPI.getDefaultInstance(this).enableDeviceNetworkInfoReporting(true);
        cleverTapAPI.enableDeviceNetworkInfoReporting(true);
        signedCallListerInit();
        cleverTapAPI.getCleverTapID(new OnInitCleverTapIDListener() {
            @Override
            public void onInitCleverTapID(final String cleverTapID) {
                Log.v("ct_objectId",cleverTapID);
                mFirebaseAnalytics.setUserProperty("ct_objectId", cleverTapID);
            }
        });
        CleverTapAPI.setSignedCallNotificationHandler(new SignedCallNotificationHandler());
        SignedCallAPI.getInstance().setNetworkQualityCheckHandler(new SCNetworkQualityHandler() {
            @Override
            public boolean onNetworkQualityResponse(int i) {
                Log.d("SignedCall", "Network quality score: " + i);
                return false;
            }
        });
        PEInit(cleverTapAPI);
        geofenceCTinit(cleverTapAPI);


        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "transactions-db").build();
    }

    private void signedCallListerInit() {

        SignedCallAPI.getInstance().registerVoIPCallStatusListener(new SCVoIPCallStatusListener() {
            @Override
            public void callStatus(final SCCallStatusDetails callStatusDetails) {
                //App is notified on the main thread to notify the changes in the call-state
                Log.d("SignedCall", "callStatus is invoked with: " + callStatusDetails.toString());

                SCCallStatusDetails.CallDirection direction = callStatusDetails.getDirection();
                VoIPCallStatus callStatus = callStatusDetails.getCallStatus();
                CallDetails callDetails = callStatusDetails.getCallDetails();
                String callId = callDetails.callId;
                String requestId = callDetails.requestId;
                SignallingChannel channel = callDetails.channel;

                if (direction.equals(SCCallStatusDetails.CallDirection.OUTGOING)) {
                    //Handle events for initiator of the call

                    if (callStatus == VoIPCallStatus.CALL_IS_PLACED) {
                        // When the call is successfully placed
                    }  else if (callStatus == VoIPCallStatus.CALL_RINGING) {
                        // When the call starts ringing on the receiver's device
                    } else if (callStatus == VoIPCallStatus.CALL_CANCELLED) {
                        // When the call is cancelled from the initiator's end
                    } else if (callStatus == VoIPCallStatus.CALL_CANCELLED_DUE_TO_RING_TIMEOUT) {
                        // When the call is call is cancelled due to a ring timeout.
                        // This event is reported when the SDK fails to establish communication with the receiver, often due to an offline device or a device with low bandwidth.
                    } else if (callStatus == VoIPCallStatus.CALL_DECLINED) {
                        // When the call is declined from the receiver's end
                    } else if (callStatus == VoIPCallStatus.CALL_MISSED) {
                        // When the call is missed at the receiver's end
                    } else if (callStatus == VoIPCallStatus.CALL_ANSWERED) {
                        // When the call is picked up by the receiver
                    } else if (callStatus == VoIPCallStatus.CALL_IN_PROGRESS) {
                        // When the connection to the receiver is established
                    } else if (callStatus == VoIPCallStatus.CALL_OVER) {
                        // When the call has been disconnected
                    }  else if (callStatus == VoIPCallStatus.CALL_OVER_DUE_TO_LOCAL_NETWORK_LOSS) {
                        // When the call is disconnected for the party that experienced a network loss
                    } else if (callStatus == VoIPCallStatus.CALL_OVER_DUE_TO_REMOTE_NETWORK_LOSS) {
                        // When the call has been disconnected for the party that dropped due to network loss at remote end
                    } else if (callStatus == VoIPCallStatus.CALL_OVER_DUE_TO_PROTOCOL_MISMATCH) {
                        // When the call has been disconnected due to protocol mismatch.
                    } else if (callStatus == VoIPCallStatus.CALL_OVER_DUE_TO_NETWORK_DELAY_IN_MEDIA_SETUP) {
                        // When the call disconnects due to network delays that prevent media setup.
                    } else if (callStatus == VoIPCallStatus.CALLEE_BUSY_ON_ANOTHER_CALL) {
                        // When the receiver is busy on another call(includes both VoIP or PSTN)
                    }  else if (callStatus == VoIPCallStatus.CALL_DECLINED_DUE_TO_BUSY_ON_VOIP) {
                        // When the receiver is busy in a VoIP call
                    } else if (callStatus == VoIPCallStatus.CALL_DECLINED_DUE_TO_BUSY_ON_PSTN) {
                        // When the receiver is busy in a PSTN call
                    } else if (callStatus == VoIPCallStatus.CALL_DECLINED_DUE_TO_LOGGED_OUT_CUID) {
                        // When the receiver's cuid is logged out and logged in with different cuid
                    } else if (callStatus == VoIPCallStatus.CALL_DECLINED_DUE_TO_NOTIFICATIONS_DISABLED) {
                        // When the receiver's Notifications Settings are disabled from application settings
                    } else if (callStatus == VoIPCallStatus.CALLEE_MICROPHONE_PERMISSION_NOT_GRANTED) {
                        // When the Microphone permission is denied or blocked while receiver answers the call
                    } else if (callStatus == VoIPCallStatus.CALLEE_MICROPHONE_PERMISSION_BLOCKED) {
                        // When the microphone permission is blocked at the receiver's end.
                    } else if (callStatus == VoIPCallStatus.CALL_FAILED_DUE_TO_INTERNAL_ERROR) {
                        // When the call fails after signalling. Possible reasons could include low internet connectivity, low RAM available on device, SDK fails to set up the voice channel within the time limit
                    } else if(callStatus == VoIPCallStatus.CALL_OVER_DUE_TO_NETWORK_DELAY_IN_CONNECTION_SETUP) {
                        // When the call fails due to network delay during the call setup phase when a call is answered but not yet ready for voice exchange.
                    }
                } else if (direction.equals(SCCallStatusDetails.CallDirection.INCOMING)) {
                    //Handle events for receiver of the call
                    // Receiver will get the same list of events as above so handle accordingly
                }
            }
        });
    }


    private void geofenceCTinit(CleverTapAPI cleverTapAPI) {
//
        Log.v("CTGeofence","Setting Start");
        CTGeofenceSettings ctGeofenceSettings = new CTGeofenceSettings.Builder()
                .setLocationFetchMode(FETCH_CURRENT_LOCATION_PERIODIC)
                .setLocationAccuracy(ACCURACY_HIGH)
                .enableBackgroundLocationUpdates(true)//boolean to enable background location updates
                .setLogLevel(Logger.VERBOSE)//Log Level
                .setGeofenceMonitoringCount(50)//int value for number of Geofences CleverTap can monitor
                .setInterval(TimeUnit.MINUTES.toMillis(30))
                .setGeofenceNotificationResponsiveness(5000)
                .build();

        CTGeofenceAPI.getInstance(getApplicationContext()).init(ctGeofenceSettings,cleverTapAPI);
        CTGeofenceAPI.getInstance(getApplicationContext())
                .setOnGeofenceApiInitializedListener(new CTGeofenceAPI.OnGeofenceApiInitializedListener() {
                    @Override
                    public void OnGeofenceApiInitialized() {
                        //App is notified on the main thread that CTGeofenceAPI is initialized
                        CTGeofenceAPI.getInstance(getApplicationContext()).triggerLocation();

                    }
                });
//
    }

    private void PEInit(CleverTapAPI cleverTapAPI) {

        PEVariables peVariables = new PEVariables(getApplicationContext());
        cleverTapAPI.parseVariables(peVariables);
        cleverTapAPI.syncVariables();

    }


    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
        Log.v("Life Cycle","Created");
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        Log.v("Life Cycle","Started");

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

        Bundle payload = activity.getIntent().getExtras();
        if (payload.containsKey("pt_id")&& payload.getString("pt_id").equals("pt_rating"))
        {
            NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(payload.getInt("notificationId"));
        }
        if (payload.containsKey("pt_id")&& payload.getString("pt_id").equals("pt_product_display"))
        {
            NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.cancel(payload.getInt("notificationId"));
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        Log.v("Life Cycle","Paused");
        HashMap<String, Object> appWentBackground = new HashMap<String, Object>();
        appWentBackground.put("State","Paused");
        appWentBackground.put("Reason","background");
        cleverTapAPI.pushEvent("App_went_background",appWentBackground);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.v("Life Cycle","Stopped");
        HashMap<String, Object> appWentBackground = new HashMap<String, Object>();
        appWentBackground.put("State","Stopped");
        appWentBackground.put("Reason","background");
        cleverTapAPI.pushEvent("App_went_background",appWentBackground);

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        HashMap<String, Object> appWentBackground = new HashMap<String, Object>();
        appWentBackground.put("State","destroyed");
        appWentBackground.put("Reason","Killed");
        cleverTapAPI.pushEvent("App_went_background",appWentBackground);
    }

    @Override
    public void onNotificationClickedPayloadReceived(HashMap<String, Object> payload) {

        Log.e("CleverTap Click", String.valueOf(payload));
        String p1 = String.valueOf(payload);
        Toast.makeText(this,p1, Toast.LENGTH_LONG).show();

    }


}
