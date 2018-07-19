package com.netty.aonet.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncTimeServerHandle> {
    @Override
    public void completed( AsynchronousSocketChannel result, AsyncTimeServerHandle attachment ) {
        /**
         * 多个地方存在accept
         * 新的客户端链接，系统将回调我们传进去的CompletionHandler 执行completed
         * 一个AsynchronousSocketChannel 链接成千上万个客户端,持续调用他的accept 方法,接受其他客户端链接,形成一个闭环
         */
        attachment.asynchronousServerSocketChannel.accept(attachment,this);
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        result.read(byteBuffer,byteBuffer,new ReadCompletionHandler(result));
    }

    @Override
    public void failed( Throwable exc, AsyncTimeServerHandle attachment ) {
        ((AsyncTimeServerHandle)attachment).latch.countDown();
    }
}
