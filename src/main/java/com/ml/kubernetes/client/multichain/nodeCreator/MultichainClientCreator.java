package com.ml.kubernetes.client.multichain.nodeCreator;

import com.ml.kubernetes.ApiClient.multichain.MultichainKubernetesClientApiClient;
import com.ml.kubernetes.client.multichain.model.MultichainNodeCreationResult;
import com.ml.kubernetes.result.V1NamespaceCreateResult;
import com.ml.kubernetes.util.CommonUtil;
import com.ml.kubernetes.util.FileReaderUtil;
import com.ml.kubernetes.util.PlaceHolderUtil;
import com.ml.kubernetes.util.multichain.MultichainKubernetesClientNamespaceUtil;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.util.Yaml;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MultichainClientCreator {
    private static Logger LOGGER = LoggerFactory.getLogger(MultichainClientCreator.class);
    private static final MultichainClientCreator instance = new MultichainClientCreator();

    private MultichainClientCreator() {
    }

    public static MultichainClientCreator getInstance() {
        return instance;
    }

    /****
     *
     * @param multichainClientNamespace
     * @param multichainServerNodeport
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param multichainClientYamlTemplate
     * @return
     */
    public MultichainNodeCreationResult createMultichainClientServerLoadAll(String multichainClientNamespace, String multichainServerNodeport, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String multichainClientYamlTemplate) {
        if (multichainClientNamespace == null || multichainClientNamespace.isEmpty()) {
            LOGGER.error("multichainClientNamespace is null");
            return new MultichainNodeCreationResult("multichainClientNamespace is null", false);
        }
        if (multichainServerNodeport == null || multichainServerNodeport.isEmpty()) {
            LOGGER.error("multichainServerNodeport is null");
            return new MultichainNodeCreationResult("MultichainServerNodeport is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(multichainServerNodeport)) {
            LOGGER.error(multichainServerNodeport + " is not a valid number");
            return new MultichainNodeCreationResult(multichainServerNodeport + " is not a valid number", false);
        }
        //verify the range of multichainServerNodeport
        if (Integer.parseInt(multichainServerNodeport) < 30000 || Integer.parseInt(multichainServerNodeport) > 32767) {
            LOGGER.error(multichainServerNodeport + " is beyond the valid range <30000,32767>");
            return new MultichainNodeCreationResult(multichainServerNodeport + " is beyond the valid range from 30000 to 32767", false);
        }

        if (memoryRequest == null || memoryRequest.isEmpty()) {
            LOGGER.error("memoryRequest is null");
            return new MultichainNodeCreationResult("memoryRequest is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(memoryRequest)) {
            LOGGER.error(memoryRequest + " is not a valid number");
            return new MultichainNodeCreationResult(memoryRequest + " is not a valid number", false);
        }
        if (cpuRequest == null || cpuRequest.isEmpty()) {
            LOGGER.error("cpuRequest is null");
            return new MultichainNodeCreationResult("cpuRequest is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cpuRequest)) {
            LOGGER.error(cpuRequest + " is not a valid number");
            return new MultichainNodeCreationResult(cpuRequest + " is not a valid number", false);
        }
        if (memoryLimit == null || memoryLimit.isEmpty()) {
            LOGGER.error("memoryLimit is null");
            return new MultichainNodeCreationResult("memoryLimit is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(memoryLimit)) {
            LOGGER.error(memoryLimit + " is not a valid number");
            return new MultichainNodeCreationResult(memoryLimit + " is not a valid number", false);
        }
        if (cpuLimit == null || cpuLimit.isEmpty()) {
            LOGGER.error("cpuLimit is null");
            return new MultichainNodeCreationResult("cpuLimit is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cpuLimit)) {
            LOGGER.error(cpuLimit + " is not a valid number");
            return new MultichainNodeCreationResult(cpuLimit + " is not a valid number", false);
        }
        if (multichainClientYamlTemplate == null || multichainClientYamlTemplate.isEmpty()) {
            LOGGER.error("multichainClientYamlTemplate is null");
            return new MultichainNodeCreationResult("multichainClientYamlTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("multichainClientNamespace", multichainClientNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("multichainServerNodeport", multichainServerNodeport);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        MultichainKubernetesClientApiClient.getInstance();
        if (!MultichainKubernetesClientNamespaceUtil.getInstance().queryNamespace(multichainClientNamespace)) {
            V1NamespaceCreateResult result = MultichainKubernetesClientNamespaceUtil.getInstance().createNamespace(multichainClientNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + multichainClientNamespace + " creation failed!");
                return new MultichainNodeCreationResult("Namespace:" + multichainClientNamespace + " creation failed!", false);
            } else {
                LOGGER.error("Namespace:" + multichainClientNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + multichainClientNamespace + " already exists!");
        }

        //parse master template file content and replace with user input
        String multichainClientYaml = FileReaderUtil.getInstance().readFileByLines(multichainClientYamlTemplate);
        if (multichainClientYaml == null || multichainClientYaml.isEmpty()) {
            LOGGER.error("Generated multichainClientYaml is null!");
            return new MultichainNodeCreationResult("Generated multichainClientYaml is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        multichainClientYaml = sub.replace(multichainClientYaml);

        //connect kubernetes and create resource
        V1Deployment v1Deployment;
        V1Service nodeport;
        List<Object> objs = new ArrayList<>();
        CoreV1Api corev1 = new CoreV1Api();

        try {
            objs = Yaml.loadAll(multichainClientYaml);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new MultichainNodeCreationResult(ex.getMessage(), false);
        }
        v1Deployment = (V1Deployment) objs.get(0);
        nodeport = (V1Service) objs.get(1);

        //create service
        try {
            corev1.createNamespacedService(nodeport.getMetadata().getNamespace(), nodeport, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("Nodeport service:" + nodeport.getMetadata().getName() + " created successfully!");

        //Deployment and StatefulSet is defined in apps/v1, so should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        try {
            appv1.createNamespacedDeployment(v1Deployment.getMetadata().getNamespace(), v1Deployment, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("v1Deployment:" + v1Deployment.getMetadata().getName() + " created successfully!");
        return new MultichainNodeCreationResult("Multichain-client: " + " Creation successfully finished!", true);
    }
}
