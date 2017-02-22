package com.meshers.fyp.scanner;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class BtLogger {
    private final File mBtResultsFile;
    private final File mScanRateFile;
    private final static String BT_FILE_NAME = "BT_";
    private final static String BT_SCAN_FILE_NAME = "BT_SCAN_";

    enum ScanType {
        REQUESTED,
        STARTED,
        FINISHED
    }

    public BtLogger() {
        mBtResultsFile = new File(Environment.getExternalStorageDirectory() + "/" + BT_FILE_NAME
                + System.currentTimeMillis() + ".csv");
        mScanRateFile = new File(Environment.getExternalStorageDirectory() + "/" + BT_SCAN_FILE_NAME
                + System.currentTimeMillis() + ".csv");
        try {
            mBtResultsFile.createNewFile();
            mScanRateFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PrintWriter pw = new PrintWriter(mBtResultsFile);
            pw.println("StartTime,DiscoveryTime,ID,Data,MACAddress,Type,BondState,BtClass,Length");
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void writeScanTiming(ScanType type, long time) {
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(mScanRateFile, true));
            pw.println(type.toString() + "," + time);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void writeScanResults(BluetoothDevice device, long startTime, long discoveryTime) throws UnsupportedEncodingException {

        LinkLayerPdu pdu = new LinkLayerPdu(device.getName());
        int length = 0;

        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(mBtResultsFile, true));

            if(device.getName() != null){
                length = device.getName().getBytes("UTF-8").length;
            }

            String line = startTime
                    + "," + discoveryTime
                    + "," + String.valueOf((int)pdu.getFromAddress())
                    + "," + pdu.getDataAsString()
                    + "," + device.getAddress()
                    + "," + device.getType()
                    + "," + device.getBondState()
                    + "," + device.getBluetoothClass().getDeviceClass()
                    + "," + length;
            pw.println(line);
            pw.close();
        } catch (FileNotFoundException e) {
            Log.e("BT Logger", "Failed writing BT results", e);
        }
        catch (UnsupportedEncodingException e) {
            Log.e("BT Logger", "Failed fue to conversion of name to byte array", e);
        }
    }
}
