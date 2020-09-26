package com.ml.kubernetes.ApiClient.corda;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class CordaKubernetesClientApiClient implements com.ml.kubernetes.ApiClient.ApiClient {
    private static final CordaKubernetesClientApiClient instance = new CordaKubernetesClientApiClient();
    private ApiClient client;

    private CordaKubernetesClientApiClient() {
        String kubeConfigPath = "src/main/resources/kubectl-corda.kubeconfig";
        try {
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
            Configuration.setDefaultApiClient(client);
            client.setDebugging(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CordaKubernetesClientApiClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CordaKubernetesClientApiClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static CordaKubernetesClientApiClient getInstance() {
        return instance;
    }
}
