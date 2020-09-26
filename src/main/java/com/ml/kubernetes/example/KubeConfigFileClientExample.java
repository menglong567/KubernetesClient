package com.ml.kubernetes.example;

import com.ml.kubernetes.ApiClient.multichain.MultichainKubernetesClientApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple example of how to use the Java API from an application outside a
 * kubernetes cluster
 *
 * @author luoliudan
 */
public class KubeConfigFileClientExample {
    public static void main(String[] args) {
        try {
            MultichainKubernetesClientApiClient.getInstance();
            CoreV1Api api = new CoreV1Api();
            V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
            for (V1Pod item : list.getItems()) {
                System.out.println(item.getMetadata().getName());
            }
        } catch (ApiException ex) {
            Logger.getLogger(KubeConfigFileClientExample.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
        }
    }
}
