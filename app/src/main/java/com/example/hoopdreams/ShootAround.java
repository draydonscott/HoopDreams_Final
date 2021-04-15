package com.example.hoopdreams;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hoopdreams.ui.main.ShootAroundHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShootAround extends AppCompatActivity {
    Button buttonShotMade;
    Button buttonShotMiss;
    Button buttonEndSession;
    TextView shotsMadetxt;
    TextView shotAttemptstxt;
    TextView shootingPercentagetxt;
    TextView shotStreaktxt;
    Chronometer timeElapsed;
    ShootAroundHelper helper;
    ShotData RecentShot;

    private boolean running = false;
    public static final String TAG = "ShootAround";
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String receiveBuffer = "";
    private byte[] receive;
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String RAW_DATA =
            "com.example.bluetooth.le.RAW_DATA";
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String date;
        setContentView(R.layout.activity_shoot_around);
        buttonShotMade = findViewById(R.id.buttonShotMade);
        shotsMadetxt = findViewById(R.id.shotsMadetxt);
        shotAttemptstxt = findViewById(R.id.shotAttempstxt);
        buttonShotMiss = findViewById(R.id.buttonShotMiss);
        shotStreaktxt = findViewById(R.id.shotStreaktxt);
        timeElapsed = findViewById(R.id.timeElapsed);
        shootingPercentagetxt = findViewById(R.id.shootingPercentagetxt);
        buttonEndSession = findViewById(R.id.buttonEndSession);
        helper = new ShootAroundHelper();
        RecentShot = new ShotData();
        DataBaseHelper databasehelper = new DataBaseHelper(ShootAround.this);
        date = helper.getDate();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setTitle("Shoot Around");

        RecentShot.ShotsMadeString = ("Shots Made: 0");
        RecentShot.ShotsAttemptedString = ("Shots Attempted: 0");
        RecentShot.ShotStreakString = ("Shot Streak: 0");
        RecentShot.ShootingPercentageString = ("Shooting Percentage: 0");

        displayStats(RecentShot);
        if( !running ){
            timeElapsed.setBase(SystemClock.elapsedRealtime());
            timeElapsed.start();
            running = true;
        }

        buttonShotMade.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RecentShot = helper.updateStats("1", shotsMadetxt, shotAttemptstxt);
               displayStats(RecentShot);
            }
        });

        buttonShotMiss.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                RecentShot = helper.updateStats("0", shotsMadetxt, shotAttemptstxt);
                displayStats(RecentShot);
            }
        });

        buttonEndSession.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v){
                boolean success = databasehelper.addOne(helper.getTime(),helper.getShotsMade(),helper.getShotsTaken(),date,"Shoot Around");
                Toast.makeText(ShootAround.this,helper.calculateExperience(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

    private void displayStats(ShotData shot){
        shotsMadetxt.setText(shot.ShotsMadeString);
        shotAttemptstxt.setText(shot.ShotsAttemptedString);
        shootingPercentagetxt.setText(shot.ShootingPercentageString);
        shotStreaktxt.setText(shot.ShotStreakString);
        Log.d(TAG,"Updating Stats");
    }

    public void onResume(){
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        //Log.d(TAG,mDeviceName);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);

        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        if(mBluetoothLeService!=null){
            iterateGattServices(mBluetoothLeService.getSupportedGattServices());
        }

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Toast.makeText(ShootAround.this,"Connected to HoopDreamsDevice!",Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                Log.d(TAG,"Connected");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(ShootAround.this,"Disconnected from HoopDreamsDevice!",Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                iterateGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                receiveBuffer += intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                receive = intent.getByteArrayExtra(RAW_DATA);

                Log.d(TAG,"received in SA");
                if(receiveBuffer.contains("\n")) {
                    receiveBuffer = receiveBuffer.substring(0, receiveBuffer.length() - 1);
                    messageHandler();
                    receiveBuffer = "";

                }
            }
        }
    };
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void messageHandler() {
        if (receiveBuffer != null) {
            Toast.makeText(ShootAround.this, receiveBuffer, Toast.LENGTH_SHORT).show();
        }
        for(byte bi:receive){
            Log.d(TAG, "Byte value: "+ bi);
        }
        RecentShot = helper.updateStats(receive[0], shotsMadetxt, shotAttemptstxt);

        displayStats(RecentShot);
        Log.d(TAG,"Attempt to write char");
        mBluetoothLeService.writeCharacteristic(helper.getFeedback());
    }
    private void iterateGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            gattServiceData.add(currentServiceData);
            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                uuid = gattCharacteristic.getUuid().toString();
                //custom code
                if(uuid.equals("19b10002-0000-1000-8000-00805f9b34fb") && mNotifyCharacteristic == null) {
                    mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                    Log.d(TAG,"Notification true");
                    mNotifyCharacteristic = gattCharacteristic;
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            iterateGattServices(mBluetoothLeService.getSupportedGattServices());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

}