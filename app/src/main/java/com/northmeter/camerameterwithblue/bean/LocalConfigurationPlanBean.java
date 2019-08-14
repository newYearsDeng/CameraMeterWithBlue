package com.northmeter.camerameterwithblue.bean;

import java.util.List;

/**
 * Created by dyd on 2019/3/12.
 * 查询本地配置方案列表
 */

public class LocalConfigurationPlanBean extends CommonResponse {

    public List<PlanBean> list;

    public List<PlanBean> getList() {
        return list;
    }

    public void setList(List<PlanBean> list) {
        this.list = list;
    }

    public static class PlanBean{
        private String address;
        private String name;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


}
