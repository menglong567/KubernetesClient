package com.ml.kubernetes.client.multichain.nodeCreator;

import com.ml.kubernetes.ApiClient.multichain.MultichainKubernetesClientApiClient;
import com.ml.kubernetes.result.V1NamespaceCreateResult;
import com.ml.kubernetes.client.multichain.model.MultichainNodeCreationResult;
import com.ml.kubernetes.util.CommonUtil;
import com.ml.kubernetes.util.FileReaderUtil;
import com.ml.kubernetes.util.multichain.MultichainKubernetesClientNamespaceUtil;
import com.ml.kubernetes.util.PlaceHolderUtil;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liudan_Luo
 */
public class MasterNodeCreator {
    private static final MasterNodeCreator instance = new MasterNodeCreator();
    private static Logger LOGGER = LoggerFactory.getLogger(MasterNodeCreator.class);

    private MasterNodeCreator() {
    }

    public static MasterNodeCreator getInstance() {
        return instance;
    }

    /**
     * **
     * Parse template files and load one by one using loadAs
     *
     * @param masterNodeName
     * @param namespace
     * @param chainName
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param nodeportnetworkPort
     * @param nodeportrpcPort
     * @param headlessTemplate
     * @param nodeportTemplate
     * @param statefulsetTemplate
     * @return MultichainNodeCreationResult
     */
    public MultichainNodeCreationResult createMasterLoadAs(String masterNodeName, String namespace, String chainName, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String nodeportnetworkPort, String nodeportrpcPort, String statefulsetTemplate, String headlessTemplate, String nodeportTemplate) {
        if (masterNodeName == null || masterNodeName.isEmpty()) {
            LOGGER.error("masterNodeName is null");
            return new MultichainNodeCreationResult("masterNodeName is null", false);
        }
        if (namespace == null || namespace.isEmpty()) {
            LOGGER.error("namespace is null");
            return new MultichainNodeCreationResult("namespace is null", false);
        }
        if (chainName == null || chainName.isEmpty()) {
            LOGGER.error("chainName is null");
            return new MultichainNodeCreationResult("chainName is null", false);
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
        if (nodeportnetworkPort == null || nodeportnetworkPort.isEmpty()) {
            LOGGER.error("nodeportnetworkPort is null");
            return new MultichainNodeCreationResult("nodeportnetworkPort is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(nodeportnetworkPort)) {
            LOGGER.error(nodeportnetworkPort + " is not a valid number");
            return new MultichainNodeCreationResult(nodeportnetworkPort + " is not a valid number", false);
        }
        //verify the range of nodeportnetworkPort
        if (Integer.parseInt(nodeportnetworkPort) < 30000 || Integer.parseInt(nodeportnetworkPort) > 32767) {
            LOGGER.error(nodeportnetworkPort + " is beyond the valid range <30000,32767>");
            return new MultichainNodeCreationResult(nodeportnetworkPort + " is beyond the valid range from 30000 to 32767", false);
        }
        if (nodeportrpcPort == null || nodeportrpcPort.isEmpty()) {
            LOGGER.error("nodeportrpcPort is null");
            return new MultichainNodeCreationResult("nodeportrpcPort is null", false);
        }
        //verify the nodeport value for nodeportrpcPort
        if (!CommonUtil.getInstance().isInteger(nodeportrpcPort)) {
            LOGGER.error(nodeportrpcPort + " is not a valid number");
            return new MultichainNodeCreationResult(nodeportrpcPort + " is not a valid number", false);
        }
        //verify the range of nodeportrpcPort
        if (Integer.parseInt(nodeportrpcPort) < 30000 || Integer.parseInt(nodeportrpcPort) > 32767) {
            LOGGER.error(nodeportrpcPort + " is beyond the valid range <30000,32767>");
            return new MultichainNodeCreationResult(nodeportrpcPort + " is beyond the valid range from 30000 to 32767", false);
        }
        if (statefulsetTemplate == null || statefulsetTemplate.isEmpty()) {
            LOGGER.error("statefulsetTemplate is null");
            return new MultichainNodeCreationResult("statefulsetTemplate is null", false);
        }
        if (headlessTemplate == null || headlessTemplate.isEmpty()) {
            LOGGER.error("headlessTemplate is null");
            return new MultichainNodeCreationResult("headlessTemplate is null", false);
        }
        if (nodeportTemplate == null || nodeportTemplate.isEmpty()) {
            LOGGER.error("nodeportTemplate is null");
            return new MultichainNodeCreationResult("nodeportTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("masterNodeName", masterNodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("namespace", namespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("chainName", chainName);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);
        PlaceHolderUtil.getInstance().addValues("nodeportnetworkPort", nodeportnetworkPort);
        PlaceHolderUtil.getInstance().addValues("nodeportrpcPort", nodeportrpcPort);
        //connect kubernetes and check namespace resource
        MultichainKubernetesClientApiClient.getInstance();
        if (!MultichainKubernetesClientNamespaceUtil.getInstance().queryNamespace(namespace)) {
            V1NamespaceCreateResult result = MultichainKubernetesClientNamespaceUtil.getInstance().createNamespace(namespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + namespace + " creation failed!");//this will help a lot when you fail
                return new MultichainNodeCreationResult("Namespace " + namespace + " creation failed!", false);
            }
        }

        //parse statefulset resource
        String statefulsetYAML = FileReaderUtil.getInstance().readFileByLines(statefulsetTemplate);
        if (statefulsetYAML == null || statefulsetYAML.isEmpty()) {
            LOGGER.error("Generated statefulsetYAML is null");
            return new MultichainNodeCreationResult("Genereated statefulsetYAML is null!", false);
        }
        String headlessYAML = FileReaderUtil.getInstance().readFileByLines(headlessTemplate);
        if (headlessYAML == null || headlessYAML.isEmpty()) {
            LOGGER.error("Generated headlessYAML is null");
            return new MultichainNodeCreationResult("Genereated headlessYAML is null!", false);
        }

        String nodeportYAML = FileReaderUtil.getInstance().readFileByLines(nodeportTemplate);
        if (nodeportYAML == null || nodeportYAML.isEmpty()) {
            LOGGER.error("Generated nodeportYAML is null");
            return new MultichainNodeCreationResult("Genereated nodeportYAML is null!", false);
        }

        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        statefulsetYAML = sub.replace(statefulsetYAML);
        //Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        V1StatefulSet statefulSet;
        try {
            statefulSet = Yaml.loadAs(statefulsetYAML, V1StatefulSet.class);
            appv1.createNamespacedStatefulSet(statefulSet.getMetadata().getNamespace(), statefulSet, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }

        CoreV1Api corev1 = new CoreV1Api();
        Yaml.addModelMap("v1", "Service", V1Service.class);

        V1Service headlessSvc;
        sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        headlessYAML = sub.replace(headlessYAML);
        try {
            headlessSvc = Yaml.loadAs(headlessYAML, V1Service.class);
            corev1.createNamespacedService(headlessSvc.getMetadata().getNamespace(), headlessSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }

        V1Service nodeportSvc;
        sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        nodeportYAML = sub.replace(nodeportYAML);
        try {
            nodeportSvc = Yaml.loadAs(nodeportYAML, V1Service.class);
            corev1.createNamespacedService(nodeportSvc.getMetadata().getNamespace(), nodeportSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }
        return new MultichainNodeCreationResult("Master Node " + masterNodeName + " Creation successfully finished!", true);
    }

    /**
     * **
     * parse yaml file and load yaml in one single call
     *
     * @param masterNodeName
     * @param namespace
     * @param chainName
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param nodeportnetworkPort
     * @param nodeportrpcPort
     * @param masterTemplateFile
     * @return MultichainNodeCreationResult
     */
    public MultichainNodeCreationResult createMasterLoadAll(String masterNodeName, String namespace, String chainName, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String nodeportnetworkPort, String nodeportrpcPort, String masterTemplateFile) {
        if (masterNodeName == null || masterNodeName.isEmpty()) {
            LOGGER.error("masterNodeName is null");
            return new MultichainNodeCreationResult("masterNodeName is null", false);
        }
        if (namespace == null || namespace.isEmpty()) {
            LOGGER.error("namespace is null");
            return new MultichainNodeCreationResult("namespace is null", false);
        }
        if (chainName == null || chainName.isEmpty()) {
            LOGGER.error("chainName is null");
            return new MultichainNodeCreationResult("chainName is null", false);
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
        if (nodeportnetworkPort == null || nodeportnetworkPort.isEmpty()) {
            LOGGER.error("nodeportnetworkPort is null");
            return new MultichainNodeCreationResult("nodeportnetworkPort is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(nodeportnetworkPort)) {
            LOGGER.error(nodeportnetworkPort + " is not a valid number");
            return new MultichainNodeCreationResult(nodeportnetworkPort + " is not a valid number", false);
        }
        //verify the range of nodeportnetworkPort
        if (Integer.parseInt(nodeportnetworkPort) < 30000 || Integer.parseInt(nodeportnetworkPort) > 32767) {
            LOGGER.error(nodeportnetworkPort + " is beyond the valid range <30000,32767>");
            return new MultichainNodeCreationResult(nodeportnetworkPort + " is beyond the valid range from 30000 to 32767", false);
        }
        if (nodeportrpcPort == null || nodeportrpcPort.isEmpty()) {
            LOGGER.error("nodeportrpcPort is null");
            return new MultichainNodeCreationResult("nodeportrpcPort is null", false);
        }
        //verify the nodeport value for nodeportrpcPort
        if (!CommonUtil.getInstance().isInteger(nodeportrpcPort)) {
            LOGGER.error(nodeportrpcPort + " is not a valid number");
            return new MultichainNodeCreationResult(nodeportrpcPort + " is not a valid number", false);
        }
        //verify the range of nodeportrpcPort
        if (Integer.parseInt(nodeportrpcPort) < 30000 || Integer.parseInt(nodeportrpcPort) > 32767) {
            LOGGER.error(nodeportrpcPort + " is beyond the valid range <30000,32767>");
            return new MultichainNodeCreationResult(nodeportrpcPort + " is beyond the valid range from 30000 to 32767", false);
        }
        if (masterTemplateFile == null || masterTemplateFile.isEmpty()) {
            LOGGER.error("masterTemplateFile is null");
            return new MultichainNodeCreationResult("masterTemplateFile is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("masterNodeName", masterNodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("namespace", namespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("chainName", chainName);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);
        PlaceHolderUtil.getInstance().addValues("nodeportnetworkPort", nodeportnetworkPort);
        PlaceHolderUtil.getInstance().addValues("nodeportrpcPort", nodeportrpcPort);

        //check namespace , if not exist ,create it
        MultichainKubernetesClientApiClient.getInstance();
        if (!MultichainKubernetesClientNamespaceUtil.getInstance().queryNamespace(namespace)) {
            V1NamespaceCreateResult result = MultichainKubernetesClientNamespaceUtil.getInstance().createNamespace(namespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + namespace + " creation failed!");
                return new MultichainNodeCreationResult("Namespace " + namespace + " creation failed!", false);
            }
        }

        //parse master template file content and replace with user input
        String masterTemplateYAML = FileReaderUtil.getInstance().readFileByLines(masterTemplateFile);
        if (masterTemplateYAML == null || masterTemplateYAML.isEmpty()) {
            LOGGER.error("Generated masterTemplateYAML is null!");
            return new MultichainNodeCreationResult("Generated masterTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        masterTemplateYAML = sub.replace(masterTemplateYAML);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        V1Service nodeportSvc;
        List<Object> objs = new ArrayList<>();
        try {
            objs = Yaml.loadAll(masterTemplateYAML);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new MultichainNodeCreationResult(ex.getMessage(), false);
        }
        statefulSet = (V1StatefulSet) objs.get(0);
        headlessSvc = (V1Service) objs.get(1);
        nodeportSvc = (V1Service) objs.get(2);

        Yaml.addModelMap("v1", "Service", V1Service.class);
        CoreV1Api corev1 = new CoreV1Api();
        try {
            corev1.createNamespacedService(headlessSvc.getMetadata().getNamespace(), headlessSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }

        try {
            corev1.createNamespacedService(nodeportSvc.getMetadata().getNamespace(), nodeportSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }

        //Deployment and StatefulSet is defined in apps/v1, so should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        try {
            appv1.createNamespacedStatefulSet(statefulSet.getMetadata().getNamespace(), statefulSet, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new MultichainNodeCreationResult(ex.getResponseBody(), false);
        }

        return new MultichainNodeCreationResult("Master Node " + masterNodeName + " Creation successfully finished!", true);
    }

    public static void main(String[] args) {
        MasterNodeCreator.getInstance().createMasterLoadAll("testMasterNode", "testmaster-namespace", "testChain", "400", "350", "400", "350", "31000", "31001", "D:\\ideaIU-2019.2.win-workspace\\multichain-docker-kubernetes\\k8s\\multichain\\template\\k8s-multichain-master-template.yaml");
//        MasterNodeCreator.getInstance().createMasterLoadAs("testMasterNode", "testmaster-namespace", "testChain", "400", "350", "400", "350", "31000","31001","" , "" ,"");
    }
}
