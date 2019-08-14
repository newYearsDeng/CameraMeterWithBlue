package com.northmeter.camerameterwithblue.bluetooth;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.northmeter.camerameterwithblue.I.IShowSMainMessage;
import com.northmeter.camerameterwithblue.base.MyApplication;
import com.northmeter.camerameterwithblue.bean.EvenBusBean;
import com.northmeter.camerameterwithblue.bluetooth.bluetooth.tools.BluetoothConnectionClient;
import com.northmeter.camerameterwithblue.bluetooth.bluetooth.tools.GattCode;
import com.northmeter.camerameterwithblue.enumBean.EvenBusEnum;
import com.northmeter.camerameterwithblue.utils.Udp_Help;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by dyd on 2019/4/2.
 */

public class BleBlue_ConnectHelper {
    private final int FOUND_DEVICE = 0X01;
    private final int DISCONNECTED = 0X02;
    private final int FOUND_SERVICE = 0X03;
    private final int WRITE_SUCCESS = 0X04;
    private final int WRITE_FAILED = 0X05;
    private final int RECONNECT = 0x06;
    private final int RECEIVE = 0x07;
    private final int SEND = 0x08;
    private final int CONNECTED = 0X09;
    private final int BLUEM_ESSAGE = 0x0a;

    private String TAG = getClass().getSimpleName();
    private String receive_msg = "";

    /**BLE蓝牙对象*/
    private BluetoothConnectionClient mConnectionClient;

    private static BleBlue_ConnectHelper uniqueInstance=null;

    private static String mConnectedDeviceName;

    /**蓝牙是否连接成功*/
    private static boolean booleanConnected = false;

    public BleBlue_ConnectHelper() {
        mConnectionClient = null;
        booleanConnected = false;
        mConnectedDeviceName = "No Connected";
    }

    public static BleBlue_ConnectHelper getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new BleBlue_ConnectHelper();
        }
        return uniqueInstance;
    }


    public void blueToothConnect(BluetoothDevice blueDevice){
        if (mConnectionClient != null) {
            mConnectionClient.disconnect();
        }
        mConnectionClient = new BluetoothConnectionClient(blueDevice, MyApplication.getContext(), mGattCallback);
        mConnectionClient.connect();
    }

    public void send(String data) {
        mConnectionClient.write(4, Udp_Help.strtoByteArray(data));
    }

    public void cancelConnect() {
        if (mConnectionClient != null) {
            mConnectionClient.disconnect();
            mConnectionClient = null;
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONNECTED://连接成功
                    Log.w(TAG, "连接成功");

                    setBooleanConnected(true);
                    BlueTooth_ConnectHelper.getInstance().setBooleanConnected(true);
                    sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(), "已连接"+mConnectedDeviceName);

                    int state = BlueTooth_UniqueInstance.getInstance().getState();
                    if(state == 20||state == 24||state==26){
                        sendEventBus(EvenBusEnum.EvenBus_BuildDevice.getEvenName(), "success");
                    }
                    break;
                case DISCONNECTED://断开连接
                    Log.w(TAG,"连接断开！");
                    setBooleanConnected(false);
                    BlueTooth_ConnectHelper.getInstance().setBooleanConnected(false);
                    sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(), "连接断开");
                    break;
                case RECONNECT:
                    if (mConnectionClient != null) {
                        mConnectionClient.connect();
                        sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(),"正在重连");
                    }
                    break;
                case RECEIVE:
                    String data = (String) msg.obj;
                    break;
                case SEND:
                    break;
                case FOUND_DEVICE:
                    if (mConnectionClient != null) {
                        mConnectionClient.disconnect();
                    }
                    sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(), "正在连接");
                    mConnectionClient = (BluetoothConnectionClient) msg.obj;
                    mConnectionClient.connect();
                    break;
                case BLUEM_ESSAGE:
                    String blueMsg = (String) msg.obj;
                    if (blueMsg.equals("success")) {
                        Log.w(TAG,  "设置成功");
                        return;
                    } else if (blueMsg.equals("fail")) {
                        Log.w(TAG, "操作失败");
                        return;
                    }
                    break;

            }
        }
    };

    @SuppressLint("NewApi")
    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (newState == BluetoothGatt.STATE_CONNECTED) {
                    try {
                        Thread.sleep(500);
                        gatt.discoverServices();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (newState == BluetoothGatt.STATE_DISCONNECTED || status == 133) {
                Log.w(TAG, "onConnectionStateChange: disconnected");
                mHandler.sendEmptyMessage(DISCONNECTED);
                mHandler.sendEmptyMessage(RECONNECT);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            BluetoothGattService service = gatt.getService(GattCode.FFF_SERVICE);
            System.out.println("Uuid: " + service.getUuid());

            mConnectionClient.addCharacteristic(3, service.getCharacteristic(GattCode.FFF_3));
            mConnectionClient.addCharacteristic(4, service.getCharacteristic(GattCode.FFF_4));

            Log.w(TAG, "onServicesDiscovered: ");
            mConnectionClient.setCharacteristicNotification(GattCode.DESCRIPTOR,
                    3, true);

            mHandler.sendEmptyMessage(FOUND_SERVICE);

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.w(TAG, "onCharacteristicWrite: status=" + status);
            if (status == 0) {
                Message msg = mHandler.obtainMessage(SEND);
                msg.obj = characteristic.getValue();
                mHandler.sendMessage(msg);
            } else {
                mHandler.sendEmptyMessage(WRITE_FAILED);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte[] value = characteristic.getValue();
            receive_msg = receive_msg + Udp_Help.bytesToHexString(value);
            Log.w(TAG, receive_msg);
            System.out.println("resultData:"+receive_msg.toUpperCase());
            handleBtBlueMessage(receive_msg.toUpperCase());
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.w(TAG, "onDescriptorWrite: status=" + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                setMTU(gatt,512);//修改mtu最大传输字节
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.w(TAG,"onMtuChanged="+mtu+",status="+status);
            if(status == BluetoothGatt.GATT_SUCCESS){
                mConnectedDeviceName = gatt.getDevice().getName();
                mHandler.sendEmptyMessage(CONNECTED);
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean setMTU(BluetoothGatt gatt,int mtu){
        Log.w(TAG,"setMTU "+mtu);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            if(mtu>20){
                boolean ret = gatt.requestMtu(mtu);
                Log.w(TAG,"requestMTU "+mtu+" ret="+ret);
                return ret;
            }
        }
        return false;
    }

    /**处理蓝牙接收到的数据*/ //FEFEFE6821000016200168 91 C300 343310EF 343D3E(图片编号，该包序号，总包数)
    private String handleBtBlueMessage(String data){//0080FEFEFE6821000016200168940000BC16C000FCF8
        int state = data.lastIndexOf("FEFEFE68");
        int state_lenth = state+28;//从FEFEFE68到标识码的长度
        String ditle = data.substring(data.length() - 2, data.length());//检查最后的字节是否为16

        if(state>=0 && data.length()>state_lenth && ditle.equals("16")){
            //String stateNum = data.substring(state+28,state+36);//标识码
            String length_1 = data.substring(state+24,state+26);//长度 c3
            String length_2 = data.substring(state+26,state+28);//长度 00
            Log.w(TAG,length_1+"/"+length_2);
            int len = Integer.valueOf(length_2+length_1,16);//字符串内长度字节

            if(data.substring(state+28,data.length()-4).length()/2 == len){

                new GetBlueEntity(new IShowSMainMessage() {
                    @Override
                    public void showMainMsg(String message) {
                        switch(BlueTooth_UniqueInstance.getInstance().getState()){
                            case 0:
                                sendEventBus(EvenBusEnum.EvenBus_NBMeter_Install.getEvenName(), message);
                                break;
                            case 1:
                            case 2:
                                sendEventBus(EvenBusEnum.EvenBus_NBMeter_Setting.getEvenName(), message);
                                break;
                            case 10:
                                sendEventBus(EvenBusEnum.EvenBus_WaterMeterPicShow.getEvenName(), message);
                                break;
                            case 21:
                            case 22:
                            case 23:
                            case 25:
                                sendEventBus(EvenBusEnum.EvenBus_BuildDevice.getEvenName(), message);
                                break;
                        }
                    }

                    @Override
                    public void showSettingMsg(String message) {
                        sendEventBus(EvenBusEnum.EvenBus_NBMeter_Setting.getEvenName(), message);
                    }
                }).transmitBlueMsg(data.substring(state, data.length()));
                receive_msg = "";
            }


        }

        return null;
    }


    public void sendEventBus(String topic, String data) {
        EvenBusBean bean = new EvenBusBean();
        bean.setTopic(topic);
        bean.setData(data);
        EventBus.getDefault().post(bean);
    }


    public BluetoothConnectionClient getConnectionClient() {
        return mConnectionClient;
    }

    public void setConnectionClient(BluetoothConnectionClient mConnectionClient) {
        this.mConnectionClient = mConnectionClient;
    }

    public static boolean isBooleanConnected() {
        return booleanConnected;
    }

    public static void setBooleanConnected(boolean booleanConnected) {
        BleBlue_ConnectHelper.booleanConnected = booleanConnected;
    }
}
