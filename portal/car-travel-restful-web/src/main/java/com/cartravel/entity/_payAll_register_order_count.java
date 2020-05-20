package com.cartravel.entity;

/**
 * Created by zxc
 */
public class _payAll_register_order_count {
    private String totalCount ;
    private String registerTotalCount ;
    private String pay_all ;

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getRegisterTotalCount() {
        return registerTotalCount;
    }

    public void setRegisterTotalCount(String registerTotalCount) {
        this.registerTotalCount = registerTotalCount;
    }

    public String getPay_all() {
        return pay_all;
    }

    public void setPay_all(String pay_all) {
        this.pay_all = pay_all;
    }

    @Override
    public String toString() {
        return "_payAll_register_order_count{" +
                "totalCount='" + totalCount + '\'' +
                ", registerTotalCount='" + registerTotalCount + '\'' +
                ", pay_all='" + pay_all + '\'' +
                '}';
    }
}
