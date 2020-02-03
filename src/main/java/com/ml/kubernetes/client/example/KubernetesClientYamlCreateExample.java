package com.ml.kubernetes.client.example;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodBuilder;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.util.Yaml;

import java.io.IOException;
import java.util.HashMap;

/**
 * A simple example of how to parse a Kubernetes object.
 *
 * <p>
 * Easiest way to run this: mvn exec:java
 * -Dexec.mainClass="io.kubernetes.client.examples.YamlExample"
 *
 * <p>
 * From inside $REPO_DIR/examples
 */
public class KubernetesClientYamlCreateExample {
    public static void main(String[] args) throws IOException, ApiException, ClassNotFoundException {
        V1Pod pod = new V1PodBuilder()
                .withNewMetadata()
                .withName("apod")
                .endMetadata()
                .withNewSpec()
                .addNewContainer()
                .withName("www")
                .withImage("nginx")
                .withNewResources()
                .withLimits(new HashMap<>())
                .endResources()
                .endContainer()
                .endSpec()
                .build();
        System.out.println(Yaml.dump(pod));

        V1Service svc = new V1ServiceBuilder()
                .withNewMetadata()
                .withName("aservice")
                .endMetadata()
                .withNewSpec()
                .withSessionAffinity("ClientIP")
                .withType("NodePort")
                .addNewPort()
                .withProtocol("TCP")
                .withName("client")
                .withPort(8008)
                .withNodePort(8080)
                .withTargetPort(new IntOrString(8080))
                .endPort()
                .endSpec()
                .build();
        System.out.println(Yaml.dump(svc));
    }
}
