package com.ml.kubernetes.result;

import io.kubernetes.client.openapi.models.V1Namespace;

/**
 * @author Liudan_Luo
 */
public class V1NamespaceCreateResult {
    private V1Namespace ns;
    private boolean result;

    public V1Namespace getNs() {
        return ns;
    }

    public void setNs(V1Namespace ns) {
        this.ns = ns;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
