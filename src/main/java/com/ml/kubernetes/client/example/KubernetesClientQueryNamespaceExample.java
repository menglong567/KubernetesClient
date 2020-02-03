package com.ml.kubernetes.client.example;

import com.ml.kubernetes.client.util.KubernetesClientNamespaceUtil;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientQueryNamespaceExample {
    public static void main(String[] args) {
        boolean b = KubernetesClientNamespaceUtil.getInstance().queryNamespace("test");
        System.out.println(b);
    }
}
