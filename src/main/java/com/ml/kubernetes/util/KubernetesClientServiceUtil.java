package com.ml.kubernetes.util;

import com.ml.kubernetes.result.V1ServiceCreateResult;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1Status;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientServiceUtil {
    private static final KubernetesClientServiceUtil instance = new KubernetesClientServiceUtil();
    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(KubernetesClientServiceUtil.class);

    private KubernetesClientServiceUtil() {
    }

    public static KubernetesClientServiceUtil getInstance() {
        return instance;
    }

    /**
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
            System.out.println(ex.getResponseBody());
            LOGGER.error(ex.getResponseBody());
            result.setResult(false);
            result.setSrv(null);
        }
        result.setResult(true);
        result.setSrv(srv);
        return result;
    }

    /**
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
            LOGGER.error(ex.getResponseBody());
            System.out.println(ex.getResponseBody());
        }
        return deleteResult;
    }
}
