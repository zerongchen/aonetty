package com.netty.aonet.selfdefinded.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class Header {
    private int crcCode=0xabef0101;
    private int length;
    private long sessionID;
    private byte type;
    private byte priority;
    private Map<String,Object> attachment=new HashMap<>();
}
