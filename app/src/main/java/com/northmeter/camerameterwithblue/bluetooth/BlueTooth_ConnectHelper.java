package com.northmeter.camerameterwithblue.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;

import com.northmeter.camerameterwithblue.I.IShowSMainMessage;
import com.northmeter.camerameterwithblue.base.MyApplication;
import com.northmeter.camerameterwithblue.bean.EvenBusBean;
import com.northmeter.camerameterwithblue.bluetooth.bt_bluetooth.BluetoothChatService;
import com.northmeter.camerameterwithblue.enumBean.EvenBusEnum;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.Set;

import static com.northmeter.camerameterwithblue.bluetooth.AllScanDeviceListActivity.DEVICE_NAME;


/**
 * Created by dyd on 2019/3/11.
 * 高速蓝牙
 */

public class BlueTooth_ConnectHelper implements IShowSMainMessage {
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Local Bluetooth adapter
    private static BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private static BluetoothChatService mChatService = null;

    private static BlueTooth_ConnectHelper uniqueInstance=null;

    private static String mConnectedDeviceName;

    /**bt蓝牙是否连接成功*/
    private static boolean booleanConnected = false;

    public BlueTooth_ConnectHelper() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mChatService = new BluetoothChatService(MyApplication.getContext(), btHandler);
        booleanConnected = false;
        mConnectedDeviceName = "No Connected";
    }

    public static BlueTooth_ConnectHelper getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new BlueTooth_ConnectHelper();
        }
        return uniqueInstance;
    }


    public void blueToothConnect(String address){
        BluetoothDevice device = mBluetoothAdapter
                .getRemoteDevice(address);
        // Attempt to connect to the device
         mChatService.connect(device);
    }

    public void stopBlueToothConnect(){
        mChatService.stop();
    }

    private Handler btHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (true)
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            System.out.println("连接成功");
                            setBooleanConnected(true);
                            setmChatService(mChatService);

                            sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(), "已连接"+mConnectedDeviceName);

                            int state = BlueTooth_UniqueInstance.getInstance().getState();
                            if(state == 20||state == 24||state==26){
                                sendEventBus(EvenBusEnum.EvenBus_BuildDevice.getEvenName(), "success");
                            }
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setmConnectedDeviceName("连接中");
                            System.out.println("连接中");
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                            break;
                        case BluetoothChatService.STATE_NONE:
                            setBooleanConnected(false);
                            setmConnectedDeviceName("连接失败");
                            sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(), "连接失败");
                            System.out.println("连接失败");
                            break;
                        case BluetoothChatService.STATE_STOP:
                            setBooleanConnected(false);
                            setmConnectedDeviceName("连接断开");
                            sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(), "连接断开");
                            System.out.println("断开连接");
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    break;
                case MESSAGE_READ:
                    String readBuf = (String) msg.obj;
                    System.out.println("接收到的数据转换后："+readBuf);
                    //getBlueEntity.transmitBlueMsg(readBuf);
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
                    }).transmitBlueMsg(readBuf);
                    break;
                case MESSAGE_DEVICE_NAME:
                    //save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    System.out.println(mConnectedDeviceName);
                    break;
                case MESSAGE_TOAST://连接丢失
                    setBooleanConnected(false);
                    setmConnectedDeviceName("连接断开");
                    System.out.println("连接断开");
                    sendEventBus(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName(), "连接断开");
                    //dataChange.setDataChange("连接断开");
                    //showMsg(msg.getData().getString(TOAST));
                    break;
            }
        }
    };

    public void sendEventBus(String topic, String data) {
        EvenBusBean bean = new EvenBusBean();
        bean.setTopic(topic);
        bean.setData(data);
        EventBus.getDefault().post(bean);
    }

    @Override
    public void showMainMsg(String message) {

    }

    @Override
    public void showSettingMsg(String message) {

    }

    public BluetoothAdapter getmBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public void setmBluetoothAdapter(BluetoothAdapter mBluetoothAdapter) {
        this.mBluetoothAdapter = mBluetoothAdapter;
    }

    public BluetoothChatService getmChatService() {
        return mChatService;
    }

    public void setmChatService(BluetoothChatService mChatService) {
        this.mChatService = mChatService;
    }

    public static boolean isBooleanConnected() {
        return booleanConnected;
    }

    public static void setBooleanConnected(boolean booleanConnected) {
        BlueTooth_ConnectHelper.booleanConnected = booleanConnected;
    }

    public static String getmConnectedDeviceName() {
        return mConnectedDeviceName;
    }

    public static void setmConnectedDeviceName(String mConnectedDeviceName) {
        BlueTooth_ConnectHelper.mConnectedDeviceName = mConnectedDeviceName;
    }

    public void removeBondDevice(String removeID){
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if(device.getAddress().equals(removeID)){
                    try {
                        removeBond(device.getClass(),device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    public static boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }
}
