package com.example.lightsensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private static final String TAG = "Sensor";


    private SensorManager sensorManager;
    private Sensor mLight;
    TextView light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        light = (TextView) findViewById(R.id.light);


        Log.d(TAG, "onCreate: Initializing Sensor Services");
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (mLight != null) {
            sensorManager.registerListener((SensorEventListener) MainActivity.this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "onCreate: Registered Light Meter Listener");
        }else {
            light.setText("Light meter Not Supported");
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        if (sensor.getType() == Sensor.TYPE_LIGHT) {

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            Long tssLong = System.currentTimeMillis();
            String tss = tssLong.toString();

            light.setText(" "+sensorEvent.values[0]);
            Log.d(TAG, "Time Stamp: " + ts + " Light: " + sensorEvent.values[0]);


        }

    }


}
