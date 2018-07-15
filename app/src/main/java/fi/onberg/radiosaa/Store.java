package fi.onberg.radiosaa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class Store<T> {
    private static final String TAG = Store.class.getSimpleName();
    private Reducer reducer;
    private ApplicationState state;
    private IntentFilter receiveFilter;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver stateReceiver;

    public Store(Context context, Reducer reducer){
        this.reducer = reducer;
        this.receiveFilter = new IntentFilter(reducer.getReceiveChannel());
        this.broadcastManager = LocalBroadcastManager.getInstance(context);
        this.stateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ApplicationState newState = (ApplicationState) intent.getSerializableExtra("state");
                Log.d(TAG, "State updated from " + state + " to " + newState);
                state = newState;
            }
        };
        broadcastManager.registerReceiver(stateReceiver, receiveFilter);
        dispatch( "@@INIT", null); // Fetch latest state
    }

    public void destroy(){
        broadcastManager.unregisterReceiver(stateReceiver);
    }

    public ApplicationState getState(){
        return state;
    }

    public void dispatch(String action, T value){
        Intent intent = populateIntent(action, value);
        Log.d(TAG, "Dispatching " + action + " with " + value + " on " + reducer.getSendChannel());
        broadcastManager.sendBroadcast(intent);
    }

    private Intent populateIntent(String action, T value){
        Intent intent = new Intent();
        intent.setAction(reducer.getSendChannel());
        intent.putExtra("action", action);
        intent.putExtra("return", reducer.getReceiveChannel());
        intent.putExtra("state", state);

        Bundle values = new Bundle();
        if(value instanceof Integer){
            values.putInt(action, (Integer)value);
        } else if(value instanceof Double){
            values.putDouble(action, (Double)value);
        } else if(value instanceof Float){
            values.putFloat(action, (Float)value);
        } else if(value instanceof Long){
            values.putLong(action, (Long)value);
        } else if(value instanceof String){
            values.putString(action, (String)value);
        } else {
            Log.e(TAG, "Unimplemented type: " + value);
        }
        intent.putExtra("values", values);

        return intent;
    }

}
