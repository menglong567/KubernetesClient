package com.ml.kubernetes.client.corda.nodeCreator;

import com.ml.kubernetes.ApiClient.corda.CordaKubernetesClientApiClient;
import com.ml.kubernetes.client.corda.model.CordaNodeCreationResult;
import com.ml.kubernetes.result.V1NamespaceCreateResult;
import com.ml.kubernetes.util.CommonUtil;
import com.ml.kubernetes.util.FileReaderUtil;
import com.ml.kubernetes.util.PlaceHolderUtil;
import com.ml.kubernetes.util.corda.CordaKubernetesClientNamespaceUtil;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mengl
 */
public class CordappRepoServerCreator {
    private static final CordappRepoServerCreator instance = new CordappRepoServerCreator();
    private static Logger LOGGER = LoggerFactory.getLogger(CordappRepoServerCreator.class);

    private CordappRepoServerCreator() {
    }

    public static CordappRepoServerCreator getInstance() {
        return instance;
    }

    /****
     *
     * @param cordappRepoServerNamespace
     * @param cordappRepoServerNodeport
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param cordappRepoServerTemplate
     * @return
     */
    public CordaNodeCreationResult createCordappRepoServerLoadAll(String cordappRepoServerNamespace, String cordappRepoServerNodeport, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String cordappRepoServerTemplate) {
        if (cordappRepoServerNamespace == null || cordappRepoServerNamespace.isEmpty()) {
            LOGGER.error("CordappRepoNamespace is null");
            return new CordaNodeCreationResult("CordappRepoNamespace is null", false);
        }
        if (cordappRepoServerNodeport == null || cordappRepoServerNodeport.isEmpty()) {//here should another logic to check the x.509 name
            LOGGER.error("cordappRepoServerNodeport is null");
            return new CordaNodeCreationResult("cordappRepoServerNodeport is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cordappRepoServerNodeport)) {
            LOGGER.error(cordappRepoServerNodeport + " is not a valid number");
            return new CordaNodeCreationResult(cordappRepoServerNodeport + " is not a valid number", false);
        }
        //verify the range of WebserverNodeport
        if (Integer.parseInt(cordappRepoServerNodeport) < 30000 || Integer.parseInt(cordappRepoServerNodeport) > 32767) {
            LOGGER.error(cordappRepoServerNodeport + " is beyond the valid range <30000,32767>");
            return new CordaNodeCreationResult(cordappRepoServerNodeport + " is beyond the valid range from 30000 to 32767", false);
        }

        if (memoryRequest == null || memoryRequest.isEmpty()) {
            LOGGER.error("memoryRequest is null");
            return new CordaNodeCreationResult("memoryRequest is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(memoryRequest)) {
            LOGGER.error(memoryRequest + " is not a valid number");
            return new CordaNodeCreationResult(memoryRequest + " is not a valid number", false);
        }
        if (cpuRequest == null || cpuRequest.isEmpty()) {
            LOGGER.error("cpuRequest is null");
            return new CordaNodeCreationResult("cpuRequest is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cpuRequest)) {
            LOGGER.error(cpuRequest + " is not a valid number");
            return new CordaNodeCreationResult(cpuRequest + " is not a valid number", false);
        }
        if (memoryLimit == null || memoryLimit.isEmpty()) {
            LOGGER.error("memoryLimit is null");
            return new CordaNodeCreationResult("memoryLimit is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(memoryLimit)) {
            LOGGER.error(memoryLimit + " is not a valid number");
            return new CordaNodeCreationResult(memoryLimit + " is not a valid number", false);
        }
        if (cpuLimit == null || cpuLimit.isEmpty()) {
            LOGGER.error("cpuLimit is null");
            return new CordaNodeCreationResult("cpuLimit is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cpuLimit)) {
            LOGGER.error(cpuLimit + " is not a valid number");
            return new CordaNodeCreationResult(cpuLimit + " is not a valid number", false);
        }
        if (cordappRepoServerTemplate == null || cordappRepoServerTemplate.isEmpty()) {
            LOGGER.error("cordappRepoServerTemplate is null");
            return new CordaNodeCreationResult("cordappRepoServerTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("cordappRepoServerNamespace", cordappRepoServerNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("cordappRepoServerNodeport", cordappRepoServerNodeport);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        CordaKubernetesClientApiClient.getInstance();
        if (!CordaKubernetesClientNamespaceUtil.getInstance().queryNamespace(cordappRepoServerNamespace)) {
            V1NamespaceCreateResult result = CordaKubernetesClientNamespaceUtil.getInstance().createNamespace(cordappRepoServerNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + cordappRepoServerNamespace + " creation failed!");
                return new CordaNodeCreationResult("Namespace:" + cordappRepoServerNamespace + " creation failed!", false);
            } else {
                LOGGER.info("Namespace:" + cordappRepoServerNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + cordappRepoServerNamespace + " already exists!");
        }

        //parse master template file content and replace with user input
        String cordappreposerverTemplateYAML = FileReaderUtil.getInstance().readFileByLines(cordappRepoServerTemplate);
        if (cordappreposerverTemplateYAML == null || cordappreposerverTemplateYAML.isEmpty()) {
            LOGGER.error("Generated cordappreposerverTemplateYAML is null!");
            return new CordaNodeCreationResult("Generated cordappreposerverTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        cordappreposerverTemplateYAML = sub.replace(cordappreposerverTemplateYAML);

        //connect kubernetes and create resource
        V1Deployment v1Deployment;
        V1Service nodeport;
        List<Object> objs = new ArrayList<>();
        CoreV1Api corev1 = new CoreV1Api();

        try {
            objs = Yaml.loadAll(cordappreposerverTemplateYAML);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new CordaNodeCreationResult(ex.getMessage(), false);
        }
        v1Deployment = (V1Deployment) objs.get(0);
        nodeport = (V1Service) objs.get(1);

        //create service
        try {
            corev1.createNamespacedService(nodeport.getMetadata().getNamespace(), nodeport, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("Nodeport service:" + nodeport.getMetadata().getName() + " created successfully!");

        //Deployment and StatefulSet is defined in apps/v1, so should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        try {
            appv1.createNamespacedDeployment(v1Deployment.getMetadata().getNamespace(), v1Deployment, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("v1Deployment:" + v1Deployment.getMetadata().getName() + " created successfully!");
        return new CordaNodeCreationResult("CordappRepoServer: " + " Creation successfully finished!", true);
    }

    /****
     *
     * @param cordappRepoServerNamespace
     * @param cordappRepoServerNodeport
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param cordappRepoServerTemplate
     * @return
     */
    public CordaNodeCreationResult createCordappRepoServerLoadAllv2(String cordappRepoServerNamespace, String cordappRepoServerNodeport, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String cordappRepoServerTemplate) {
        if (cordappRepoServerNamespace == null || cordappRepoServerNamespace.isEmpty()) {
            LOGGER.error("cordappRepoServerNamespace is null");
            return new CordaNodeCreationResult("cordappRepoServerNamespace is null", false);
        }
        if (cordappRepoServerNodeport == null || cordappRepoServerNodeport.isEmpty()) {
            LOGGER.error("cordappRepoServerNodeport is null");
            return new CordaNodeCreationResult("cordappRepoServerNodeport is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cordappRepoServerNodeport)) {
            LOGGER.error(cordappRepoServerNodeport + " is not a valid number");
            return new CordaNodeCreationResult(cordappRepoServerNodeport + " is not a valid number", false);
        }
        //verify the range of nodeportnetworkPort
        if (Integer.parseInt(cordappRepoServerNodeport) < 30000 || Integer.parseInt(cordappRepoServerNodeport) > 32767) {
            LOGGER.error(cordappRepoServerNodeport + " is beyond the valid range <30000,32767>");
            return new CordaNodeCreationResult(cordappRepoServerNodeport + " is beyond the valid range from 30000 to 32767", false);
        }
        if (memoryRequest == null || memoryRequest.isEmpty()) {
            LOGGER.error("memoryRequest is null");
            return new CordaNodeCreationResult("memoryRequest is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(memoryRequest)) {
            LOGGER.error(memoryRequest + " is not a valid number");
            return new CordaNodeCreationResult(memoryRequest + " is not a valid number", false);
        }
        if (cpuRequest == null || cpuRequest.isEmpty()) {
            LOGGER.error("cpuRequest is null");
            return new CordaNodeCreationResult("cpuRequest is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cpuRequest)) {
            LOGGER.error(cpuRequest + " is not a valid number");
            return new CordaNodeCreationResult(cpuRequest + " is not a valid number", false);
        }
        if (memoryLimit == null || memoryLimit.isEmpty()) {
            LOGGER.error("memoryLimit is null");
            return new CordaNodeCreationResult("memoryLimit is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(memoryLimit)) {
            LOGGER.error(memoryLimit + " is not a valid number");
            return new CordaNodeCreationResult(memoryLimit + " is not a valid number", false);
        }
        if (cpuLimit == null || cpuLimit.isEmpty()) {
            LOGGER.error("cpuLimit is null");
            return new CordaNodeCreationResult("cpuLimit is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(cpuLimit)) {
            LOGGER.error(cpuLimit + " is not a valid number");
            return new CordaNodeCreationResult(cpuLimit + " is not a valid number", false);
        }
        if (cordappRepoServerTemplate == null || cordappRepoServerTemplate.isEmpty()) {
            LOGGER.error("cordappRepoServerTemplate is null");
            return new CordaNodeCreationResult("cordappRepoServerTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("cordappRepoServerNamespace", cordappRepoServerNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("cordappRepoServerNodeport", cordappRepoServerNodeport);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        CordaKubernetesClientApiClient.getInstance();
        if (!CordaKubernetesClientNamespaceUtil.getInstance().queryNamespace(cordappRepoServerNamespace)) {
            V1NamespaceCreateResult result = CordaKubernetesClientNamespaceUtil.getInstance().createNamespace(cordappRepoServerNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + cordappRepoServerNamespace + " creation failed!");
                return new CordaNodeCreationResult("Namespace:" + cordappRepoServerNamespace + " creation failed!", false);
            } else {
                LOGGER.info("Namespace:" + cordappRepoServerNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + cordappRepoServerNamespace + " already exists!");
        }

        //parse master template file content and replace with user input
        String cordappreposerverTemplateYAML = FileReaderUtil.getInstance().readFileByLines(cordappRepoServerTemplate);
        if (cordappreposerverTemplateYAML == null || cordappreposerverTemplateYAML.isEmpty()) {
            LOGGER.error("Generated cordappreposerverTemplateYAML is null!");
            return new CordaNodeCreationResult("Generated cordappreposerverTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        cordappreposerverTemplateYAML = sub.replace(cordappreposerverTemplateYAML);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        V1Service nodeportSvc;
        List<Object> objs = null;
        try {
            objs = Yaml.loadAll(cordappreposerverTemplateYAML);
        } catch (IOException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getMessage());
            return new CordaNodeCreationResult(ex.getMessage(), false);
        }
        statefulSet = (V1StatefulSet) objs.get(0);
        headlessSvc = (V1Service) objs.get(1);
        nodeportSvc = (V1Service) objs.get(2);

        CoreV1Api corev1 = new CoreV1Api();
        try {
            corev1.createNamespacedService(headlessSvc.getMetadata().getNamespace(), headlessSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }

        try {
            corev1.createNamespacedService(nodeportSvc.getMetadata().getNamespace(), nodeportSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }

        //Deployment and StatefulSet is defined in apps/v1, so should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        try {
            appv1.createNamespacedStatefulSet(statefulSet.getMetadata().getNamespace(), statefulSet, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }
        return new CordaNodeCreationResult("Cordappreposerver creation successfully finished!", true);
    }
}
