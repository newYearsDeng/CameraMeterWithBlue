package com.northmeter.camerameterwithblue.I;

/**
 * Created by dyd
 * 2017/5/18
 */
public interface ISendBlueMessage {

    void sendBlueMessage(String para);
    void sendBTblueMessage(String para, int state);
}
