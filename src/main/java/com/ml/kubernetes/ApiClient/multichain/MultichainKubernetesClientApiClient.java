package com.ml.kubernetes.ApiClient.multichain;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Liudan_Luo
 */
public class MultichainKubernetesClientApiClient implements com.ml.kubernetes.ApiClient.ApiClient {
    private static final MultichainKubernetesClientApiClient instance = new MultichainKubernetesClientApiClient();
    private ApiClient client;

    private MultichainKubernetesClientApiClient() {
        try {
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader("/resources/kubectl-multichain.kubeconfig"))).build();
            Configuration.setDefaultApiClient(client);
            client.setDebugging(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MultichainKubernetesClientApiClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MultichainKubernetesClientApiClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static MultichainKubernetesClientApiClient getInstance() {
        return instance;
    }
}
