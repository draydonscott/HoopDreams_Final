package com.example.hoopdreams;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllAroundTheWorld extends AppCompatActivity {
    TextView playerOneScore;
    TextView playerTwoScore;
    TextView timeText;
    TextView P1;
    TextView P2;
    Chronometer timeElapsed;
    Button endSession;
    AATWHelper helper;
    Button shotMade;
    Button shotMiss;
    Button PlayAgain;

    private boolean running = false;
    private boolean session = false;
    private boolean gameOver = false;
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
        setContentView(R.layout.activity_all_around_the_world);
        final String[] date = new String[1];
        shotMade = findViewById(R.id.buttonShotMade);
        shotMiss = findViewById(R.id.buttonShotMiss);
        playerOneScore = findViewById(R.id.playerOneScore);
        playerTwoScore = findViewById(R.id.playerTwoScore);
        P1 = findViewById(R.id.PlayerOneText);
        P2 = findViewById(R.id.PlayerTwoText);
        timeText = findViewById(R.id.timeText);
        timeElapsed = findViewById(R.id.twentyOneTime);
        endSession = findViewById(R.id.buttonEndSession);
        PlayAgain = findViewById(R.id.buttonRestart);
        helper = new AATWHelper();
        DataBaseHelper databasehelper = new DataBaseHelper(AllAroundTheWorld.this);
        date[0] = helper.getDate();
        startTimer();

            shotMade.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!helper.finished()) {
                        session = helper.updateStats("1");
                        displayStats();
                        if (session) {
                            timeElapsed.stop();
                            PlayAgain.setVisibility(View.VISIBLE);
                            //shotMade.setVisibility(View.INVISIBLE);
                           // shotMiss.setVisibility(View.INVISIBLE);
                            Toast.makeText(AllAroundTheWorld.this, helper.calculateExperience(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        shotMiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session = helper.updateStats("0");
                displayStats();
            }
        });
        endSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = databasehelper.addOne(helper.getTime(),helper.getShotsMade()[0],helper.getShotsTaken()[0], date[0],"AATW");
                boolean temp = databasehelper.addOne(helper.getTime(),helper.getShotsMade()[1],helper.getShotsTaken()[1], date[0],"AATW");
                Toast.makeText(AllAroundTheWorld.this,helper.calculateExperience(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        PlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayAgain.setVisibility(View.GONE);
                shotMade.setVisibility(View.VISIBLE);
                shotMiss.setVisibility(View.VISIBLE);
                boolean success = databasehelper.addOne(helper.getTime(),helper.getShotsMade()[0],helper.getShotsTaken()[0], date[0],"AATW");
                boolean temp = databasehelper.addOne(helper.getTime(),helper.getShotsMade()[1],helper.getShotsTaken()[1], date[0],"AATW");
                helper = new AATWHelper();
                date[0] = helper.getDate();
                running = false;
                startTimer();
                displayStats();
            }
        });
    }
    public void displayStats(){
        int[] PlayerScore;
        PlayerScore = helper.displayScore();
        P1.setText(" " +PlayerScore[0]);
        P2.setText(" " +PlayerScore[1]);
    }
    private void startTimer(){
        if (!running) {
            timeElapsed.setBase(SystemClock.elapsedRealtime());
            timeElapsed.start();
            running = true;
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Toast.makeText(AllAroundTheWorld.this,"Connected to HoopDreamsDevice!",Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                Log.d(TAG,"Connected");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Toast.makeText(AllAroundTheWorld.this,"Disconnected from HoopDreamsDevice!",Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
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
            Toast.makeText(AllAroundTheWorld.this, receiveBuffer, Toast.LENGTH_SHORT).show();
        }
        for(byte bi:receive){
            Log.d(TAG, "Byte value: "+ bi);
        }
        if (!helper.finished()) {
            session = helper.updateStats(receive[0]);
            displayStats();
            mBluetoothLeService.writeCharacteristic(helper.getFeedback());
            if (session) {
                timeElapsed.stop();
                PlayAgain.setVisibility(View.VISIBLE);
                //shotMade.setVisibility(View.INVISIBLE);
                // shotMiss.setVisibility(View.INVISIBLE);
                Toast.makeText(AllAroundTheWorld.this, helper.calculateExperience(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void displayGattServices(List<BluetoothGattService> gattServices) {
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
            displayGattServices(mBluetoothLeService.getSupportedGattServices());
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
            displayGattServices(mBluetoothLeService.getSupportedGattServices());
        }

    }
}