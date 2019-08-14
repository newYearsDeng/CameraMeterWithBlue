package com.northmeter.camerameterwithblue.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.northmeter.camerameterwithblue.I.IShowAnalysisPic;
import com.northmeter.camerameterwithblue.I.I_ShowBlueSend;
import com.northmeter.camerameterwithblue.R;
import com.northmeter.camerameterwithblue.base.BaseFragment;
import com.northmeter.camerameterwithblue.base.Constants;
import com.northmeter.camerameterwithblue.base.ToastUtil;
import com.northmeter.camerameterwithblue.bean.EvenBusBean;
import com.northmeter.camerameterwithblue.bluetooth.BlueTooth_ConnectHelper;
import com.northmeter.camerameterwithblue.bluetooth.BlueTooth_UniqueInstance;
import com.northmeter.camerameterwithblue.bluetooth.SendBlueMessage;
import com.northmeter.camerameterwithblue.enumBean.EvenBusEnum;
import com.northmeter.camerameterwithblue.presenter.OKhttpRequest;
import com.northmeter.camerameterwithblue.utils.SharedPreferencesUtil;
import com.northmeter.camerameterwithblue.utils.Udp_Help;
import com.northmeter.camerameterwithblue.widget.DragScaleViewTimeing;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by dyd on 2017/8/3.
 * NB摄像表安装测试
 */
public class Fragment_NBMeter_Install extends BaseFragment implements DragScaleViewTimeing.NotifeXY,
        TextWatcher, I_ShowBlueSend, IShowAnalysisPic {
    @BindView(R.id.edittext_x)
    EditText edittextX;
    @BindView(R.id.edittext_x_long)
    EditText edittextXLong;
    @BindView(R.id.edittext_y)
    EditText edittextY;
    @BindView(R.id.edittext_y_long)
    EditText edittextYLong;
    @BindView(R.id.button_para_select)
    Button buttonParaSelect;
    @BindView(R.id.button_para_setting)
    Button buttonParaSetting;
    Unbinder unbinder;
    @BindView(R.id.connect_flag)
    EditText connectFlag;
    @BindView(R.id.edittext_tablenum)
    EditText edittextTablenum;
    @BindView(R.id.image_camera_show)
    ImageView imageCameraShow;
    @BindView(R.id.button_analysis_picture)
    Button buttonAnalysisPicture;
    @BindView(R.id.scale_view)
    DragScaleViewTimeing scaleView;
    @BindView(R.id.relative)
    RelativeLayout relative;


    private static Fragment_NBMeter_Install fragment;
    private SendBlueMessage sendBlueMessage;
    private boolean photoFlag = false;//确定是否拍照
    private boolean compressFlag = false;//压缩率设置
    private String compressNum = "60";
    private Map<Integer,String> map_count = new HashMap();
    private Bitmap bmp = null;//读取到的水表照片
    private String photoModel = "34EE";//34EE主动上报第一张图片；  34DD手动读取第一张图片
    private OKhttpRequest oKhttpRequest;
    private int projectId;
    private String equipmentNum;

    public static Fragment_NBMeter_Install newInstance(int getType) {
        fragment = new Fragment_NBMeter_Install();
        Bundle bundle = new Bundle();
        bundle.putInt("getType", getType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_installing_test;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void startGetArgument(Bundle savedInstanceState) {
        oKhttpRequest = new OKhttpRequest(this);
        sendBlueMessage = new SendBlueMessage(this);
        projectId = getActivity().getIntent().getIntExtra("projectId",0);
        equipmentNum = SharedPreferencesUtil.getPrefString(getActivity(),"BlueNumber","000000000000");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void finishCreateView(Bundle savedInstanceState) {
        connectFlag.setText(BlueTooth_ConnectHelper.getInstance().getmConnectedDeviceName());
        scaleView.setNotifeXY(this);
        relative.measure(0, 0);
        scaleView.initScreenWAndH(relative.getMeasuredWidth(), relative.getMeasuredHeight());
        edittextTablenum.setText(equipmentNum);
        edittextTablenum.addTextChangedListener(this);
        BlueTooth_UniqueInstance.getInstance().setTableNum(equipmentNum);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        BlueTooth_UniqueInstance.getInstance().setTableNum(edittextTablenum.getText().toString());
        SharedPreferencesUtil.setPrefString(getActivity(),"BlueNumber",edittextTablenum.getText().toString());
    }

    @OnClick({R.id.button_para_select, R.id.button_para_setting,R.id.button_analysis_picture})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_analysis_picture:
                startLoadingDialog();
                oKhttpRequest.okhttpRequest(bmp,edittextTablenum.getText().toString());
                break;
            case R.id.button_para_select://查询
                startLoadingDialog();
                photoFlag = true;
                String firstpara = "68" + Udp_Help.reverseRst(edittextTablenum.getText().toString()) +
                        "68110400383310EF";
                String cs = Udp_Help.get_sum(firstpara).toUpperCase() + "16";
                String last = "FEFEFEFE" + firstpara + cs;
                sendBlueMessage.sendBTblueMessage(last,0);
                //getDefaultPic();
                break;
            case R.id.button_para_setting://设置
                if(TextUtils.isEmpty(edittextX.getText())||TextUtils.isEmpty(edittextXLong.getText())||
                        TextUtils.isEmpty(edittextY.getText())||TextUtils.isEmpty(edittextYLong.getText())){
                    showMsgLong("参数不可为空");
                }else{
                    int text_x = Integer.parseInt(edittextX.getText().toString()) + Integer.parseInt(edittextXLong.getText().toString());
                    int text_y = Integer.parseInt(edittextY.getText().toString()) + Integer.parseInt(edittextYLong.getText().toString());
                    int text_xl = Integer.parseInt(edittextXLong.getText().toString());
                    int text_yl = Integer.parseInt(edittextYLong.getText().toString());

                    if(text_xl*text_yl>30000){
                        showMsgLong("X轴长度乘以Y轴长度不能大于30000");
                    }else{
                        if (text_x > 320 || text_y > 240){
                            if (text_x > 320) {
                                showMsgLong("X轴起始坐标+X轴长度必须小于等于320");
                            } else {
                                showMsgLong("Y轴起始坐标+Y轴长度必须小于等于240");
                            }
                        } else {
                            startLoadingDialog();
                            compressFlag = true;
                            compressNum = "90";
                            String para_x = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                                    (edittextX.getText().toString()));
                            String para_y = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                                    (edittextY.getText().toString()));
                            String para_xl = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                                    (edittextXLong.getText().toString()));
                            String para_yl = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                                    (edittextYLong.getText().toString()));

                            String para_str = para_x + para_y + para_xl + para_yl;

                            String str1 = "68" + Udp_Help.reverseRst(edittextTablenum.getText().toString()) +
                                    "68141400383310EF"+ Constants.HandlerKey + para_str;
                            String cs1 = Udp_Help.get_sum(str1) + "16";
                            String last1 = "FEFEFEFE" + str1 + cs1;
                            sendBlueMessage.sendBTblueMessage(last1,0);
                            //设置图片根据窗口参数的padding值
                            int x = Integer.parseInt(edittextX.getText().toString())*relative.getMeasuredWidth()/320;
                            int y = Integer.parseInt(edittextY.getText().toString())*relative.getMeasuredHeight()/240;
                            int xl = Integer.parseInt(edittextXLong.getText().toString())*relative.getMeasuredWidth()/320;
                            int yl = Integer.parseInt(edittextYLong.getText().toString())*relative.getMeasuredHeight()/240;
                            imageCameraShow.setPadding( x, y, relative.getMeasuredWidth()-(x+xl),relative.getMeasuredHeight()-(y+yl));
                        }
                    }
                }
                break;
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                String blueMsg = (String) msg.obj;
                int state = BlueTooth_UniqueInstance.getInstance().getState();
                switch (state) {
                    case 1:
                    case 2:
                        sendBroad("Intent.NBMeter_Setting", blueMsg);
                        return;
                    case 3://主动抄图片的拍照
                        if (blueMsg.equals("success")) {//设置窗口参数成功
                            showMsg("拍照成功，正在读取图片");
                            if(photoModel.equals("34DD")){
                                //读包信息，这里读第一个图片
                                String firstpara = "68" + Udp_Help.reverseRst(edittextTablenum.getText().toString()) +
                                        "68110500333310EF34";
                                String cs = Udp_Help.get_sum(firstpara).toUpperCase() + "16";
                                String last = "FEFEFEFE" + firstpara + cs;
                                sendBlueMessage.sendBTblueMessage(last, 0);
                            }
                        } else if (blueMsg.equals("fail")) {
                            showMsg("拍照失败");
                            stopLoadingDialog();
                        }
                        break;
                    case 0:
                        if (blueMsg.equals("success")) {//设置窗口参数成功
                            showMsg("操作成功");
                            if (photoFlag) {
                                photo();
                            }
                            if(compressFlag){
                                setCompress(compressNum);
                            }
                            return;
                        } else if (blueMsg.equals("fail")) {
                            stopLoadingDialog();
                            showMsg("操作失败");
                            return;
                        }
                        String control = blueMsg.substring(22, 24).toUpperCase();
                        if (control.equals("91")) {
                            String msgflag = blueMsg.substring(28, 36).toUpperCase();
                            switch (msgflag) {
                                case "383310EF":// fefefe6821000016200168910c00383310ef 3333653373339733 9d16
                                    String blue_data = blueMsg.substring(36, blueMsg.length() - 4);
                                    edittextX.setText("" + Integer.parseInt(Udp_Help.get_645ToHex(
                                            Udp_Help.reverseRst(blue_data.substring(0, 4))), 16));
                                    edittextY.setText("" + Integer.parseInt(Udp_Help.get_645ToHex(
                                            Udp_Help.reverseRst(blue_data.substring(4, 8))), 16));
                                    edittextXLong.setText("" + Integer.parseInt(Udp_Help.get_645ToHex(
                                            Udp_Help.reverseRst(blue_data.substring(8, 12))), 16));
                                    edittextYLong.setText("" + Integer.parseInt(Udp_Help.get_645ToHex(
                                            Udp_Help.reverseRst(blue_data.substring(12, 16))), 16));

                                    int x = Integer.parseInt(Udp_Help.get_645ToHex(Udp_Help.reverseRst(blue_data.substring(0, 4))), 16)*relative.getMeasuredWidth()/320;
                                    int y = Integer.parseInt(Udp_Help.get_645ToHex(Udp_Help.reverseRst(blue_data.substring(4, 8))), 16)*relative.getMeasuredHeight()/240;
                                    int xl = Integer.parseInt(Udp_Help.get_645ToHex(Udp_Help.reverseRst(blue_data.substring(8, 12))), 16)*relative.getMeasuredWidth()/320;
                                    int yl = Integer.parseInt(Udp_Help.get_645ToHex(Udp_Help.reverseRst(blue_data.substring(12, 16))), 16)*relative.getMeasuredHeight()/240;
                                    imageCameraShow.setPadding(x,y,relative.getMeasuredWidth()-(x+xl),relative.getMeasuredHeight()-(y+yl));
                                    photo();
                                    break;
                                case "343310EF"://照片fefefe6821000016200168910802343310ef 343c3e 2cd0261e28dfde750a6c9c8
                                    String img_data = blueMsg.substring(36, blueMsg.length() - 4);

                                    String page_code_1 = Udp_Help.get_645ToHex(img_data.substring(0, 2));//图片序号

                                    //get_645ToHex获取到的数据只为减去33后的16进制数据，得到最终的参数需要转换为10进制参数
                                    int page_count_1 = Integer.parseInt(Udp_Help.get_645ToHex(img_data.substring(4, 6)), 16);//包总个数
                                    int page_count_2 = Integer.parseInt(Udp_Help.get_645ToHex(img_data.substring(2, 4)), 16);//当前包序号
                                    map_count.put(page_count_2, img_data.substring(6, img_data.length()));

                                    System.out.println(page_count_1 + "--*-*-**-*-*-" + page_count_2);
                                    if (page_count_1 - page_count_2 == 1) {
                                        try {
                                            String result = "";
                                            for (int i = 0; i < page_count_1; i++) {
                                                String datas = map_count.get(i);
                                                System.out.println("+++++++++++++++++++++++" + datas);
                                                result = result + datas;
                                            }

                                            System.out.println("+++++result++++++" + result);
                                            String img_datas = Udp_Help.get_645ToHex(result);
                                            System.out.println("+++++img_datas++++++" + img_datas);
                                            byte[] bytes = Udp_Help.strtoByteArray(img_datas);

                                            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            imageCameraShow.setImageBitmap(bmp);

                                            stopLoadingDialog();
                                            map_count.clear();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            stopLoadingDialog();
                                            showMsg("读取照片失败");
                                        }

                                    }
                                    break;

                                case "333310EF"://包信息  6823050017200168 910900333310EF  34 3C33 3435  3B16
                                    String page_data = blueMsg.substring(36, blueMsg.length() - 4);
                                    int page_index = Integer.parseInt(Udp_Help.get_645ToHex(page_data.substring(0, 2)), 16);//图片序号
                                    int page_number = Integer.parseInt(Udp_Help.get_645ToHex(Udp_Help.reverseRst(page_data.substring(2, 6))), 16);//图片包个数
                                    int page_length = Integer.parseInt(Udp_Help.get_645ToHex(Udp_Help.reverseRst(page_data.substring(6, 10))), 16);//图片长度
                                    if (page_number > 0) {
                                        getPage(0);
                                    }
                                    break;
                            }
                        }
                        break;
                }




            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void setCompress(String compress){
        photoFlag = true;
        compressFlag = false;
        String para = "68" + Udp_Help.reverseRst(edittextTablenum.getText().toString()) +
                "68140E00393310EF" + Constants.HandlerKey + "DD"+Udp_Help.get_Stting_HexTo645(compress);
        String cs = Udp_Help.get_sum(para).toUpperCase() + "16";
        String sendMsg = "FEFEFEFE" + para + cs;
        sendBlueMessage.sendBTblueMessage(sendMsg,0);
    }

    /**设置默认参数拍照*/
    private void getDefaultPic(){
        startLoadingDialog();
        photoFlag = false;
        compressFlag = true;
        compressNum = "60";
        String para_x = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                (edittextX.getText().toString()));
        String para_y = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                (edittextY.getText().toString()));
        String para_xl = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                (edittextXLong.getText().toString()));
        String para_yl = Udp_Help.reverseRst(Udp_Help.get_came_hexTo645
                (edittextYLong.getText().toString()));

        String para_str = para_x + para_y + para_xl + para_yl;

        String str1 = "68" + Udp_Help.reverseRst(edittextTablenum.getText().toString()) +
                "68141400383310EF"+ Constants.HandlerKey + para_str;
        String cs1 = Udp_Help.get_sum(str1) + "16";
        String last1 = "FEFEFEFE" + str1 + cs1;
        sendBlueMessage.sendBTblueMessage(last1,0);
        //设置图片根据窗口参数的padding值
        int x = Integer.parseInt(edittextX.getText().toString())*relative.getMeasuredWidth()/320;
        int y = Integer.parseInt(edittextY.getText().toString())*relative.getMeasuredHeight()/240;
        int xl = Integer.parseInt(edittextXLong.getText().toString())*relative.getMeasuredWidth()/320;
        int yl = Integer.parseInt(edittextYLong.getText().toString())*relative.getMeasuredHeight()/240;
        imageCameraShow.setPadding( x, y, relative.getMeasuredWidth()-(x+xl),relative.getMeasuredHeight()-(y+yl));
    }

    private void photo() {//34EE主动上报第一张图片；  34DD手动读取第一张图片
        cleanDrawable();
        photoFlag = false;
        String para_0 = "68" + Udp_Help.reverseRst(edittextTablenum.getText().toString()) +
                "68140E00363310EF" + Constants.HandlerKey + photoModel;
        String cs_0 = Udp_Help.get_sum(para_0).toUpperCase() + "16";
        String last_0 = "FEFEFEFE" + para_0 + cs_0;
        System.out.println(last_0);
        sendBlueMessage.sendBTblueMessage(last_0,0);//state 0 ：表示安装测试界面的操作，1,2表示设置界面的操作，3：表示主从读取模式的拍照
    }

    /**
     * 清除imageview里面的图片
     */
    private void cleanDrawable() {
        Drawable drawable = imageCameraShow.getDrawable();
        if (drawable != null && drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
        imageCameraShow.setImageDrawable(null);
    }

    /**读取下一包图片数据*/
    private void getPage(int i){
        String firstpara = "68" + Udp_Help.reverseRst(edittextTablenum.getText().toString()) +
                "68110700343310EF34"+Udp_Help.reverseRst(Udp_Help.get_came_hexTo645(i+""));
        String cs = Udp_Help.get_sum(firstpara).toUpperCase() + "16";
        String last = "FEFEFEFE" + firstpara + cs;
        sendBlueMessage.sendBTblueMessage(last,0);
    }

    public void sendBroad(String action, String str) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("msg", str);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void showAnalysisPic(String Code, String Message, String Value) {
        Message msg = handler_1.obtainMessage(1);
        msg.obj = Value;
        Fragment_NBMeter_Install.this.handler_1.sendMessage(msg);
    }

    @Override
    public void showAnalysisPicException(String exception) {//图片解析数据异常
        Message msg = handler_1.obtainMessage(2);
        msg.obj = exception;
        Fragment_NBMeter_Install.this.handler_1.sendMessage(msg);
    }

    private Handler handler_1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String blueMsg = (String) msg.obj;
            stopLoadingDialog();
            switch (msg.what) {
                case 1:
                    dialog_show(bmp, blueMsg.split(",")[0].split("\\.")[0]);
                    break;
                case 2:
                    ToastUtil.showToastLong(getActivity(), blueMsg);
                    break;
            }
        }
    };


    /**
     * 4.事件订阅者处理事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(EvenBusBean evenBusBean) {
        String topic = evenBusBean.getTopic();
        if(topic.equals(EvenBusEnum.EvenBus_NBMeter_Install.getEvenName())){
            String data = evenBusBean.getData();
            Message msg = handler.obtainMessage(1);
            msg.obj = data;
            Fragment_NBMeter_Install.this.handler.sendMessage(msg);
        }else if (topic.equals(EvenBusEnum.EvenBus_BlueTooth_Connect.getEvenName())) {
            connectFlag.setText(evenBusBean.getData());
            showMsg(evenBusBean.getData());
            if(evenBusBean.getData().indexOf("已连接")>=0||evenBusBean.getData().equals("连接成功")){
                sendBlueMessage.sendBTblueMessage("0000",0);
            }
        }


    }

    @Override
    public void notifeXY(int x, int y, int x_long, int y_long) {
        edittextX.setText("" + x);
        edittextY.setText("" + y);
        edittextXLong.setText("" + x_long);
        edittextYLong.setText("" + y_long);
    }

    @Override
    public void showMessage(String message) {
        showMsg(message);
        if (message.equals("蓝牙未连接")) {
            stopLoadingDialog();
        }
    }
}
