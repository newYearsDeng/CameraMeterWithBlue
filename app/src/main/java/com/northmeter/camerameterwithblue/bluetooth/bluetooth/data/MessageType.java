package com.northmeter.camerameterwithblue.bluetooth.bluetooth.data;

/**
 * Created by benjamin on 16/5/25.
 */
public enum MessageType {
    RX(1), TX(2);
    private int mCode;

    private MessageType(int code) {
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
