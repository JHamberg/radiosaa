package fi.onberg.radiosaa;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Random;

public class RadiosaaApplication extends Application {
    private static final String TAG = RadiosaaApplication.class.getSimpleName();
    private int APPLICATION_ID;
    private Store<Double> sensorStore;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initStore();
        APPLICATION_ID = new Random().nextInt(Integer.MAX_VALUE);

        Log.i(TAG,  "Application instance " + APPLICATION_ID + " started!");
    }

    @Override
    public void onTerminate() {
        sensorStore.destroy();
        super.onTerminate();
    }

    public void initStore(){
        sensorStore = new Store<>(context, new Reducer(context, "SENSOR_STORE") {
            @Override
            ApplicationState reduce(ApplicationState state, String action, Bundle value) {
                Log.d(TAG, "Received " + action);
                ApplicationState.Builder stateBuilder = new ApplicationState.Builder(state);
                switch(action){
                    case "UPDATE_TEMPERATURE":
                        double pressure = value.getDouble("UPDATE_TEMPERATURE");
                        stateBuilder.setTemperature(pressure);
                        break;
                    case "UPDATE_CONNECTION":
                        double connection = value.getDouble("UPDATE_CONNECTION");
                        stateBuilder.setConnection(connection);
                        break;
                }
                return stateBuilder.build();
            }
        });
    }

    public Store<Double> getSensorStore(){
        return sensorStore;
    }

    public static PendingIntent getLaunchIntent(@NonNull Context context){
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
