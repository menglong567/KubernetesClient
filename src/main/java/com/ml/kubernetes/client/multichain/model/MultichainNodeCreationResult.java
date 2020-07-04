package com.ml.kubernetes.client.multichain.model;


/**
 * @author luoliudan
 */
public class MultichainNodeCreationResult {
    private boolean result;
    private String msg;
    public MultichainNodeCreationResult(String msg,boolean result){
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
