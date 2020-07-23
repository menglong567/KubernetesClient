package com.ml.kubernetes.example;

import com.ml.kubernetes.util.MultichainKubernetesClientNamespaceUtil;

/**
 * @author Liudan_Luo
 */
public class KubernetesClientQueryNamespaceExample {
    public static void main(String[] args) {
        boolean b = MultichainKubernetesClientNamespaceUtil.getInstance().queryNamespace("test");
        System.out.println(b);
    }
}
