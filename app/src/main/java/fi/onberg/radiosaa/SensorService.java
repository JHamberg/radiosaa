package fi.onberg.radiosaa;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

@SuppressWarnings("FieldCanBeLocal")
public class SensorService extends Service {
    private static final String TAG = SensorService.class.getSimpleName();
    private final int SERVICE_ID = 1484049303; // Randomly generated to avoid collisions
    private final String SERVICE_CHANNEL = "RadioSaa";
    private SensorManager sensorManager;
    private Sensor temperatureSensor;
    private TemperatureListener temperatureListener;
    private ConnectionListener connectionListener;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        RadiosaaApplication application = (RadiosaaApplication)getApplication();
        Notification notification = getForegroundNotification();
        startForeground(SERVICE_ID, notification);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        Store<Double> sensorStore = application.getSensorStore();
        registerTemperatureSensor(sensorStore);
        registerConnectionListener(sensorStore);

        return START_STICKY;
    }

    public void registerConnectionListener(Store<Double> sensorStore){
        connectionListener = new ConnectionListener(this, sensorStore, 1000);
    }

    public void registerTemperatureSensor(Store<Double> sensorStore){
        temperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        temperatureListener = new TemperatureListener(sensorStore);
        sensorManager.registerListener(temperatureListener, temperatureSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.i(TAG, "Registered pressure listener");
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(temperatureListener, temperatureSensor);
        connectionListener.destroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // This is not a bound service
        return null;
    }

    private Notification getForegroundNotification(){
        // Create a pending intent to launch the main activity
        PendingIntent launchIntent = RadiosaaApplication.getLaunchIntent(getApplicationContext());

        // Setup an ongoing notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, SERVICE_CHANNEL);
        builder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentText(getString(R.string.app_name))
                .setContentIntent(launchIntent);

        // Build the notification
        return builder.build();
    }
}
