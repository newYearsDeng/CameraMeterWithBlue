package com.northmeter.camerameterwithblue.enumBean;

/**
 * Created by dyd on 2019/3/27.
 */

public enum EvenBusEnum {
    EvenBus_WaterMeterPicShow("EvenBus_WaterMeterPicShow"),
    EvenBus_NBMeter_Install("EvenBus_NBMeter_Install"),
    EvenBus_NBMeter_Setting("EvenBus_NBMeter_Setting"),
    EvenBus_BlueTooth_Connect("EvenBus_BlueTooth_Connect"),
    EvenBus_BuildDevice("EvenBus_BuildDevice");

    private String evenName;

    EvenBusEnum(String evenName){
        this.evenName = evenName;
    }

    public String getEvenName() {
        return evenName;
    }

    public void setEvenName(String evenName) {
        this.evenName = evenName;
    }
}
