package com.northmeter.camerameterwithblue.bluetooth;


import com.northmeter.camerameterwithblue.I.IGetBlueMessage;

/**
 * Created by dyd
 * 2017/5/15
 */
public class GetBuleModel implements IGetBlueMessage {

    protected GetBuleModel(){}

    @Override
    public void getBlueMessage(String str,DownloadMsgCallback downloadMsgCallback) {
        if(str.indexOf("FEFEFE68")>=0){
            int stand_0 = str.indexOf("FEFEFE68");
            String blueMsg = str.substring(stand_0,str.length()).toUpperCase();
            String control = blueMsg.substring(22,24).toUpperCase();
            switch(control){
                case "91":
                    String msgflag = blueMsg.substring(28,36).toUpperCase();
                    System.out.println(msgflag);
                    switch(msgflag){
                        case "333310EF"://图片大小查询
                            downloadMsgCallback.onMainResult(blueMsg);
                            break;
                        case "343310EF"://拍照
                            downloadMsgCallback.onMainResult(blueMsg);
                            break;
                        case "383310EF"://拍照窗口参数查询
                            downloadMsgCallback.onMainResult(blueMsg);
                            break;
                        case "413310EF"://曝光时间
                        case "423310EF"://对比度
                        case "353310EF"://补光灯
                        case "403310EF"://参数初始化
                        case "3C3310EF"://清楚图片缓存
                        case "35373337"://程序版本
                        case "433310EF"://曝光设置
                        case "463310EF"://socket信息
                        case "473310EF"://nb通道rssi
                        case "483310EF"://nb模块版本
                        case "493310EF"://NB当前日志
                        case "36343337"://上报时间
                        case "34343337"://系统时间
                        case "393310EF"://压缩率
                        case "4C3310EF"://组合设置命令
                        case "38343337"://组合设置命令
                            downloadMsgCallback.onSettingResult(blueMsg);
                            break;
                    }
                    break;
                case "93"://查询通讯地址应答
                    downloadMsgCallback.onSettingResult(blueMsg);
                    break;
                case "95"://修改通讯地址应答
                case "94"://成功应答
                    downloadMsgCallback.onSuccessResult("success");
                    break;
                default:
                    downloadMsgCallback.onSuccessResult("fail");
                    break;
            }


        }
    }

    public interface DownloadMsgCallback {
        void onMainResult(String mainString);

        void onSettingResult(String setString);

        void onSuccessResult(String successResult);
    }
}
