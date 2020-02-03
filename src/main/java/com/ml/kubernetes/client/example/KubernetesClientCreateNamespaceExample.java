package com.ml.kubernetes.client.example;

import com.ml.kubernetes.client.util.KubernetesClientNamespaceUtil;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientCreateNamespaceExample {
    public static void main(String[] args) {
        KubernetesClientNamespaceUtil.getInstance().createNamespace("test");
    }
}
