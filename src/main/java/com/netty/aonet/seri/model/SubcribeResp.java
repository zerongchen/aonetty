package com.netty.aonet.seri.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubcribeResp implements Serializable {

    private int reqId;
    private String respCode;
    private String desc;
}
