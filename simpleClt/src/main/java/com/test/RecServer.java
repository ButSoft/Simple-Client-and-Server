package com.test;

import java.io.Serializable;

public class RecServer implements Serializable {

    private static final long serialVersionUID = -45432949948539323L;

    long commandId;
    String message;
    Object result;


    public RecServer(long commandId, String message, Object result) {
        this.commandId = commandId;
        this.message = message;
        this.result = result;
    }

    @Override
    public String toString() {
        String str = "id=" + commandId;
        str += message == null || message.isEmpty() ? "" : " message=" + message;
        str += result == null ? "" : " result= " + result.toString();
        return str;
    }
}


