package com.ml.kubernetes.client.corda.nodeCreator;

import com.ml.kubernetes.ApiClient.CordaKubernetesClientApiClient;
import com.ml.kubernetes.client.corda.model.CordaNodeCreationResult;
import com.ml.kubernetes.result.V1NamespaceCreateResult;
import com.ml.kubernetes.util.*;
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
public class CordaNodeCreator {
    private static final CordaNodeCreator instance = new CordaNodeCreator();
    private static Logger LOGGER = LoggerFactory.getLogger(CordaNodeCreator.class);

    private CordaNodeCreator() {
    }

    public static CordaNodeCreator getInstance() {
        return instance;
    }

    /****
     * create network-map-service node
     * @param networkMapServiceNodeName
     * @param networkMapServiceNamespace
     * @param networkMapServiceNodeport
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param networkTemplate
     * @return
     */
    public CordaNodeCreationResult createNetworkMapServiceLoadAll(String networkMapServiceNodeName, String networkMapServiceNamespace, String networkMapServiceNodeport, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String networkTemplate) {
        if (networkMapServiceNodeName == null || networkMapServiceNodeName.isEmpty()) {
            LOGGER.error("networkMapServiceNodeName is null");
            return new CordaNodeCreationResult("networkMapServiceNodeName is null", false);
        }
        if (networkMapServiceNamespace == null || networkMapServiceNamespace.isEmpty()) {
            LOGGER.error("networkMapServiceNamespace is null");
            return new CordaNodeCreationResult("networkMapServiceNamespace is null", false);
        }
        if (networkMapServiceNodeport == null || networkMapServiceNodeport.isEmpty()) {
            LOGGER.error("networkMapServiceNodeport is null");
            return new CordaNodeCreationResult("networkMapServiceNodeport is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(networkMapServiceNodeport)) {
            LOGGER.error(networkMapServiceNodeport + " is not a valid number");
            return new CordaNodeCreationResult(networkMapServiceNodeport + " is not a valid number", false);
        }
        //verify the range of nodeportnetworkPort
        if (Integer.parseInt(networkMapServiceNodeport) < 30000 || Integer.parseInt(networkMapServiceNodeport) > 32767) {
            LOGGER.error(networkMapServiceNodeport + " is beyond the valid range <30000,32767>");
            return new CordaNodeCreationResult(networkMapServiceNodeport + " is beyond the valid range from 30000 to 32767", false);
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
        if (networkTemplate == null || networkTemplate.isEmpty()) {
            LOGGER.error("networkTemplate is null");
            return new CordaNodeCreationResult("networkTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("networkMapServiceNodeName", networkMapServiceNodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("networkMapServiceNamespace", networkMapServiceNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("networkMapServiceNodeport", networkMapServiceNodeport);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        CordaKubernetesClientApiClient.getInstance();
        if (!CordaKubernetesClientNamespaceUtil.getInstance().queryNamespace(networkMapServiceNamespace)) {
            V1NamespaceCreateResult result = CordaKubernetesClientNamespaceUtil.getInstance().createNamespace(networkMapServiceNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + networkMapServiceNamespace + " creation failed!");
                return new CordaNodeCreationResult("Namespace:" + networkMapServiceNamespace + " creation failed!", false);
            } else {
                LOGGER.error("Namespace:" + networkMapServiceNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + networkMapServiceNamespace + " already exists!");
        }


        //parse master template file content and replace with user input
        String networkTemplateYAML = FileReaderUtil.getInstance().readFileByLines(networkTemplate);
        if (networkTemplateYAML == null || networkTemplateYAML.isEmpty()) {
            LOGGER.error("Generated networkTemplateYAML is null!");
            return new CordaNodeCreationResult("Generated networkTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        networkTemplateYAML = sub.replace(networkTemplateYAML);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        V1Service nodeportSvc;
        List<Object> objs = new ArrayList<>();
        try {
            objs = Yaml.loadAll(networkTemplateYAML);
        } catch (IOException ex) {
            ex.printStackTrace();
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
        return new CordaNodeCreationResult("Network-map-service Node: " + networkMapServiceNodeName + " Creation successfully finished!", true);
    }

    /****
     * create notary node
     * @param networkMapServiceHost
     * @param networkMapServicePort
     * @param notaryNodeName
     * @param notaryNodeNamespace
     * @param notaryLegalName
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param notaryTemplate
     * @return
     */
    public CordaNodeCreationResult createNotaryLoadAll(String networkMapServiceHost, String networkMapServicePort, String notaryNodeName, String notaryNodeNamespace, String notaryLegalName, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String notaryTemplate) {
        if (networkMapServiceHost == null || networkMapServiceHost.isEmpty()) {
            LOGGER.error("networkMapServiceNodeName is null");
            return new CordaNodeCreationResult("networkMapServiceNodeName is null", false);
        }
        if (networkMapServicePort == null || networkMapServicePort.isEmpty()) {
            LOGGER.error("networkMapServicePort is null");
            return new CordaNodeCreationResult("networkMapServicePort is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(networkMapServicePort)) {
            LOGGER.error(networkMapServicePort + " is not a valid number");
            return new CordaNodeCreationResult(networkMapServicePort + " is not a valid number", false);
        }
        if (notaryNodeName == null || notaryNodeName.isEmpty()) {
            LOGGER.error("notaryNodeName is null");
            return new CordaNodeCreationResult("notaryNodeName is null", false);
        }
        if (notaryNodeNamespace == null || notaryNodeNamespace.isEmpty()) {
            LOGGER.error("notaryNodeNamespace is null");
            return new CordaNodeCreationResult("notaryNodeNamespace is null", false);
        }
        if (notaryLegalName == null || notaryLegalName.isEmpty()) {//here should another logic to check the x.509 name
            LOGGER.error("notaryLegalName is null");
            return new CordaNodeCreationResult("notaryLegalName is null", false);
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
        if (notaryTemplate == null || notaryTemplate.isEmpty()) {
            LOGGER.error("notaryTemplate is null");
            return new CordaNodeCreationResult("notaryTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("networkMapServiceHost", networkMapServiceHost);
        PlaceHolderUtil.getInstance().addValues("notaryNodeNamespace", notaryNodeNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("notaryNodeName", notaryNodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("networkMapServicePort", networkMapServicePort);
        PlaceHolderUtil.getInstance().addValues("notaryLegalName", notaryLegalName);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        CordaKubernetesClientApiClient.getInstance();
        if (!CordaKubernetesClientNamespaceUtil.getInstance().queryNamespace(notaryNodeNamespace)) {
            V1NamespaceCreateResult result = CordaKubernetesClientNamespaceUtil.getInstance().createNamespace(notaryNodeNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + notaryNodeNamespace + " creation failed!");
                return new CordaNodeCreationResult("Namespace:" + notaryNodeNamespace + " creation failed!", false);
            } else {
                LOGGER.error("Namespace:" + notaryNodeNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + notaryNodeNamespace + " already exists!");
        }

        //parse master template file content and replace with user input
        String notaryTemplateYAML = FileReaderUtil.getInstance().readFileByLines(notaryTemplate);
        if (notaryTemplateYAML == null || notaryTemplateYAML.isEmpty()) {
            LOGGER.error("Generated notaryTemplateYAML is null!");
            return new CordaNodeCreationResult("Generated notaryTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        notaryTemplateYAML = sub.replace(notaryTemplateYAML);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        List<Object> objs = new ArrayList<>();
        try {
            objs = Yaml.loadAll(notaryTemplateYAML);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new CordaNodeCreationResult(ex.getMessage(), false);
        }
        statefulSet = (V1StatefulSet) objs.get(0);
        headlessSvc = (V1Service) objs.get(1);

        CoreV1Api corev1 = new CoreV1Api();
        try {
            corev1.createNamespacedService(headlessSvc.getMetadata().getNamespace(), headlessSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("Headless service:" + headlessSvc.getMetadata().getName() + " created successfully!");

        //Deployment and StatefulSet is defined in apps/v1, so should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        try {
            appv1.createNamespacedStatefulSet(statefulSet.getMetadata().getNamespace(), statefulSet, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("StatefulSet:" + statefulSet.getMetadata().getName() + " created successfully!");
        return new CordaNodeCreationResult("Notary Node: " + notaryNodeName + " Creation successfully finished!", true);
    }

    /****
     * create party node
     * @param networkMapServiceHost
     * @param networkMapServicePort
     * @param myLegalName
     * @param NodeName
     * @param NodeNamespace
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param partyTemplate
     * @return
     */
    public CordaNodeCreationResult createPartyLoadAll(String networkMapServiceHost, String networkMapServicePort, String myLegalName, String NodeName, String NodeNamespace, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String partyTemplate) {
        if (networkMapServiceHost == null || networkMapServiceHost.isEmpty()) {
            LOGGER.error("networkMapServiceNodeName is null");
            return new CordaNodeCreationResult("networkMapServiceNodeName is null", false);
        }
        if (networkMapServicePort == null || networkMapServicePort.isEmpty()) {
            LOGGER.error("networkMapServicePort is null");
            return new CordaNodeCreationResult("networkMapServicePort is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(networkMapServicePort)) {
            LOGGER.error(networkMapServicePort + " is not a valid number");
            return new CordaNodeCreationResult(networkMapServicePort + " is not a valid number", false);
        }
        if (myLegalName == null || myLegalName.isEmpty()) {//here should another logic to check the x.509 name
            LOGGER.error("myLegalName is null");
            return new CordaNodeCreationResult("myLegalName is null", false);
        }
        if (NodeName == null || NodeName.isEmpty()) {
            LOGGER.error("NodeName is null");
            return new CordaNodeCreationResult("NodeName is null", false);
        }
        if (NodeNamespace == null || NodeNamespace.isEmpty()) {
            LOGGER.error("NodeNamespace is null");
            return new CordaNodeCreationResult("NodeNamespace is null", false);
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
        if (partyTemplate == null || partyTemplate.isEmpty()) {
            LOGGER.error("partyTemplate is null");
            return new CordaNodeCreationResult("partyTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("networkMapServiceHost", networkMapServiceHost);
        PlaceHolderUtil.getInstance().addValues("networkMapServicePort", networkMapServicePort);
        PlaceHolderUtil.getInstance().addValues("myLegalName", myLegalName);
        PlaceHolderUtil.getInstance().addValues("NodeName", NodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("NodeNamespace", NodeNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        CordaKubernetesClientApiClient.getInstance();
        if (!CordaKubernetesClientNamespaceUtil.getInstance().queryNamespace(NodeNamespace)) {
            V1NamespaceCreateResult result = CordaKubernetesClientNamespaceUtil.getInstance().createNamespace(NodeNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + NodeNamespace + " creation failed!");
                return new CordaNodeCreationResult("Namespace:" + NodeNamespace + " creation failed!", false);
            } else {
                LOGGER.error("Namespace:" + NodeNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + NodeNamespace + " already exists!");
        }

        //parse master template file content and replace with user input
        String partyTemplateYAML = FileReaderUtil.getInstance().readFileByLines(partyTemplate);
        if (partyTemplateYAML == null || partyTemplateYAML.isEmpty()) {
            LOGGER.error("Generated partyTemplateYAML is null!");
            return new CordaNodeCreationResult("Generated partyTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        partyTemplateYAML = sub.replace(partyTemplateYAML);

        //connect kubernetes and create resource
        V1StatefulSet statefulSet;
        V1Service headlessSvc;
        List<Object> objs = new ArrayList<>();
        try {
            objs = Yaml.loadAll(partyTemplateYAML);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new CordaNodeCreationResult(ex.getMessage(), false);
        }
        statefulSet = (V1StatefulSet) objs.get(0);
        headlessSvc = (V1Service) objs.get(1);

        CoreV1Api corev1 = new CoreV1Api();
        try {
            corev1.createNamespacedService(headlessSvc.getMetadata().getNamespace(), headlessSvc, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("Headless service:" + headlessSvc.getMetadata().getName() + " created successfully!");

        //Deployment and StatefulSet is defined in apps/v1, so should use AppsV1Api instead of CoreV1API
        AppsV1Api appv1 = new AppsV1Api();
        try {
            appv1.createNamespacedStatefulSet(statefulSet.getMetadata().getNamespace(), statefulSet, null, null, null);
        } catch (ApiException ex) {
            ex.printStackTrace();
            LOGGER.error(ex.getResponseBody());//this will help a lot when you fail
            return new CordaNodeCreationResult(ex.getResponseBody(), false);
        }
        LOGGER.info("StatefulSet:" + statefulSet.getMetadata().getName() + " created successfully!");
        return new CordaNodeCreationResult("Party Node: " + NodeName + " Creation successfully finished!", true);
    }

    /****
     *
     * @param NodeName
     * @param NodeNamespace
     * @param WebserverNodeport
     * @param PartyRPCPort
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param partyWebserverTemplate
     * @return
     */
    public CordaNodeCreationResult createPartyWebserverLoadAll(String NodeName, String NodeNamespace, String WebserverNodeport, String PartyRPCPort, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String partyWebserverTemplate) {
        if (NodeName == null || NodeName.isEmpty()) {
            LOGGER.error("NodeName is null");
            return new CordaNodeCreationResult("NodeName is null", false);
        }
        if (NodeNamespace == null || NodeNamespace.isEmpty()) {
            LOGGER.error("networkMapServicePort is null");
            return new CordaNodeCreationResult("networkMapServicePort is null", false);
        }
        if (WebserverNodeport == null || WebserverNodeport.isEmpty()) {//here should another logic to check the x.509 name
            LOGGER.error("WebserverNodeport is null");
            return new CordaNodeCreationResult("WebserverNodeport is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(WebserverNodeport)) {
            LOGGER.error(WebserverNodeport + " is not a valid number");
            return new CordaNodeCreationResult(WebserverNodeport + " is not a valid number", false);
        }
        //verify the range of WebserverNodeport
        if (Integer.parseInt(WebserverNodeport) < 30000 || Integer.parseInt(WebserverNodeport) > 32767) {
            LOGGER.error(WebserverNodeport + " is beyond the valid range <30000,32767>");
            return new CordaNodeCreationResult(WebserverNodeport + " is beyond the valid range from 30000 to 32767", false);
        }

        if (PartyRPCPort == null || PartyRPCPort.isEmpty()) {
            LOGGER.error("PartyRPCPort is null");
            return new CordaNodeCreationResult("PartyRPCPort is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(PartyRPCPort)) {
            LOGGER.error(PartyRPCPort + " is not a valid number");
            return new CordaNodeCreationResult(PartyRPCPort + " is not a valid number", false);
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
        if (partyWebserverTemplate == null || partyWebserverTemplate.isEmpty()) {
            LOGGER.error("partyWebserverTemplate is null");
            return new CordaNodeCreationResult("partyWebserverTemplate is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("NodeName", NodeName.toLowerCase());//a DNS-1035 label must consist of lower case alphanumeric characters or '-', start with an alphabetic character, and end with an alphanumeric character (e.g. 'my-name',  or 'abc-123', regex used for validation is '[a-z]([-a-z0-9]*[a-z0-9])?')
        PlaceHolderUtil.getInstance().addValues("NodeNamespace", NodeNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("WebserverNodeport", WebserverNodeport);
        PlaceHolderUtil.getInstance().addValues("PartyRPCPort", PartyRPCPort);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        CordaKubernetesClientApiClient.getInstance();
        if (!CordaKubernetesClientNamespaceUtil.getInstance().queryNamespace(NodeNamespace)) {
            V1NamespaceCreateResult result = CordaKubernetesClientNamespaceUtil.getInstance().createNamespace(NodeNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + NodeNamespace + " creation failed!");
                return new CordaNodeCreationResult("Namespace:" + NodeNamespace + " creation failed!", false);
            } else {
                LOGGER.error("Namespace:" + NodeNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + NodeNamespace + " already exists!");
        }

        //parse master template file content and replace with user input
        String partyWebserverTemplateYAML = FileReaderUtil.getInstance().readFileByLines(partyWebserverTemplate);
        if (partyWebserverTemplateYAML == null || partyWebserverTemplateYAML.isEmpty()) {
            LOGGER.error("Generated partyWebserverTemplateYAML is null!");
            return new CordaNodeCreationResult("Generated partyWebserverTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        partyWebserverTemplateYAML = sub.replace(partyWebserverTemplateYAML);

        //connect kubernetes and create resource
        V1Deployment v1Deployment;
        V1Service nodeport;
        List<Object> objs = new ArrayList<>();
        CoreV1Api corev1 = new CoreV1Api();

        try {
            objs = Yaml.loadAll(partyWebserverTemplateYAML);
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
        return new CordaNodeCreationResult("Webserver for party: " + NodeName + " Creation successfully finished!", true);
    }

    /****
     *
     * @param NodeNamespace
     * @param WebserverNodeport
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param partyWebserverv2Template
     * @return
     */
    public CordaNodeCreationResult createCordaClientServerLoadAll(String NodeNamespace, String WebserverNodeport, String memoryRequest, String cpuRequest, String memoryLimit, String cpuLimit, String partyWebserverv2Template) {
        if (NodeNamespace == null || NodeNamespace.isEmpty()) {
            LOGGER.error("networkMapServicePort is null");
            return new CordaNodeCreationResult("networkMapServicePort is null", false);
        }
        if (WebserverNodeport == null || WebserverNodeport.isEmpty()) {//here should another logic to check the x.509 name
            LOGGER.error("WebserverNodeport is null");
            return new CordaNodeCreationResult("WebserverNodeport is null", false);
        }
        if (!CommonUtil.getInstance().isInteger(WebserverNodeport)) {
            LOGGER.error(WebserverNodeport + " is not a valid number");
            return new CordaNodeCreationResult(WebserverNodeport + " is not a valid number", false);
        }
        //verify the range of WebserverNodeport
        if (Integer.parseInt(WebserverNodeport) < 30000 || Integer.parseInt(WebserverNodeport) > 32767) {
            LOGGER.error(WebserverNodeport + " is beyond the valid range <30000,32767>");
            return new CordaNodeCreationResult(WebserverNodeport + " is beyond the valid range from 30000 to 32767", false);
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
        if (partyWebserverv2Template == null || partyWebserverv2Template.isEmpty()) {
            LOGGER.error("partyWebserverv2Template is null");
            return new CordaNodeCreationResult("partyWebserverv2Template is null", false);
        }
        //fill the user input
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("NodeNamespace", NodeNamespace.toLowerCase());//a namespace is not allowed to have capital characters
        PlaceHolderUtil.getInstance().addValues("WebserverNodeport", WebserverNodeport);
        PlaceHolderUtil.getInstance().addValues("memoryRequest", memoryRequest);
        PlaceHolderUtil.getInstance().addValues("cpuRequest", cpuRequest);
        PlaceHolderUtil.getInstance().addValues("memoryLimit", memoryLimit);
        PlaceHolderUtil.getInstance().addValues("cpuLimit", cpuLimit);

        //check namespace , if not exist ,create it
        CordaKubernetesClientApiClient.getInstance();
        if (!CordaKubernetesClientNamespaceUtil.getInstance().queryNamespace(NodeNamespace)) {
            V1NamespaceCreateResult result = CordaKubernetesClientNamespaceUtil.getInstance().createNamespace(NodeNamespace);
            if (!result.isResult()) {
                LOGGER.error("Namespace " + NodeNamespace + " creation failed!");
                return new CordaNodeCreationResult("Namespace:" + NodeNamespace + " creation failed!", false);
            } else {
                LOGGER.error("Namespace:" + NodeNamespace + " creation succeeded!");
            }
        } else {
            LOGGER.info("Namespace:" + NodeNamespace + " already exists!");
        }

        //parse master template file content and replace with user input
        String partyWebserverv2TemplateYAML = FileReaderUtil.getInstance().readFileByLines(partyWebserverv2Template);
        if (partyWebserverv2TemplateYAML == null || partyWebserverv2TemplateYAML.isEmpty()) {
            LOGGER.error("Generated partyWebserverTemplateYAML is null!");
            return new CordaNodeCreationResult("Generated partyWebserverTemplateYAML is null", false);
        }
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        partyWebserverv2TemplateYAML = sub.replace(partyWebserverv2TemplateYAML);

        //connect kubernetes and create resource
        V1Deployment v1Deployment;
        V1Service nodeport;
        List<Object> objs = new ArrayList<>();
        CoreV1Api corev1 = new CoreV1Api();

        try {
            objs = Yaml.loadAll(partyWebserverv2TemplateYAML);
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
        return new CordaNodeCreationResult("Webserver-v2: " + " Creation successfully finished!", true);
    }

    public static void main(String[] args) {
//        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createNetworkMapServiceLoadAll("network-map-service", "corda-dynamic-zone-ns-test", "31080", "400", "350", "400", "350", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\network-map-service-template.yaml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));

//        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createNotaryLoadAll("http://network-map-service-0.network-map-service", "8080", "notary", "corda-dynamic-zone-ns-test", "O=Notary,L=London,C=GB", "400", "350", "400", "350", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\notary-node-template.yaml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));

        //O=PartyA,L=London,C=GB
//        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyLoadAll("http://network-map-service-0.network-map-service", "8080", "O=PartyA,L=London,C=GB", "partya", "corda-dynamic-zone-ns-test", "500", "400", "500", "400", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\party-node-template.yaml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));
//
//        //O=PartyB,L=New York,C=US
//        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyLoadAll("http://network-map-service-0.network-map-service", "8080", "O=PartyB,L=New York,C=US", "partyb", "corda-dynamic-zone-ns-test", "500", "400", "500", "400", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\party-node-template.yaml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));
//
//        //O=PartyC,L=Paris,C=FR
//        CordaNodeCreationResult result =CordaNodeCreator.getInstance().createPartyLoadAll("http://network-map-service-0.network-map-service", "8080", "O=PartyC,L=Paris,C=FR", "partyc", "corda-dynamic-zone-ns-test", "500", "400", "500", "400", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\party-node-template.yaml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));

        //partya 10005 31005
//        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyWebserverLoadAll("partya", "corda-dynamic-zone-ns-test","31005", "10005", "300", "300", "300", "300", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\party-webserver-k8s-template.yml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));

        //partyb 10009 31006
//        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyWebserverLoadAll("partyb", "corda-dynamic-zone-ns-test","31006", "10005", "300", "300", "300", "300", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\party-webserver-k8s-template.yml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));
//
//        //partya 10013 31007
//        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyWebserverLoadAll("partyc", "corda-dynamic-zone-ns-test","31007", "10005", "300", "300", "300", "300", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\party-webserver-k8s-template.yml");
//        LOGGER.info(GSonUtil.getInstance().object2Json(result));

        //webserver-v2
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createCordaClientServerLoadAll("corda-dynamic-zone-ns-test", "31008", "400", "400", "400", "400", "D:\\ideaIU-2019.2.win-workspace\\corda-docker-kubernetes\\docker-corda\\dynamic-compatibility-zone\\kubernetes-client-k8s-dynamic-compatibility-zone-template\\party-webserver-v2-k8s-template.yml");
        LOGGER.info(GSonUtil.getInstance().object2Json(result));
    }
}
