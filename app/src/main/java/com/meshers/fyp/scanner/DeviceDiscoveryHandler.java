package com.meshers.fyp.scanner;


import android.bluetooth.BluetoothDevice;

public interface DeviceDiscoveryHandler {
    void handleDiscovery(BluetoothDevice receivedPacket, short rssi);
    void handleStarted();
    void handleFinished();
}
