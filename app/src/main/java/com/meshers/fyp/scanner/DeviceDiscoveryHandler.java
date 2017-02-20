package com.meshers.fyp.scanner;


import android.bluetooth.BluetoothDevice;

import java.io.UnsupportedEncodingException;

public interface DeviceDiscoveryHandler {
    void handleDiscovery(BluetoothDevice receivedPacket) throws UnsupportedEncodingException;
    void handleStarted();
    void handleFinished();
}
