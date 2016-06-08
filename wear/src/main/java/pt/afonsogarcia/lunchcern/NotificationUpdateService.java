package pt.afonsogarcia.lunchcern;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import static com.google.android.gms.wearable.PutDataRequest.WEAR_URI_SCHEME;

public class NotificationUpdateService extends WearableListenerService {

    public static final String ACTION_DISMISS = "pt.afonsogarcia.lunchcern.DISMISS";
    public static final String NOTIFICATION_PATH = "/lunchAtCERN";
    public static final String GROUP_KEY = "CERN_GROUP";
    private DismissNotificationCommand dismissNotificationCommand;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        dismissNotificationCommand = new DismissNotificationCommand();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(dismissNotificationCommand)
                .addOnConnectionFailedListener(dismissNotificationCommand)
                .build();

        mGoogleApiClient.connect();

        dismissNotificationCommand.setmGoogleApiClient(mGoogleApiClient);

        Log.d("CERN-NOT", "Starting service");
        if (null != intent) {
            String action = intent.getAction();
            if (ACTION_DISMISS.equals(action)) {
                dismissNotification();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("CERN-NOT", "Receiving it");
        for(DataEvent dataEvent: dataEvents) {
            if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
                if (NOTIFICATION_PATH.equals(dataEvent.getDataItem().getUri().getPath())) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    Integer dayOfWeek = dataMapItem.getDataMap().getInt("pt.afonsogarcia.lunchcern.dayOfWeek");
                    sendNotification("Lunch@CERN", String.valueOf(dayOfWeek));
                }
            }
        }
    }

    private void sendNotification(String title, String content) {

        // this intent will open the activity when the user taps the "open" action on the notification
        Intent viewIntent = new Intent(this, DailyMenu.class);
        PendingIntent pendingViewIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        // this intent will be sent when the user swipes the notification to dismiss it
        Intent dismissIntent = new Intent(ACTION_DISMISS);
        PendingIntent pendingDeleteIntent = PendingIntent.getService(this, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(((BitmapDrawable) ResourcesCompat
                        .getDrawable(getResources(), R.mipmap.ic_launcher, null)).getBitmap())
                .setContentTitle(title)
                .setContentText(getResources().getString(R.string.lunch_message))
                .setDeleteIntent(pendingDeleteIntent)
                .setContentIntent(pendingViewIntent)
                .setGroup(GROUP_KEY)
                .setPriority(Notification.PRIORITY_HIGH)
                .setGroupSummary(false)
                .setVibrate(new long[] {500, 500});

        Notification notification = builder.build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, notification);
    }

    private void dismissNotification() {
        dismissNotificationCommand.execute();
    }


    private class DismissNotificationCommand implements GoogleApiClient.ConnectionCallbacks, ResultCallback<DataApi.DeleteDataItemsResult>, GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "DismissNotification";

        private GoogleApiClient mGoogleApiClient;

        public void setmGoogleApiClient(GoogleApiClient mGoogleApiClient) {
            this.mGoogleApiClient = mGoogleApiClient;
        }

        public void execute() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            final Uri dataItemUri =
                    new Uri.Builder().scheme(WEAR_URI_SCHEME).path(NOTIFICATION_PATH).build();
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Deleting Uri: " + dataItemUri.toString());
            }
            Wearable.DataApi.deleteDataItems(
                    mGoogleApiClient, dataItemUri).setResultCallback(this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "onConnectionSuspended");
        }

        @Override
        public void onResult(@NonNull DataApi.DeleteDataItemsResult deleteDataItemsResult) {
            if (!deleteDataItemsResult.getStatus().isSuccess()) {
                Log.e(TAG, "dismissWearableNotification(): failed to delete DataItem");
            }
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "onConnectionFailed");
        }
    }
}