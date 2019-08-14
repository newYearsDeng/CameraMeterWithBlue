package com.northmeter.camerameterwithblue.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.northmeter.camerameterwithblue.I.IShowAnalysisPic;
import com.northmeter.camerameterwithblue.I.IokhttpRequest;
import com.northmeter.camerameterwithblue.base.Constants;
import com.northmeter.camerameterwithblue.utils.GetPhoneInfo;
import com.northmeter.camerameterwithblue.utils.ImageUtil;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dyd on 2017/9/11.
 */
public class OKhttpRequest implements IokhttpRequest {
    private static final String TAG = "OKhttpRequest";
    public IShowAnalysisPic iShowAnalysisPic;
    private static OkHttpClient okHttpClient = new OkHttpClient();
    public OKhttpRequest(IShowAnalysisPic iShowAnalysisPic){
        this.iShowAnalysisPic = iShowAnalysisPic;
    }
    @Override
    public String okhttpRequest(final Bitmap bitmap,final String tanleNum) {
        new Thread(){
            public void run() {
                try{
                    String macFromWifi = GetPhoneInfo.getMacFromWifi();
                    FormEncodingBuilder builder = new FormEncodingBuilder();
                    Log.i(TAG, "--------参数----------");
                    builder.add("ClientType", "Hardware");
                    builder.add("UserID", "SZBD2016");
                    builder.add("DeviceID", tanleNum);//GetPhoneInfo.getIMEI()
                    Log.i(TAG, "DeviceID= " + GetPhoneInfo.getIMEI());
                    builder.add("MacAddress", macFromWifi);
                    Log.i(TAG, "MacAddress= " + macFromWifi);
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd-HHmmss-0SSS");//设置日期格式
                    String timestamp = df.format(new Date());
                    builder.add("Datetime", timestamp);
                    Log.i(TAG, "Datetime= " + timestamp);
                    builder.add("Province", "guangdong");
                    builder.add("City", "shenzhen");
                    String encoded = ImageUtil.picStrToBase64(bitmap);
                    builder.add("Image", encoded);
                    builder.add("LastRecord", "0,0");
//                  builder.add("Integer","5");
                    builder.add("Decimal","0");
                    Log.i(TAG, "encoded= " + encoded);
                    //builder.add("NumArea", numArea);
                    //Log.i(TAG,"NumArea= "+numArea);
                    Log.i(TAG, "------------------");
                    RequestBody requestBody = builder.build();
                    Request request = new Request.Builder().url(Constants.ANALYSISNUM)
                            .tag("")
                            .post(requestBody)
                            .build();
                    try {
                        Response response = okHttpClient.newCall(request).execute();
                        if (response.isSuccessful()) {
                            String result = response.body().string();
                            Log.i(TAG, "图片验证结果: " + result);
                            JSONObject json = JSONObject.parseObject(result);
                            String Code = json.get("Code").toString();
                            String Message = json.get("Message").toString();
                            String Value = json.get("Value").toString();
                            iShowAnalysisPic.showAnalysisPic(Code, Message, Value);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i(TAG, "netException " + e.toString());
                        iShowAnalysisPic.showAnalysisPicException("网络异常，请稍后重试");
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    iShowAnalysisPic.showAnalysisPicException("数据异常，解析出错");
                }
            }

        }.start();

        return null;
    }

}
