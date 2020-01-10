package com.ml.kubernetes.model;

import io.kubernetes.client.openapi.models.V1Deployment;

/**
 *
 * @author Liudan_Luo
 */
public class V1DeploymentCreateResult {
    private V1Deployment deployment;
    private boolean result;

    public V1Deployment getDeployment() {
        return deployment;
    }

    public void setDeployment(V1Deployment deployment) {
        this.deployment = deployment;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
