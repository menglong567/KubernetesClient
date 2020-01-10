package com.ml.kubernetes.nodeCreator;

import com.ml.kubernetes.client.KubernetesClientApiClient;
import com.ml.kubernetes.model.V1NamespaceCreateResult;
import com.ml.kubernetes.util.FileReaderUtil;
import com.ml.kubernetes.util.KubernetesClientNamespaceUtil;
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

/**
 *
 * @author Liudan_Luo
 */
public class SlaveNodeCreator {
    private static final SlaveNodeCreator instance = new SlaveNodeCreator();

    private SlaveNodeCreator() {
    }

    public static SlaveNodeCreator getInstance() {
        return instance;
    }

    /**
     *
     * @param slaveNodeName
     * @param slaveNodeNamespace
     * @param chainName
     * @param masterNodeName
     * @param masterNamespace
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param networkPort
     * @param rpcPort
     * @return
     */
    public boolean createSlaveLoadAll(String slaveNodeName, String slaveNodeNamespace, String chainName, String masterNodeName, String masterNamespace, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String networkPort, String rpcPort) {
        if (slaveNodeName == null || slaveNodeName.isEmpty()) {
            return false;
        }
        if (slaveNodeNamespace == null || slaveNodeNamespace.isEmpty()) {
            return false;
        }
        if (chainName == null || chainName.isEmpty()) {
            return false;
        }
        if (masterNodeName == null || masterNodeName.isEmpty()) {
            return false;
        }
        if (masterNamespace == null || masterNamespace.isEmpty()) {
            return false;
        }

        if (memoryRequest == null || memoryRequest.isEmpty()) {
            return false;
        }
        if (cpuRequest == null || cpuRequest.isEmpty()) {
            return false;
        }
        if (memoryLimit == null || memoryLimit.isEmpty()) {
            return false;
        }
        if (cpuLimit == null || cpuLimit.isEmpty()) {
            return false;
        }
        if (networkPort == null || networkPort.isEmpty()) {
            return false;
        }
        if (rpcPort == null || rpcPort.isEmpty()) {
            return false;
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
        PlaceHolderUtil.getInstance().addValues("networkPort", networkPort);
        PlaceHolderUtil.getInstance().addValues("rpcPort", rpcPort);

        //check namespace , if not exist ,create it
        KubernetesClientApiClient.getInstance();
        if (!KubernetesClientNamespaceUtil.getInstance().queryNamespace(slaveNodeNamespace)) {
            V1NamespaceCreateResult result = KubernetesClientNamespaceUtil.getInstance().createNamespace(slaveNodeNamespace);
            if (!result.isResult()) {
                System.out.println("Namespace creation failed:" + slaveNodeNamespace);
                return false;
            }
        }

        //parse slave template
        String slaveTemplate = FileReaderUtil.getInstance().readFileByLines("D:\\netbeans11-workspace\\multichain-docker-kubernetes\\k8s\\template\\k8s-multichain-slave-template.yaml");
        if (slaveTemplate == null || slaveTemplate.isEmpty()) {
            return false;
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        slaveTemplate = sub.replace(slaveTemplate);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        V1Service nodeportSvc;
        List<Object> objs = new ArrayList<>();
        try {
            objs = Yaml.loadAll(slaveTemplate);
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        statefulSet = (V1StatefulSet) objs.get(0);
        headlessSvc = (V1Service) objs.get(1);
        nodeportSvc = (V1Service) objs.get(2);

        Yaml.addModelMap("v1", "Service", V1Service.class);
        CoreV1Api corev1 = new CoreV1Api();

        System.out.println(Yaml.dump(headlessSvc));
        try {
            corev1.createNamespacedService(headlessSvc.getMetadata().getNamespace(), headlessSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
            return false;
        }

        System.out.println(Yaml.dump(nodeportSvc));
        try {
            corev1.createNamespacedService(nodeportSvc.getMetadata().getNamespace(), nodeportSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
            return false;
        }

        //Deployment and StatefulSet is defined in apps/v1, so should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        System.out.println(Yaml.dump(statefulSet));
        try {
            appv1.createNamespacedStatefulSet(statefulSet.getMetadata().getNamespace(), statefulSet, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
       // SlaveNodeCreator.getInstance().createSlaveLoadAll("CITI-slave-1", "CITI-ns", "testChain", "testMasterNode", "testmaster-namespace", "400", "350", "400", "350", "31002", "31003");
        SlaveNodeCreator.getInstance().createSlaveLoadAll("UBS-slave-1", "UBS-ns", "testChain", "testMasterNode", "testmaster-namespace", "400", "350", "400", "350", "31004", "31005");
    }
}
