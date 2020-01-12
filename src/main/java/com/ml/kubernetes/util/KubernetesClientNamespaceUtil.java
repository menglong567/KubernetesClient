package com.ml.kubernetes.util;

import com.ml.kubernetes.client.KubernetesClientApiClient;
import com.ml.kubernetes.model.V1NamespaceCreateResult;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1DeleteOptions;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1NamespaceList;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Status;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientNamespaceUtil {
    private static final KubernetesClientNamespaceUtil instance = new KubernetesClientNamespaceUtil();

    private KubernetesClientNamespaceUtil() {
    }

    public static KubernetesClientNamespaceUtil getInstance() {
        return instance;
    }

    /**
     * @param name
     * @return
     */
    public boolean queryNamespace(String name) {
        KubernetesClientApiClient.getInstance();
        CoreV1Api api = new CoreV1Api();
        V1NamespaceList nsl = new V1NamespaceList();
        try {
            nsl = api.listNamespace(null, null, null, null, null, null, null, null, null);
            List<V1Namespace> nss = nsl.getItems();
            for (int a = 0; a < nss.size(); a++) {
                V1Namespace ns = nss.get(a);
                if (ns.getMetadata().getName().equals(name.toLowerCase())) { //a namespace must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
                    return true;
                }
            }
        } catch (ApiException e) {
            e.printStackTrace();
            System.out.println(e.getResponseBody());//this will help a lot when you fail
        }
        return false;
    }

    /**
     * @param name
     * @return
     */
    public V1NamespaceCreateResult createNamespace(String name) {
        KubernetesClientApiClient.getInstance();
        CoreV1Api api = new CoreV1Api();
        V1Namespace ns = new V1Namespace();
        V1ObjectMeta meta = new V1ObjectMeta();
        meta.setNamespace(name.toLowerCase());//a namespace must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        meta.setName(name.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        ns.setMetadata(meta);
        V1NamespaceCreateResult result = new V1NamespaceCreateResult();
        try {
            V1Namespace nsresult = api.createNamespace(ns, null, null, null);
            result.setNs(nsresult);
            result.setResult(true);
        } catch (ApiException e) {
            if (e.getCode() == 409) {
                System.out.println("Namespace with the same name already exist!");
                result.setResult(false);
                return result;
            }
            if (e.getCode() == 201) {
                System.out.println("Namespace with the same name already exist!");
                result.setResult(false);
                return result;
            }
            if (e.getCode() == 401) {
                System.out.println("Don't have the permission to create namespace!");
                result.setResult(false);
                return result;
            }
            e.printStackTrace();
            System.out.println(e.getResponseBody());//this will help a lot when you fail
        }
        return result;
    }

    /**
     * @param name
     * @param namespace
     * @return
     */
    public V1Status deleteNamespace(String name, String namespace) {
        KubernetesClientApiClient.getInstance();
        CoreV1Api api = new CoreV1Api();
        V1Status deleteResult = null;
        try {
            deleteResult = api.deleteNamespacedService(name.toLowerCase(), namespace.toLowerCase(), null, null, null, null, null, new V1DeleteOptions());
            System.out.println(deleteResult);
        } catch (ApiException ex) {
            Logger.getLogger(KubernetesClientNamespaceUtil.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
        }
        return deleteResult;
    }
}
