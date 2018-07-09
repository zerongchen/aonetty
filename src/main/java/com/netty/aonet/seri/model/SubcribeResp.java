package com.netty.aonet.seri.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubcribeResp implements Serializable {

    private int reqId;
    private String respCode;
    private String desc;

    public int getReqId() {
        return reqId;
    }

    public void setReqId(int reqId) {
        this.reqId = reqId;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
