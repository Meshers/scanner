package com.meshers.fyp.scanner;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiManager mWifiManager;

    private final static String TAG = "MainActivity";

    private long mLastWifiScanStarted;
    private WifiLogger mWifiLogger;
    private BtLogger mBtLogger;
    private BtHelper mBtHelper;

    private TextView mWifiTv;
    private TextView mBtTv;

    private HashSet<String> mWifiDiscoveredSet;
    private HashSet<String> mWifiDiscoveredSet2;

    private HashSet<String> mBtDiscoveredSet;

    private boolean mWifiReceiverRegistered = false;
    private boolean mBtReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiTv = (TextView) findViewById(R.id.wifi_tv);
        mBtTv = (TextView) findViewById(R.id.bt_tv);

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        MyBluetoothAdapter adapter = new MyBluetoothAdapter(this);

        mBtHelper = new BtHelper(adapter, new DeviceDiscoveryHandler() {
            long mLastScanStarted;

            @Override
            public void handleDiscovery(BluetoothDevice receivedPacket, short rssi) {
                mBtDiscoveredSet.add(receivedPacket.getAddress());
                mBtTv.setText("" + mBtDiscoveredSet.size());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mBtLogger.writeScanResults(receivedPacket, rssi, mLastScanStarted,
                            System.currentTimeMillis());
                }
            }

            @Override
            public void handleStarted() {
                mLastScanStarted = System.currentTimeMillis();
                mBtLogger.writeScanTiming(BtLogger.ScanType.STARTED, mLastScanStarted);
            }

            @Override
            public void handleFinished() {
                mBtLogger.writeScanTiming(BtLogger.ScanType.FINISHED, System.currentTimeMillis());
                mBtLogger.writeScanTiming(BtLogger.ScanType.REQUESTED, System.currentTimeMillis());
                mBtHelper.startDiscovery();
            }
        });
    }

    BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> scanResults = mWifiManager.getScanResults();
                for (ScanResult result : scanResults) {
                    mWifiDiscoveredSet.add(result.BSSID);
                    mWifiDiscoveredSet2.add(result.SSID);
                }
                // add your logic here
                Log.d(TAG, "Scan results are:" + scanResults);
                mWifiTv.setText(mWifiDiscoveredSet.size() + ":" + mWifiDiscoveredSet2.size());
                mWifiLogger.writeScanResults(scanResults, mLastWifiScanStarted, System.currentTimeMillis());
                startWifiScan();
            }
        }
    };

    private void registerWifiReceiver() {

        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        registerReceiver(mWifiScanReceiver, intentFilter);
    }

    public void startClicked(View v) {
        mWifiDiscoveredSet = new HashSet<>();
        mWifiDiscoveredSet2 = new HashSet<>();
        mWifiLogger = new WifiLogger();
        startWifiScan();
    }

    public void stopClicked(View v) {
        if (!mWifiReceiverRegistered) {
            return;
        }
        mWifiReceiverRegistered = false;
        unregisterReceiver(mWifiScanReceiver);
    }


    public void startBtClicked(View v) {
        mBtDiscoveredSet = new HashSet<>();
        mBtLogger = new BtLogger();
        if (!mBtReceiverRegistered) {
            mBtHelper.startListening();
            mBtReceiverRegistered = true;
        }
        startBtScan();
    }


    public void stopBtClicked(View v) {
        if (!mBtReceiverRegistered) {
            return;
        }
        mBtHelper.stopListening();
        mBtReceiverRegistered = false;
    }

    public void startWifiScan() {
        mLastWifiScanStarted = System.currentTimeMillis();
        mWifiManager.startScan();

        if (!mWifiReceiverRegistered) {
            registerWifiReceiver();
            mWifiReceiverRegistered = true;
        }
    }

    public void startBtScan() {
        mBtHelper.startDiscovery();
    }
}
