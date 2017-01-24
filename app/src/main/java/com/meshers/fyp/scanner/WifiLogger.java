package com.meshers.fyp.scanner;

import android.net.wifi.ScanResult;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class WifiLogger {
    private File mWifiResultsFile;
    private final static String WIFI_FILE_NAME = "WIFI_";

    public WifiLogger() {
        mWifiResultsFile = new File(Environment.getExternalStorageDirectory() + "/" + WIFI_FILE_NAME
                + System.currentTimeMillis() + ".csv");
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

    }

    public void writeScanResults(List<ScanResult> results, long startTime, long endTime) {

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(mWifiResultsFile, true));
            String line = "";
            for (ScanResult result : results) {
                line += startTime
                        + "," + endTime
                        + "," + result.SSID
                        + "," + result.BSSID
                        + "," + result.frequency
                        + "," + result.level;
                pw.println(line);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            Log.e("WifiLogger", "Failed writing WIFI results", e);
        }
    }
}
