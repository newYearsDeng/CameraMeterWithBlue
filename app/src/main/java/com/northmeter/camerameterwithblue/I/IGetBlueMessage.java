package com.northmeter.camerameterwithblue.I;


import com.northmeter.camerameterwithblue.bluetooth.GetBuleModel;

/**
 * Created by dyd
 * 2017/5/15
 */
public interface IGetBlueMessage {
    void getBlueMessage(String str, GetBuleModel.DownloadMsgCallback downloadMsgCallback);
}
