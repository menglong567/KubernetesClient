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
public class MasterNodeCreator {
    private static final MasterNodeCreator instance = new MasterNodeCreator();

    private MasterNodeCreator() {
    }

    public static MasterNodeCreator getInstance() {
        return instance;
    }

    /**
     * **
     *
     * @param masterNodeName
     * @param namespace
     * @param chainName
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @return
     */
    public boolean createMasterLoadAs(String masterNodeName, String namespace, String chainName, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit) {
        if (masterNodeName == null || masterNodeName.isEmpty()) {
            return false;
        }
        if (namespace == null || namespace.isEmpty()) {
            return false;
        }
        if (chainName == null || chainName.isEmpty()) {
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
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("masterNodeName", masterNodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("namespace", namespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("chainName", chainName);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //connect kubernetes and check namespace resource
        KubernetesClientApiClient.getInstance();
        if (!KubernetesClientNamespaceUtil.getInstance().queryNamespace(namespace)) {
            V1NamespaceCreateResult result = KubernetesClientNamespaceUtil.getInstance().createNamespace(namespace);
            if (!result.isResult()) {
                System.out.println("Namespace creation failed!");
                return false;
            }
        }

        //parse statefulset resource
        String statefulset = FileReaderUtil.getInstance().readFileByLines("D:\\netbeans11-workspace\\multichain-docker-kubernetes\\k8s\\template\\k8s-multichain-statefulset-template.yaml");
        if (statefulset == null || statefulset.isEmpty()) {
            return false;
        }
        String headless = FileReaderUtil.getInstance().readFileByLines("D:\\netbeans11-workspace\\multichain-docker-kubernetes\\k8s\\template\\k8s-multichain-headless-template.yaml");
        if (headless == null || headless.isEmpty()) {
            return false;
        }

        String nodeport = FileReaderUtil.getInstance().readFileByLines("D:\\netbeans11-workspace\\multichain-docker-kubernetes\\k8s\\template\\k8s-multichain-nodeport-template.yaml");
        if (nodeport == null || nodeport.isEmpty()) {
            return false;
        }

        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        statefulset = sub.replace(statefulset);
        //Deployment and StatefulSet is defined in apps/v1, so you should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        V1StatefulSet statefulSet;
        try {
            statefulSet = Yaml.loadAs(statefulset, V1StatefulSet.class);
            appv1.createNamespacedStatefulSet(statefulSet.getMetadata().getNamespace(), statefulSet, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
            return false;
        }

        CoreV1Api corev1 = new CoreV1Api();
        Yaml.addModelMap("v1", "Service", V1Service.class);

        V1Service headlessSvc;
        sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        headless = sub.replace(headless);
        try {
            headlessSvc = Yaml.loadAs(headless, V1Service.class);
            corev1.createNamespacedService(headlessSvc.getMetadata().getNamespace(), headlessSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
            return false;
        }

        V1Service nodeportSvc;
        sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        nodeport = sub.replace(nodeport);
        try {
            nodeportSvc = Yaml.loadAs(nodeport, V1Service.class);
            corev1.createNamespacedService(nodeportSvc.getMetadata().getNamespace(), nodeportSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            System.out.println(ex.getResponseBody());//this will help a lot when you fail
            return false;
        }
        return true;
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
     * @return
     */
    public boolean createMasterLoadAll(String masterNodeName, String namespace, String chainName, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit) {
        if (masterNodeName == null || masterNodeName.isEmpty()) {
            return false;
        }
        if (namespace == null || namespace.isEmpty()) {
            return false;
        }
        if (chainName == null || chainName.isEmpty()) {
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
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("masterNodeName", masterNodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("namespace", namespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("chainName", chainName);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        KubernetesClientApiClient.getInstance();
        if (!KubernetesClientNamespaceUtil.getInstance().queryNamespace(namespace)) {
            V1NamespaceCreateResult result = KubernetesClientNamespaceUtil.getInstance().createNamespace(namespace);
            if (!result.isResult()) {
                System.out.println("Namespace creation failed!");
                return false;
            }
        }

        //parse master template
        String masterTemplate = FileReaderUtil.getInstance().readFileByLines("D:\\netbeans11-workspace\\multichain-docker-kubernetes\\k8s\\template\\k8s-multichain-master-template.yaml");
        if (masterTemplate == null || masterTemplate.isEmpty()) {
            return false;
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        masterTemplate = sub.replace(masterTemplate);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        V1Service nodeportSvc;
        List<Object> objs = new ArrayList<>();
        try {
            objs = Yaml.loadAll(masterTemplate);
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
        MasterNodeCreator.getInstance().createMasterLoadAll("testMasterNode", "testmaster-namespace", "testChain", "400", "350", "400", "350");
//        MasterNodeCreator.getInstance().createMasterLoadAs("testMasterNode", "testmaster-namespace", "testChain", "400", "350", "400", "350");
    }
}
