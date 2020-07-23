package com.ml.kubernetes.example;

import com.ml.kubernetes.ApiClient.MultichainKubernetesClientApiClient;
import com.ml.kubernetes.result.V1ServiceCreateResult;
import com.ml.kubernetes.util.KubernetesClientServiceUtil;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1Status;
import io.kubernetes.client.util.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientYamlLoadDeployServiceExample {
    public static void main(String[] args) {
        MultichainKubernetesClientApiClient.getInstance();
        //  See issue #474. Not needed at most cases, but it is needed if you are using war packging or running this on JUnit.
        Yaml.addModelMap("v1", "Service", V1Service.class);
        File file = new File("src/main/resources/test-svc.yaml");
        V1Service yamlSvc;
        try {
            yamlSvc = (V1Service) Yaml.load(file);
            // Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
            CoreV1Api api = new CoreV1Api();

            V1ServiceCreateResult result = KubernetesClientServiceUtil.getInstance().createService(api, yamlSvc, "default");
            System.out.println(result);

            V1Status deleteResult = KubernetesClientServiceUtil.getInstance().deleteService(api, yamlSvc.getMetadata().getName(), "default");
            System.out.println(deleteResult);
            /**
             * class V1Status { apiVersion: v1 code: null details: class
             * V1StatusDetails { causes: null group: null kind: services name:
             * test-service retryAfterSeconds: null uid:
             * 0490c944-851e-4018-aeb9-6587832d7c1d } kind: Status message: null
             * metadata: class V1ListMeta { _continue: null remainingItemCount:
             * null resourceVersion: null selfLink: null } reason: null status:
             * Success }
             */
        } catch (IOException ex) {
            Logger.getLogger(KubernetesClientYamlLoadDeployServiceExample.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
