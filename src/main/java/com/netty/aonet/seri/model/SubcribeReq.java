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
}
