package com.netty.aonet.selfdefinded.model;

public enum MessageType {

    HEARBEAT_RESP((byte)3),
    HEARBEAT_REQ((byte)4),
    LOGIN_RESP((byte)2),
    LOGIN_REQ((byte)1);

    private byte value;
    MessageType( byte value){
        this.value=value;
    }

    public byte getValue() {
        return value;
    }

    public void setValue( byte value ) {
        this.value = value;
    }

    public enum MsgAction{
        REFUSE((byte)-1),
        AGREE((byte)0);

        private byte value;
        MsgAction(byte value){
            this.value=value;
        }

        public byte getValue() {
            return value;
        }

        public void setValue( byte value ) {
            this.value = value;
        }
    }
}
