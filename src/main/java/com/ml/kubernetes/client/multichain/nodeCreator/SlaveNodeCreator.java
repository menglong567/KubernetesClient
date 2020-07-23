package com.ml.kubernetes.client.multichain.nodeCreator;

import com.ml.kubernetes.ApiClient.MultichainKubernetesClientApiClient;
import com.ml.kubernetes.result.V1NamespaceCreateResult;
import com.ml.kubernetes.client.multichain.model.MultichainNodeCreationResult;
import com.ml.kubernetes.util.CommonUtil;
import com.ml.kubernetes.util.FileReaderUtil;
import com.ml.kubernetes.util.MultichainKubernetesClientNamespaceUtil;
import com.ml.kubernetes.util.PlaceHolderUtil;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1StatefulSet;
import io.kubernetes.client.util.Yaml;

import java.io.IOException;
import java.util.List;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Liudan_Luo
 */
public class SlaveNodeCreator {
    private static final SlaveNodeCreator instance = new SlaveNodeCreator();
    private static Logger LOGGER = LoggerFactory.getLogger(SlaveNodeCreator.class);

    private SlaveNodeCreator() {
    }

    public static SlaveNodeCreator getInstance() {
        return instance;
    }

    /**
     * @param slaveNodeName       Name for slave node
     * @param slaveNodeNamespace  Namespace for slave node
     * @param chainName           Chain this slave node belongs to
     * @param masterNodeName      Master node this slave node to contact to pull configuration
     * @param masterNamespace     Namespace the master node belongs to
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param nodeportnetworkPort
     * @param nodeportrpcPort
     * @param slaveTemplateFile
     * @return MultichainNodeCreationResult
     */
    public MultichainNodeCreationResult createSlaveLoadAll(String slaveNodeName, String slaveNodeNamespace, String chainName, String masterNodeName, String masterNamespace, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String nodeportnetworkPort, String nodeportrpcPort, String slaveTemplateFile) {
        if (slaveNodeName == null || slaveNodeName.isEmpty()) {
            LOGGER.error("slaveNodeName is null");
            return new MultichainNodeCreationResult("slaveNodeName is null", false);
        }
        if (slaveNodeNamespace == null || slaveNodeNamespace.isEmpty()) {
            LOGGER.error("slaveNodeNamespace is null");
            return new MultichainNodeCreationResult("slaveNodeNamespace is null", false);
        }
        if (chainName == null || chainName.isEmpty()) {
            LOGGER.error("chainName is null");
            return new MultichainNodeCreationResult("chainName is null", false);
        }
        if (masterNodeName == null || masterNodeName.isEmpty()) {
            LOGGER.error("masterNodeName is null");
            return new MultichainNodeCreationResult("masterNodeName is null", false);
        }
        if (masterNamespace == null || masterNamespace.isEmpty()) {
            LOGGER.error("masterNamespace is null");
            return new MultichainNodeCreationResult("masterNamespace is null", false);
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

        if (slaveTemplateFile == null || slaveTemplateFile.isEmpty()) {
            LOGGER.error("slaveTemplateFile is null");
            return new MultichainNodeCreationResult("slaveTemplateFile is null", false);
        }

        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("slaveNodeName", slaveNodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("slaveNodeNamespace", slaveNodeNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("chainName", chainName);
        PlaceHolderUtil.getInstance().addValues("masterNodeName", masterNodeName.toLowerCase());
        PlaceHolderUtil.getInstance().addValues("masterNamespace", masterNamespace.toLowerCase());
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);
        PlaceHolderUtil.getInstance().addValues("nodeportnetworkPort", nodeportnetworkPort);
        PlaceHolderUtil.getInstance().addValues("nodeportrpcPort", nodeportrpcPort);

        //check namespace , if not exist ,create it
        MultichainKubernetesClientApiClient.getInstance();
        if (!MultichainKubernetesClientNamespaceUtil.getInstance().queryNamespace(slaveNodeNamespace)) {
            V1NamespaceCreateResult result = MultichainKubernetesClientNamespaceUtil.getInstance().createNamespace(slaveNodeNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + slaveNodeNamespace + " creation failed!");
                return new MultichainNodeCreationResult("Namespace " + slaveNodeNamespace + " creation failed!", false);
            }
        }

        //parse slave template file content and replace with user input
        String slaveTemplateTAML = FileReaderUtil.getInstance().readFileByLines(slaveTemplateFile);
        if (slaveTemplateTAML == null || slaveTemplateTAML.isEmpty()) {
            LOGGER.error("Genereted slaveTemplateTAML is null");
            return new MultichainNodeCreationResult("Genereted slaveTemplateTAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        slaveTemplateTAML = sub.replace(slaveTemplateTAML);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        V1Service nodeportSvc;
        List<Object> objs;
        try {
            objs = Yaml.loadAll(slaveTemplateTAML);
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
        return new MultichainNodeCreationResult("Slave Node " + slaveNodeName + " Creation successfully finished!", true);
    }

    public static void main(String[] args) {
//        SlaveNodeCreator.getInstance().createSlaveLoadAll("CITI-slave-1", "CITI-ns", "testChain", "testMasterNode", "testmaster-namespace", "400", "350", "400", "350", "31002", "31003", "D:\ideaIU-2019.2.win-workspace\multichain-docker-kubernetes\k8s\multichain\template\\k8s-multichain-slave-template.yaml");
        SlaveNodeCreator.getInstance().createSlaveLoadAll("UBS-slave-1", "UBS-ns", "testChain", "testMasterNode", "testmaster-namespace", "400", "350", "400", "350", "31004", "31005", "D:\\ideaIU-2019.2.win-workspace\\multichain-docker-kubernetes\\k8s\\multichain\\template\\k8s-multichain-slave-template.yaml");

//        SlaveNodeCreator.getInstance().createSlaveLoadAll("CITI-slave-2", "CITI-ns", "testChain", "testMasterNode", "testmaster-namespace", "400", "350", "400", "350", "31006", "31007", "D:\ideaIU-2019.2.win-workspace\multichain-docker-kubernetes\k8s\multichain\template\\k8s-multichain-slave-template.yaml");
//        SlaveNodeCreator.getInstance().createSlaveLoadAll("UBS-slave-2", "UBS-ns", "testChain", "testMasterNode", "testmaster-namespace", "400", "350", "400", "350", "31008", "31009", "D:\ideaIU-2019.2.win-workspace\multichain-docker-kubernetes\k8s\multichain\template\\k8s-multichain-slave-template.yaml");
    }
}
