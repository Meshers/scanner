package com.meshers.fyp.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


public class BtHelper {

    private MyBluetoothAdapter mBluetoothAdapter;
    private DeviceDiscoveryHandler mDiscoveryHandler;
    private BroadcastReceiver mBtReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            System.out.println("ACTION:" + intent.getAction());
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mDiscoveryHandler.handleStarted();
            }

            // if our discovery has finished, time to start again!
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mDiscoveryHandler.handleFinished();
            }

            // When discovery finds a device
            if (!BluetoothDevice.ACTION_FOUND.equals(action)) return;
            // Get the BluetoothDevice object from the Intent
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device!= null) {
                mDiscoveryHandler.handleDiscovery(device);
            }
        }
    };

    public BtHelper(MyBluetoothAdapter bluetoothAdapter, DeviceDiscoveryHandler discoveryHandler) {
        mBluetoothAdapter = bluetoothAdapter;
        mDiscoveryHandler = discoveryHandler;
    }

    public void startDiscovery() {
        mBluetoothAdapter.find();
    }

    public void startListening() {
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // check started just for debugging purposes
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mBluetoothAdapter.getContext().registerReceiver(mBtReceiver, filter);
    }

    public void stopListening() {
        mBluetoothAdapter.getContext().unregisterReceiver(mBtReceiver);
    }
}
