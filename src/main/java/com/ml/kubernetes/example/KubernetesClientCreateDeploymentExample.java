package com.ml.kubernetes.example;

import com.ml.kubernetes.ApiClient.multichain.MultichainKubernetesClientApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1ObjectMeta;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientCreateDeploymentExample {
    public Object createDeployment(String name) {
        MultichainKubernetesClientApiClient.getInstance();
        CoreV1Api api = new CoreV1Api();
        AppsV1Api appsV1Api = new AppsV1Api();
        V1Deployment deployment = new V1Deployment();
        deployment.setApiVersion("apps/v1");
        deployment.setKind("Deployment");
        V1ObjectMeta meta = new V1ObjectMeta();
        meta.setNamespace(name);
        meta.setName(name);
        deployment.setMetadata(meta);
        V1DeploymentSpec spec = new V1DeploymentSpec();
        spec.setReplicas(1);
        deployment.setSpec(spec);
        try {
            deployment = appsV1Api.createNamespacedDeployment(name, deployment, null, null, null);
            System.out.println(deployment);
        } catch (ApiException e) {
            e.printStackTrace();
            System.out.println(e.getResponseBody());//this will help a lot when you fail
        }
        return deployment;
    }
}
