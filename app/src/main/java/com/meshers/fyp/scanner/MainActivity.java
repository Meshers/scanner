package com.meshers.fyp.scanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiManager mWifiManager;

    private final static String TAG = "MainActivity";
    private final static String WIFI_FILE_NAME = "WIFI_";

    private long mLastScanStarted;
    private File mWifiResultsFile;

    private TextView mWifiTv;

    private HashSet<String> mWifiDiscoveredSet;
    private HashSet<String> mWifiDiscoveredSet2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiTv = (TextView) findViewById(R.id.wifi_tv);
        mWifiDiscoveredSet = new HashSet<>();
        mWifiDiscoveredSet2 = new HashSet<>();
        mWifiResultsFile = new File(Environment.getExternalStorageDirectory() + "/" + WIFI_FILE_NAME
                + System.currentTimeMillis());
        try {
            mWifiResultsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        registerWifiReceiver();

        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    }

    private void registerWifiReceiver() {

        IntentFilter intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

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
                    writeScanResults(scanResults, mLastScanStarted, System.currentTimeMillis());
                    startScan();
                }
            }
        };

        registerReceiver(mWifiScanReceiver, intentFilter);

    }

    private void writeScanResults(List<ScanResult> results, long startTime, long endTime) {

        try {
            PrintWriter pw = new PrintWriter(mWifiResultsFile);
            String line = "";
            for (ScanResult result : results) {
                line += startTime
                        + "," + endTime
                        + "," + result.SSID
                        + "," + result.BSSID
                        + "," + result.frequency
                        + "," + result.level;

            }
            pw.println(line);
            pw.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed writing WIFI results", e);
        }
    }

    public void startClicked(View v) {
        mWifiDiscoveredSet = new HashSet<>();
        mWifiDiscoveredSet2 = new HashSet<>();
        mWifiResultsFile = new File(Environment.getExternalStorageDirectory() + "/" + WIFI_FILE_NAME
                + System.currentTimeMillis());
        try {
            mWifiResultsFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PrintWriter pw = new PrintWriter(mWifiResultsFile);
            pw.println("StartTime,EndTime,SSID,BSSID,Frequency,Level");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        startScan();
    }

    public void startScan() {
        mLastScanStarted = System.currentTimeMillis();
        mWifiManager.startScan();

    }
}
