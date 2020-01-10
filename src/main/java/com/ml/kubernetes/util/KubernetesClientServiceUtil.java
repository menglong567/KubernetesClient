package com.ml.kubernetes.util;

import com.ml.kubernetes.model.V1ServiceCreateResult;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1Status;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Liudan_Luo
 */
public class KubernetesClientServiceUtil {
    private static final KubernetesClientServiceUtil instance = new KubernetesClientServiceUtil();

    private KubernetesClientServiceUtil() {
    }

    public static KubernetesClientServiceUtil getInstance() {
        return instance;
    }

    /**
     * 
     * @param api
     * @param service
     * @param namespace
     * @return 
     */
    public V1ServiceCreateResult createService(CoreV1Api api, V1Service service, String namespace) {
        V1Service srv = null;
        V1ServiceCreateResult result = new V1ServiceCreateResult();
        try {
            srv = api.createNamespacedService("default", service, null, null, null);
        } catch (ApiException ex) {
            Logger.getLogger(KubernetesClientServiceUtil.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
            result.setResult(false);
            result.setSrv(null);
        }
        result.setResult(true);
        result.setSrv(srv);
        return result;
    }

    /**
     *
     * @param api
     * @param name
     * @param namespace
     * @return
     */
    public V1Status deleteService(CoreV1Api api, String name, String namespace) {
        V1Status deleteResult = null;
        try {
            deleteResult = api.deleteNamespacedService(name, namespace, null, null, null, null, null, new V1DeleteOptions());
        } catch (ApiException ex) {
            Logger.getLogger(KubernetesClientServiceUtil.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
        }
        return deleteResult;
    }
}
