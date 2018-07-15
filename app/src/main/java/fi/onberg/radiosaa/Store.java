package fi.onberg.radiosaa;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class Store<T> {
    private static final String TAG = Store.class.getSimpleName();
    private Reducer reducer;
    private ApplicationState state;
    private IntentFilter receiveFilter;
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver stateReceiver;
    private Set<Runnable> subscribers;

    public Store(Context context, Reducer reducer){
        this.reducer = reducer;
        this.receiveFilter = new IntentFilter(reducer.getReceiveChannel());
        this.broadcastManager = LocalBroadcastManager.getInstance(context);
        this.subscribers = new HashSet<>();

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

    public void subscribe(Runnable callback){
        subscribers.add(callback);
    }

    public void unsubscribe(Runnable callback){
        subscribers.remove(callback);
    }

    public ApplicationState getState(){
        return state;
    }

    public void dispatch(String action, T value){
        Intent intent = populateIntent(action, value);
        Log.d(TAG, "Dispatching " + action + " with " + value + " on " + reducer.getSendChannel());
        broadcastManager.sendBroadcast(intent);
        if(!subscribers.isEmpty()){
            for(Runnable callback : subscribers){
                callback.run();
            }
        }
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
