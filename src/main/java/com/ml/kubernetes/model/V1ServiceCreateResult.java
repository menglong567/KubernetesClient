package com.ml.kubernetes.model;

import io.kubernetes.client.openapi.models.V1Service;

/**
 *
 * @author Liudan_Luo
 */
public class V1ServiceCreateResult {
    public V1Service getSrv() {
        return srv;
    }

    public void setSrv(V1Service srv) {
        this.srv = srv;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
    private V1Service srv;
    private boolean result;
}
