package com.example.masayuki.test3;

import android.app.Activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;



public class MainActivity extends Activity {

    private final static int REQUEST_TEXT = 0;  // text ID

    private SensorManager manager;
    private Sensor sensor;
    private SensorEventListener sample_listener;

    private TextView textView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);
        textView.setText("test");

        sample_listener = new SampleSensorEventListener();



        /*
        //
        Intent intent=new Intent();
        intent.setClassName("com.example.masayuki.test3","com.example.masayuki.test3.AnoterhActivity");
        //Intent intent = new Intent(this, com.example.masayuki.test3.CameraActivity.class);
        //
        startActivity(intent);
        */

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onResume(){
        super.onResume();
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor  = manager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        manager.registerListener(sample_listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause(){
        super.onPause();
        manager.unregisterListener(sample_listener);
    }


}
