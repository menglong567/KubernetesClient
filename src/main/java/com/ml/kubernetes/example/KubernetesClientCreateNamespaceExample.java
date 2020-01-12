package com.ml.kubernetes.example;

import com.ml.kubernetes.util.KubernetesClientNamespaceUtil;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientCreateNamespaceExample {
    public static void main(String[] args) {
        KubernetesClientNamespaceUtil.getInstance().createNamespace("test");
    }
}
