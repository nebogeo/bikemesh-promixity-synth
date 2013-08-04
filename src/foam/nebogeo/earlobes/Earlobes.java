/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foam.nebogeo.earlobes;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.Toast;
import android.net.wifi.WifiManager;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class Earlobes extends Activity implements SensorEventListener{

    static final int CLIP_NONE = 0;
    static final int CLIP_HELLO = 1;
    static final int CLIP_ANDROID = 2;
    static final int CLIP_SAWTOOTH = 3;
    static final int CLIP_PLAYBACK = 4;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    static String URI;
    static AssetManager assetManager;

    static boolean isPlayingAsset = false;
    static boolean isPlayingUri = false;

    static int numChannelsUri = 0;

    WifiManager wifi;
	BroadcastReceiver receiver;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main);

        assetManager = getAssets();

		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        wifi.startScan();

		if (receiver == null)
			receiver = new WiFiScanReceiver();

		registerReceiver(receiver, new IntentFilter(
                             WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        // initialize native audio system

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //get the accelerometer sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        createEngine();

        ((SeekBar) findViewById(R.id.volume_uri)).setOnSeekBarChangeListener(
            new OnSeekBarChangeListener() {
                int lastProgress = 100;
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    assert progress >= 0 && progress <= 100;
                    lastProgress = progress;
                    setParam(0,progress);
                }
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });


        ((SeekBar) findViewById(R.id.pan_uri)).setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
            int lastProgress = 100;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                assert progress >= 0 && progress <= 100;
                lastProgress = progress;
                setParam(1,progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ((SeekBar) findViewById(R.id.mix)).setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
            int lastProgress = 100;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                assert progress >= 0 && progress <= 100;
                lastProgress = progress;
                setParam(2,progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        ((SeekBar) findViewById(R.id.mesh)).setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
            int lastProgress = 100;
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                assert progress >= 0 && progress <= 100;
                lastProgress = progress;
                setParam(3,progress);
            }
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event)
    {
        // Many sensors return 3 values, one for each axis.
        int x = (int)(event.values[0]*10);
        int y = (int)(event.values[1]*10);
        int z = (int)(event.values[2]*10);



        int m = -((WiFiScanReceiver)receiver).Level;
        if (m<0) m=0;
        if (m>100) m=100;

        if (x<0) x=0;
        if (y<0) y=0;
        if (z<0) z=0;
        if (x>100) x=100;
        if (y>100) y=100;
        if (z>100) z=100;

//        setParam(0,x);
//        setParam(1,y);
//        setParam(2,z);

        ((SeekBar) findViewById(R.id.volume_uri)).setProgress(x);
        ((SeekBar) findViewById(R.id.pan_uri)).setProgress(y);
        ((SeekBar) findViewById(R.id.mix)).setProgress(z);
        ((SeekBar) findViewById(R.id.mesh)).setProgress(m);

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /** Called when the activity is about to be destroyed. */
    @Override
    protected void onPause()
    {
/*        // turn off all audio
        selectClip(CLIP_NONE, 0);
        isPlayingAsset = false;
        setPlayingAssetAudioPlayer(false);
        isPlayingUri = false;
        setPlayingUriAudioPlayer(false);*/
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    /** Called when the activity is about to be destroyed. */
    @Override
    protected void onDestroy()
    {
//        shutdown();
        super.onDestroy();
    }

    /** Native methods, implemented in jni folder */
    public static native void createEngine();
    public static native void setParam(int which, int amount);

    /** Load jni .so on initialization */
    static {
         System.loadLibrary("earlobes");
    }

}
