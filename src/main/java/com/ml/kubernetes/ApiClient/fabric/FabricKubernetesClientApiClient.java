package com.ml.kubernetes.ApiClient.fabric;/*
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
public class FabricKubernetesClientApiClient implements com.ml.kubernetes.ApiClient.ApiClient {
    private static final FabricKubernetesClientApiClient instance = new FabricKubernetesClientApiClient();
    private ApiClient client;

    private FabricKubernetesClientApiClient() {
        String kubeConfigPath = "src/main/resources/kubectl-fabric.kubeconfig";
        try {
            client = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(new FileReader(kubeConfigPath))).build();
            Configuration.setDefaultApiClient(client);
            client.setDebugging(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FabricKubernetesClientApiClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FabricKubernetesClientApiClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static FabricKubernetesClientApiClient getInstance() {
        return instance;
    }
}
