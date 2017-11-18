package com.test;

import java.io.Serializable;

public class RecClient implements Serializable {

    private static final long serialVersionUID = -4543294994853932372L;

    long commandId;
    String serviceName;
    String methodName;
    Object[] params;

    public RecClient(long commandId, String serviceName, String methodName, Object[] params) {
        this.commandId = commandId;
        this.serviceName = serviceName;
        this.methodName = methodName;
        this.params = params;
    }

    @Override
    public String toString() {
        String str = "id=" + commandId;
        str += serviceName == null || serviceName.isEmpty() ? "" : " service=" + serviceName;
        str += methodName == null || methodName.isEmpty() ? "" : " service=" + methodName;
        if (null != params) {
            str += " params:";
            for (Object obj : params)
                if (null != obj)
                    str += " par=" + obj.toString();
        }
        return str;
    }
}
