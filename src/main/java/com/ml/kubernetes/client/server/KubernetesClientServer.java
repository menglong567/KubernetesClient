package com.ml.kubernetes.client.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author luoliudan
 */
@SpringBootApplication(scanBasePackages = "com.ml")
public class KubernetesClientServer {
    public static void main(String[] args) {
        SpringApplication.run(KubernetesClientServer.class, args);
    }
}
