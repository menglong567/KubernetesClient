package com.ml.kubernetes.client.corda.model;


/**
 * @author luoliudan
 */
public class CordaNodeCreationResult {
    private boolean result;
    private String msg;
    public CordaNodeCreationResult(String msg, boolean result){
        this.msg=msg;
        this.result=result;
    }
    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
