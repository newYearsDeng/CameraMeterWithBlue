package com.northmeter.camerameterwithblue.bean;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by dyd on 2019/4/4.
 */

public class BlueToothDeviceBean implements Serializable{
    private BluetoothDevice blueDevice;
    private int type;//0表示BT经典蓝牙，1表示ble蓝牙
    private boolean isCheck;

    public BlueToothDeviceBean(BluetoothDevice blueDevice, int type, boolean isCheck){
        this.blueDevice = blueDevice;
        this.type=type;
        this.type = type;
        this.isCheck = isCheck;
    }

    public BluetoothDevice getBlueDevice() {
        return blueDevice;
    }

    public void setBlueDevice(BluetoothDevice blueDevice) {
        this.blueDevice = blueDevice;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }
}
