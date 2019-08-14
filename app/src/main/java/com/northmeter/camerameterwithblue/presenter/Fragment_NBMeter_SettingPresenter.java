package com.northmeter.camerameterwithblue.presenter;

import android.content.Context;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.northmeter.camerameterwithblue.I.I_Fragment_NBMeter_SettingPresenter;
import com.northmeter.camerameterwithblue.I.I_ShowFilesInBTSetting;
import com.northmeter.camerameterwithblue.base.Constants;
import com.northmeter.camerameterwithblue.bean.LocalConfigurationPlanBean;
import com.northmeter.camerameterwithblue.utils.SaveUserInfo;
import com.northmeter.camerameterwithblue.utils.Udp_Help;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dyd on 2019/3/15.
 */

public class Fragment_NBMeter_SettingPresenter implements I_Fragment_NBMeter_SettingPresenter {

    private Context context;
    private I_ShowFilesInBTSetting showFilesInBTSetting;
    public Fragment_NBMeter_SettingPresenter(Context context, I_ShowFilesInBTSetting showFilesInBTSetting){
        this.context = context;
        this.showFilesInBTSetting = showFilesInBTSetting;
    }

    @Override
    public void downloadFiles(String url,String name) {
        OkGo.<File>get(url)
                .tag(this)
                //.params("token", SaveUserInfo.getLoginUser(context).getToken())
                .execute(new FileCallback(Constants.filePath,name) {

                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                       //pd.dismiss();
                    }

                    @Override
                    public void onError(Response<File> response) {
                       //pd.dismiss();
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        System.out.println(progress);
                        //handler.sendEmptyMessage((int) (progress.fraction*100));
                    }
                });
    }



    /**获取文件列表*/
    public void getFilesAllName(String path) {
        File file=new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files == null){
            showFilesInBTSetting.returnMessage("文件不存在，请返回项目列表重新加载");
        }else{
            List<LocalConfigurationPlanBean.PlanBean> fileList = new ArrayList<>();
            for(int i = 0;i<files.length;i++){
                System.out.println("path======"+files[i].getAbsolutePath());
                System.out.println("name======"+files[i].getName());
                LocalConfigurationPlanBean.PlanBean planBean = new LocalConfigurationPlanBean.PlanBean();
                planBean.setAddress(files[i].getAbsolutePath());
                planBean.setName(files[i].getName());
                fileList.add(planBean);
            }
            showFilesInBTSetting.showData(fileList);
        }
    }


    /**
     * 曝光时间=30
     曝光设置=自动曝光
     取景坐标x轴原点=0
     取景坐标y轴原点=0
     取景坐标x轴长度=320
     取景坐标y轴长度=240
     对比度=60
     压缩率=60
     补光灯状态=开启自动补光
     上报类型=参考起始时间上报
     上报起始日期=2019-03-21
     上报起始时间=12:00:00
     上报间隔分钟=5
     随机退避时间=5
     拍照时间=12:00*/
    public List<String> getFilesInfo(int projectId,String fileName){
        List<String> paraList = new ArrayList<String>();
        String result = loadFromSDFile(Constants.filePath+projectId,fileName);
        if(result==null||result==""||result.equals("")){
            showFilesInBTSetting.returnMessage("文件不存在，请返回项目列表重新加载");
        }else{
            try{
                String[] infoList = result.split("\n");
                if(infoList.length>=15){
                    for(String item:infoList){
                        paraList.add(item.split("=")[1]);
                    }
//                    String etExposureTime = infoList[0].split("=")[1];
//                    String tvExposureSetting = infoList[1].split("=")[1];
//                    String etCoordinateX = infoList[2].split("=")[1];
//                    String etCoordinateY = infoList[3].split("=")[1];
//                    String etCoordinateXlong = infoList[4].split("=")[1];
//                    String etCoordinateYlong = infoList[5].split("=")[1];
//                    String etContrastRatio = infoList[6].split("=")[1];
//                    String etCompressionRatio = infoList[7].split("=")[1];
//                    String tvFlashState = infoList[8].split("=")[1];
//                    String reportingDatas = infoList[9].split("=")[1];
//                    String tvReportingDate = infoList[10].split("=")[1];
//                    String tvReportingTime = infoList[11].split("=")[1];
//                    String etSpaceTime = infoList[12].split("=")[1];
//                    String etRandomTime = infoList[13].split("=")[1];
//                    String tvPhotoTime = infoList[14].split("=")[1];

                    return paraList;
                }
            }catch(Exception e){
                e.printStackTrace();
                showFilesInBTSetting.returnMessage("配置文件中设置参数有误，解析无效");
                return null;
            }

        }
        return null;
    }

    /**读取文件
     * Environment.getExternalStorageDirectory().getAbsolutePath()+"/northmeter/"+"progectID"*/
    public String loadFromSDFile(String path,String fileName) {
        String result = "";
        try {
            File file=new File(path+"/"+fileName);
            if(!file.exists()){
                result = null;
            }else{
                FileInputStream fin = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fin,"GBK");
                BufferedReader br = new BufferedReader(reader);
                String line;
                while ((line = br.readLine()) != null) {
                    result = result+line+"\n";
                    System.out.println("======"+line);
                }
                br.close();
                reader.close();
                fin.close();
            }
        }catch (Exception e){
            e.printStackTrace();
            showFilesInBTSetting.returnMessage("没有找到指定文件");
        }
        return result;
    }


    private String get_setting(String equipmentId,String etExposureTime,String tvExposureSetting , String tvFlashState ,String etCoordinateX,String etCoordinateY,
                               String etCoordinateXlong,String etCoordinateYlong,String etContrastRatio,String etCompressionRatio) {//4c3310ef 现场参数设置
        String exposureSetting  = "DD";
        if (tvExposureSetting.equals("自动曝光")) {
            exposureSetting = "DD";//自动曝光
        } else {
            exposureSetting = "EE";//手动曝光
        }

        String para_flash = "DDDD";
        switch (tvFlashState){
            case "开启自动补光":
                para_flash = "DDDD";//开启
                break;
            case "关闭自动补光":
                para_flash = "DD88";//关闭
                break;
            case "开启手动补光":
                para_flash = "EEDD";//开启
                break;
            case "关闭手动补光":
                para_flash = "EE88";//关闭
                break;
            default:
                para_flash = "DDDD";//开启
                break;
        }

        String para_x = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645(etCoordinateX));
        String para_y = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645(etCoordinateY));
        String para_xl = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645(etCoordinateXlong));
        String para_yl = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645(etCoordinateYlong));

        String para_str = para_x + para_y + para_xl + para_yl;


        String para_set = "68" + Udp_Help.reverseRst(equipmentId) +
                "68141B004c3310ef" + Constants.HandlerKey +
                Udp_Help.get_Stting_HexTo645(etExposureTime) +
                exposureSetting +
                para_str +
                Udp_Help.get_Stting_HexTo645(etContrastRatio) +
                "DD" + Udp_Help.get_Stting_HexTo645(etCompressionRatio) +
                para_flash;

        return getSendString(para_set);
    }

    private String get_TimeSet(String equipmentId , String reportingDatas,String tvReportingDate,String tvReportingTime,
                               String etSpaceTime,String etRandomTime,String tvPhotoTime) {//38343337
        String str_Enable = "34";//使能位,参考起始时间上报或者忽略起始时间上报，禁能
        switch(reportingDatas){
            case "参考起始时间上报":
                str_Enable = "34";
                break;
            case "忽略起始时间上报":
                str_Enable = "35";
                break;
            case "禁能":
                str_Enable = "EE";
                break;
            default:
                str_Enable = "34";
                break;
        }

        String para_Time = "68" + Udp_Help.reverseRst(equipmentId) +
                "68141B0038343337" + Constants.HandlerKey +
                str_Enable +
                Udp_Help.get_NBTimeTo645(tvReportingDate, 0) + "33" +
                Udp_Help.get_NBTimeTo645(tvReportingTime, 1) +
                Udp_Help.reverseRst(Udp_Help.get_NBMinTo645(etSpaceTime)) +
                Udp_Help.get_Stting_HexTo645(etRandomTime) +
                Udp_Help.get_NBTimeTo645(tvPhotoTime, 1);

        return getSendString(para_Time);
    }

    private String getSendString(String prar) {
        String cs = Udp_Help.get_sum(prar).toUpperCase() + "16";
        String sendMsg = "FEFEFEFE" + prar + cs;
        return sendMsg;
    }

}
