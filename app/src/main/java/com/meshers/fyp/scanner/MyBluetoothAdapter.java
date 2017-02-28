package com.meshers.fyp.scanner;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;


/**
 * AUTHOR: sarahchristina on 1/10/17.
 */
public class MyBluetoothAdapter {

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter myBluetoothAdapter;

    private Activity activity;

    private String BTName;

    public MyBluetoothAdapter(Activity activity) {

        this.activity = activity;
        this.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public Context getContext() {
        return activity;
    }

    public boolean isSupported() {
        return myBluetoothAdapter != null;
    }

    public boolean on(String name) {
        this.BTName = name;

        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            return true;
        } else {

            if(name != null){
                myBluetoothAdapter.setName(name);
            }
            else{
                myBluetoothAdapter.setName("");
            }
            makeDiscoverable(3000);
            return false;
        }

    }

    public void setName(String name) {
        BTName = name;
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
        } else {
            if(name != null){
                myBluetoothAdapter.setName(name);
            }
            else{
                myBluetoothAdapter.setName("");
            }
            makeDiscoverable(3000);
        }
    }

    private void makeDiscoverable(int timeOut) {
        Class<?> baClass = BluetoothAdapter.class;
        Method[] methods = baClass.getDeclaredMethods();
        Method mSetScanMode = null;
        for (Method method : methods) {
            if (method.getName().equals("setScanMode") && method.getParameterTypes().length == 2
                    && method.getParameterTypes()[0].equals(int.class)
                    && method.getParameterTypes()[1].equals(int.class)) {
                mSetScanMode = method;
                break;
            }
        }
        try {
            mSetScanMode.invoke(myBluetoothAdapter,
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, timeOut);
        } catch (Exception e) {
            Log.e("discoverable", e.getMessage());
            for (Class parameter : mSetScanMode.getParameterTypes()) {
                System.out.println("PARAM:" + parameter);
            }
        }
    }

    public boolean off() {
        return myBluetoothAdapter.disable();
    }

    public String activityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK) {
            setName(BTName);
        } else if (resultCode == REQUEST_ENABLE_BT) {
            Toast.makeText(activity, "Bluetooth failed to be enabled", Toast.LENGTH_LONG).show();
        }
        return null;
    }


    public void find() {

        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }
        myBluetoothAdapter.startDiscovery();
    }
}
