package com.netty.aonet.seri.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubcribeReq implements Serializable {

    private int reqId;
    private String name;
    private String productName;
    private String phoneNo;
    private String address;

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "name: "+getName()+",reqid:"+getReqId()+",phoneNo:"+getPhoneNo()+",adress:"+getAddress()+",productName:"+getProductName();
    }
}
