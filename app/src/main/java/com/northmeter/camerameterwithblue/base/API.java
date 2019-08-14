package com.northmeter.camerameterwithblue.base;

import android.content.Context;

import com.northmeter.camerameterwithblue.utils.SharedPreferencesUtil;


/**
 * Created by dyd on 2018/12/13.
 */

public class API {
    public static String URL_BASE = "https://devapi.beidiancloud.cn/machine-cloud";//腾讯云服务器
    //public static String URL_BASE = "http://10.168.1.200:8080/machine-cloud";//200服务器
    //public static String URL_BASE = "http://10.168.1.50:801/machine-cloud";//开发者电脑服务器
    //public static String URL_BASE = "http://10.168.1.10:801/machine-cloud";//开发者电脑服务器
    //public static String URL_BASE = "http://10.168.1.123:801/machine-cloud";//开发者电脑服务器


    public static String getSharedUrl(Context context){
        return SharedPreferencesUtil.getPrefString(context,"BASE_URL",URL_BASE);
    }

    /**账户管理--------------------------GET*/
    public static String login = "/app/customers/login";

    /**项目列表查询--------------------------GET*/
    public static String getProjectList = "/app/project/list";

    /**项目建筑信息的获取-------------------------GET*/
    public static String getBuildList = "/app/project/buildinglist";

    /**设备的激活--------------------------GET*/
    public static String doactiveEquipment = "/app/buildingequipment/active";

    /**单个设备自检--------------------------GET*/
    public static String singleSelfChecking = "/app/buildingequipment/detection";

    /**单个设备自检结果查询--------------------------GET*/
    public static String singlSeselfCheckingResult = "/app/buildingequipment/equiptaskResult";

    /**设备的注册--------------------------POST*/
    public static String registereEquipment = "/app/buildingequipment/regist";

    /**设备列表查询接口--------------------------GET*/
    public static String getEquipList = "/app/buildingequipment/list";

    /**设备的删除--------------------------POST*/
    public static String deleteEquipment = "/app/buildingequipment/delete";

    /**新增设备接口--------------------------POST*/
    public static String addBuildingequipment = "/app/buildingequipment/save";

    /**新增设备时获取配置方案列表--------------------------GET*/
    public static String getConfigurationPlan = "/app/buildingequipment/getConfigurationPlan";

    /**新增设备时获取产品型号--------------------------GET*/
    public static String getProjetType = "/app/project/projetType";

    /**设备的详情--------------------------GET*/
    public static String getEquipmentDetails = "/app/buildingequipment/details";

    /**获取水表最新图片--------------------------GET*/
    public static String getMeterNetWorkPic = "/app/buildingequipment/getImageUrl";

    /**前端传入设备建筑表id，进行设备撤销注册操作--------------------------POST*/
    public static String cancelRegister = "/app/buildingequipment/cancel/register";




    /**档案的导入*/
    public static String saveEquipmentRecord = "/app/equipment/save";

    /**集中器查询接口*/
    public static String getConcentratorList = "/app/concentrator/list";

    /**档案的下发*/
    public static String saveConcentratorList = "/app/concentrator/files";



}
