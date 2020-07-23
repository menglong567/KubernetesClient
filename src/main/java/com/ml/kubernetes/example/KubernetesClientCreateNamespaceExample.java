package com.ml.kubernetes.example;

import com.ml.kubernetes.util.MultichainKubernetesClientNamespaceUtil;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientCreateNamespaceExample {
    public static void main(String[] args) {
        MultichainKubernetesClientNamespaceUtil.getInstance().createNamespace("test");
    }
}
