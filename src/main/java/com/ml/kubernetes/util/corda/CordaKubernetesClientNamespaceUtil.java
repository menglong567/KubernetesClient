package com.ml.kubernetes.util.corda;

import com.ml.kubernetes.ApiClient.corda.CordaKubernetesClientApiClient;
import com.ml.kubernetes.result.V1NamespaceCreateResult;
import com.ml.kubernetes.util.GSonUtil;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Liudan_Luo
 */
public class CordaKubernetesClientNamespaceUtil {
    private static final CordaKubernetesClientNamespaceUtil instance = new CordaKubernetesClientNamespaceUtil();
    private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CordaKubernetesClientNamespaceUtil.class);

    private CordaKubernetesClientNamespaceUtil() {
    }

    public static CordaKubernetesClientNamespaceUtil getInstance() {
        return instance;
    }

    /**
     * @param name
     * @return
     */
    public boolean queryNamespace(String name) {
        CordaKubernetesClientApiClient.getInstance();
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
            LOGGER.error(e.getResponseBody());
            System.out.println(e.getResponseBody());
        }
        return false;
    }

    /**
     * @param name
     * @return
     */
    public V1NamespaceCreateResult createNamespace(String name) {
        CordaKubernetesClientApiClient.getInstance();
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
                LOGGER.error("Namespace with the same name already exist!");
                result.setResult(false);
                return result;
            }
            if (e.getCode() == 201) {
                System.out.println("Namespace with the same name already exist!");
                LOGGER.error("Namespace with the same name already exist!");
                result.setResult(false);
                return result;
            }
            if (e.getCode() == 401) {
                System.out.println("Don't have the permission to create namespace!");
                LOGGER.error("Don't have the permission to create namespace!");
                result.setResult(false);
                return result;
            }
            e.printStackTrace();
            LOGGER.error(e.getResponseBody());
            System.out.println(e.getResponseBody());
        }
        return result;
    }

    /**
     * @param name
     * @param namespace
     * @return
     */
    public V1Status deleteNamespace(String name, String namespace) {
        CordaKubernetesClientApiClient.getInstance();
        CoreV1Api api = new CoreV1Api();
        V1Status deleteResult = null;
        try {
            deleteResult = api.deleteNamespacedService(name.toLowerCase(), namespace.toLowerCase(), null, null, null, null, null, new V1DeleteOptions());
            System.out.println(deleteResult);
            LOGGER.info(GSonUtil.getInstance().object2Json(deleteResult));
        } catch (ApiException ex) {
            System.out.println(ex.getResponseBody());
            LOGGER.error(ex.getResponseBody());
        }
        return deleteResult;
    }
}
