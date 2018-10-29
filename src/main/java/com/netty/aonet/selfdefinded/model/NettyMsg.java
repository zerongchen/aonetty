package com.netty.aonet.selfdefinded.model;

import lombok.Data;

/**
 * 心跳
 * 握手请求
 * 握手应答
 */
@Data
public class NettyMsg {
    private Header header;
    private Object body;
}
