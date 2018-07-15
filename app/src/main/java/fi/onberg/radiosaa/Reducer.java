package fi.onberg.radiosaa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public abstract class Reducer {
    private static final String TAG = Reducer.class.getSimpleName();
    private LocalBroadcastManager broadcastManager;
    private String channel;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Unwrap reduce request
            String action = intent.getStringExtra("action");
            String returnChannel = intent.getStringExtra("return");
            Bundle values = intent.getBundleExtra("values");
            ApplicationState state = (ApplicationState) intent.getSerializableExtra("state");

            Log.d(TAG, "Received a reduce call for " + action);

            // Update state
            ApplicationState updatedState = reduce(state, action, values);
            Log.d(TAG, "Updated state:" + updatedState);

            // Send state back
            Intent returnState = new Intent(returnChannel);
            returnState.putExtra("state", updatedState);
            broadcastManager.sendBroadcast(returnState);
            Log.d(TAG, "Sent message back on channel " + returnChannel);
        }
    };

    public Reducer(Context context, String channel){
        this.channel = channel;

        IntentFilter filter = new IntentFilter(channel);
        broadcastManager = LocalBroadcastManager.getInstance(context);
        broadcastManager.registerReceiver(receiver, filter);
        Log.d(TAG, "Registered reducer listening to " + channel);
    }

    public String getSendChannel(){
        return channel;
    }

    public String getReceiveChannel(){
        return channel + "_R";
    }

    abstract ApplicationState reduce(ApplicationState state, String action, Bundle value);
}
