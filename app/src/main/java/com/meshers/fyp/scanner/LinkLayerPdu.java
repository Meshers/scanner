package com.meshers.fyp.scanner;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by sarahcs on 2/21/2017.
 */

public class LinkLayerPdu {
    private byte fromAddr;
    private byte[] data;

    private final static int ADDR_SIZE_BYTES = 1;

    public LinkLayerPdu(byte fromAddr, byte[] data){
        this.fromAddr = fromAddr;
        this.data = data;
    }

    public LinkLayerPdu(byte[] encoded) {
        if (!isValidPdu(encoded)) {
            throw new IllegalArgumentException("Invalid PDU format!");
        }
        fromAddr = encoded[ADDR_SIZE_BYTES - 1];
        data = new byte[encoded.length - ADDR_SIZE_BYTES ];
        System.arraycopy(encoded, ADDR_SIZE_BYTES, data, 0, data.length);
    }

    public LinkLayerPdu(String encoded) throws UnsupportedEncodingException {
        this(encoded.getBytes("UTF-8"));
    }

    public static boolean isValidPdu(String encoded) {
        try {
            if(encoded != null){
                return isValidPdu(encoded.getBytes("UTF-8"));
            }
            else{
                return false;

            }

        } catch (UnsupportedEncodingException e) {
            Log.e("LLPDU", "isValid failed", e);
            return false;
        }
    }

    public static boolean isValidPdu(byte[] encoded) {

        if (encoded.length < ADDR_SIZE_BYTES) {
            return false;
        }

        return true;
    }

    public byte[] getData() {
        return data;
    }

    public String getDataAsString() {
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("LLPDU", "Failed to decode", e);
            return null;
        }
    }

    public byte getFromAddress() {
        return fromAddr;
    }

    public String getPduAsString(){
        try{
            byte[] pdu = new byte[ADDR_SIZE_BYTES + data.length];

            pdu[ADDR_SIZE_BYTES - 1] = fromAddr;
            System.arraycopy(data, 0, pdu, ADDR_SIZE_BYTES, data.length);
            Log.e("PDU", Arrays.toString(pdu));

            return new String(pdu, "UTF-8");
        }catch (UnsupportedEncodingException e){
            Log.e("LLPDU", "Failed to decode", e);
            return null;
        }
    }
}
