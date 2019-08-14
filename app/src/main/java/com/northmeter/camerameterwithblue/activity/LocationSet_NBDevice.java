package com.northmeter.camerameterwithblue.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import com.google.android.material.tabs.TabLayout;
import com.northmeter.camerameterwithblue.R;
import com.northmeter.camerameterwithblue.base.BaseActivity;
import com.northmeter.camerameterwithblue.bluetooth.AllScanDeviceListActivity;
import com.northmeter.camerameterwithblue.bluetooth.BleBlue_ConnectHelper;
import com.northmeter.camerameterwithblue.bluetooth.BleConnect_InstanceHelper;
import com.northmeter.camerameterwithblue.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.camerameterwithblue.fragment.Fragment_NBMeter_Install;
import com.northmeter.camerameterwithblue.fragment.Fragment_NBMeter_Setting;
import com.northmeter.camerameterwithblue.widget.EmptyFragmentPagerAdapter;
import com.northmeter.camerameterwithblue.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.northmeter.camerameterwithblue.bluetooth.AllScanDeviceListActivity.DATA_DEVICE;
import static com.northmeter.camerameterwithblue.bluetooth.AllScanDeviceListActivity.DATA_TYPE;

/**
 * Created by dyd on 2019/3/11.
 * 本地设置——NB摄像水表
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class LocationSet_NBDevice extends BaseActivity {
    @BindView(R.id.vp_empty)
    NoScrollViewPager vpEmpty;
    @BindView(R.id.tl_empty)
    TabLayout tlEmpty;
    @BindView(R.id.tv_toolbar_title)
    TextView tvToolbarTitle;
    @BindView(R.id.tv_right_text)
    TextView tvRightText;
    private String TAG = getClass().getSimpleName();
    public static final String DEVICE_NAME = "device_name";

    private List<Fragment> fragments = new ArrayList<>();
    private String[] mTitles;
    private EmptyFragmentPagerAdapter adapter;

    private int type;
    private String equipmentNum;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_location_set_nb_device;
    }

    @Override
    public void initIntentData() {
        super.initIntentData();
        equipmentNum = getIntent().getStringExtra("equipmentNum");
        type =  getIntent().getIntExtra("type",0);
    }

    @Override
    public void setTitle() {
        super.setTitle();
        tvToolbarTitle.setText("本地配置");
        tvRightText.setText("蓝牙");
    }

    @Override
    public void initData() {
        super.initData();

        fragments.add(Fragment_NBMeter_Install.newInstance(0));
        fragments.add(Fragment_NBMeter_Setting.newInstance(1));
        mTitles = new String[]{"安装测试","参数设置"};

        adapter = new EmptyFragmentPagerAdapter(getSupportFragmentManager(), fragments, mTitles);
        vpEmpty.setAdapter(adapter);
        tlEmpty.setupWithViewPager(vpEmpty);
        vpEmpty.setOffscreenPageLimit(2);
    }

    private static final int REQUEST_CAMERARESULT=201;
    private static final int REQUEST_LOCATIONARESULT=2010;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkLocationAndOpenCamer();
            }else{
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERARESULT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CAMERARESULT:
                boolean isAllGranted = true;
                for(int result : grantResults){
                    if(result == PackageManager.PERMISSION_DENIED){
                        isAllGranted = false;
                        break;
                    }
                }
                if(!isAllGranted){
                    //权限有缺失
                    showMsg("该功能需要您授权打开相机和定位");
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 10);
                }else{
                    checkLocationAndOpenCamer();
                }
                break;
            case REQUEST_LOCATIONARESULT:
                break;
            default:
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 10);
                break;
        }

    }

    /**检查是否打开了定位服务*/
    private void checkLocationAndOpenCamer(){
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean locationISOK = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!locationISOK){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, REQUEST_LOCATIONARESULT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @OnClick({R.id.btn_tb_back,R.id.tv_right_text})
    public void onViewClicked(View v) {
        switch (v.getId()){
            case R.id.btn_tb_back:
                this.finish();
                break;
            case R.id.tv_right_text:
                Intent intent = new Intent(this, AllScanDeviceListActivity.class);
                startActivityForResult(intent, AllScanDeviceListActivity.REQUEST_DEVICE);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AllScanDeviceListActivity.REQUEST_DEVICE://搜索BLE蓝牙
                if (resultCode == Activity.RESULT_OK) {
                    //断开蓝牙
                    BleConnect_InstanceHelper.getInstance().cancelConnect();

                    int type = data.getExtras().getInt(DATA_TYPE);
                    BluetoothDevice checkedDevice = data.getExtras().getParcelable(DATA_DEVICE);
                    BlueTooth_ConnectHelper.getInstance().removeBondDevice(checkedDevice.getAddress());
                    if(type == 1){
                        BleConnect_InstanceHelper bleConnect = BleConnect_InstanceHelper.getInstance();
                        bleConnect.setMacStr(checkedDevice.getAddress());
                        bleConnect.connecedDevice();
                    }
                }
                break;
        }
    }


}
