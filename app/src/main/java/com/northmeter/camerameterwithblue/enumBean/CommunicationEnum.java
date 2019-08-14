package com.northmeter.camerameterwithblue.enumBean;

/**
 * Created by dyd on 2019/4/24.
 * 添加设备时候选通讯端口
 */

/**
 *  "RS485-1":"02",
 "RS485-2":"03",
 "RS485-3":"04",
 "RS485-4":"05",
 "本地通讯终端端口":"31",
 "终端交流采样接口":"01"
 */

public enum  CommunicationEnum {

    CommunicationRS485_1("RS485-1","02"),
    CommunicationRS485_2("RS485-2","03"),
    CommunicationRS485_3("RS485-3","04"),
    CommunicationRS485_4("RS485-4","05"),
    CommunicationLocal("本地通讯终端端口","31"),
    CommunicationTerminal("终端交流采样接口","01");

    private String portName;
    private String portValue;

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getPortValue() {
        return portValue;
    }

    public void setPortValue(String portValue) {
        this.portValue = portValue;
    }

    CommunicationEnum(String portName, String portValue){
        this.portName = portName;
        this.portValue = portValue;
    }
}


