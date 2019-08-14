package com.northmeter.camerameterwithblue.I;

/**
 * Created by dyd on 2017/9/13.
 */
public interface IShowAnalysisPic {
    /**图片解析结果*/
    void showAnalysisPic(String Code, String Message, String Value);
    void showAnalysisPicException(String exception);
}
