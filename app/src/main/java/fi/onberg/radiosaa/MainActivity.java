package fi.onberg.radiosaa;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import fi.onberg.radiosaa.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainView;
    private Store<Double> store;
    private Runnable render = new Runnable() {
        @Override
        public void run() {
            String connection = "Connection: " + store.getState().getConnection() +"dBm";
            mainView.connection.setText(connection);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainView = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final RadiosaaApplication application = (RadiosaaApplication)getApplication();
        store = application.getSensorStore();
        store.subscribe(render);
        
        startService(new Intent(this, SensorService.class));
        initListeners();
    }

    private void initListeners(){
        mainView.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                render.run();
            }
        });
    }
}
