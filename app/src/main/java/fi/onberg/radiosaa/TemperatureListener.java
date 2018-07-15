package fi.onberg.radiosaa;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

public class TemperatureListener implements SensorEventListener{
    private static final String TAG = TemperatureListener.class.getSimpleName();
    private Store<Double> store;

    public TemperatureListener(Store<Double> sensorStore){
        this.store = sensorStore;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "Temperature change");
        if(sensorEvent == null) return;
        float[] values = sensorEvent.values;
        double mPa = values[0];
        store.dispatch( "UPDATE_TEMPERATURE", mPa);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // ...
    }
}
