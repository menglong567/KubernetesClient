package com.ml.kubernetes.client.corda.controller;

import com.ml.kubernetes.client.corda.model.CordaNodeCreationResult;
import com.ml.kubernetes.client.corda.nodeCreator.CordaNodeCreator;
import com.ml.kubernetes.util.GSonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author luoliudan
 */
@RestController
public class CordaNodeCreatorController {
    @Value("${corda.network.map.service.template.file}")
    private String cordaNetworkMapServiceTemplate;
    @Value("${corda.network.notary.service.template.file}")
    private String cordaNotaryTemplate;
    @Value("${corda.network.party.node.template.file}")
    private String cordaPartyNodeTemplate;
    @Value("${corda.network.party.webserver.node.template.file}")
    private String cordaPartyWebserverTemplate;
    @Value("${corda.network.party.webserverv2.node.template.file}")
    private String cordaPartyWebserverv2Template;
    @Value("${corda.network.node.postgres.template.file}")
    private String cordaNodePostgresTemplate;

    @RequestMapping(value = "/corda/networkmapservice/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createMasterNodeForm(@RequestParam(value = "networkMapServiceNodeName", required = true) String networkMapServiceNodeName,
                                       @RequestParam(value = "networkMapServiceNamespace", required = true) String networkMapServiceNamespace,
                                       @RequestParam(value = "networkMapServiceNodeport", required = true) String networkMapServiceNodeport,
                                       @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                       @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                       @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                       @RequestParam(value = "cpuLimit", required = true) String cpuLimit) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createNetworkMapServiceLoadAll(networkMapServiceNodeName, networkMapServiceNamespace, networkMapServiceNodeport, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaNetworkMapServiceTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/notary/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createNotaryNodeForm(@RequestParam(value = "networkMapServiceHost", required = true) String networkMapServiceHost,
                                       @RequestParam(value = "networkMapServicePort", required = true) String networkMapServicePort,
                                       @RequestParam(value = "notaryNodeName", required = true) String notaryNodeName,
                                       @RequestParam(value = "notaryNodeNamespace", required = true) String notaryNodeNamespace,
                                       @RequestParam(value = "notaryLegalName", required = true) String notaryLegalName,
                                       @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                       @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                       @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                       @RequestParam(value = "cpuLimit", required = true) String cpuLimit) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createNotaryLoadAll(networkMapServiceHost, networkMapServicePort, notaryNodeName, notaryNodeNamespace, notaryLegalName, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaNotaryTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/party/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createPartyNodeForm(@RequestParam(value = "networkMapServiceHost", required = true) String networkMapServiceHost,
                                      @RequestParam(value = "networkMapServicePort", required = true) String networkMapServicePort,
                                      @RequestParam(value = "myLegalName", required = true) String myLegalName,
                                      @RequestParam(value = "NodeName", required = true) String NodeName,
                                      @RequestParam(value = "NodeNamespace", required = true) String NodeNamespace,
                                      @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                      @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                      @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                      @RequestParam(value = "cpuLimit", required = true) String cpuLimit) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createNotaryLoadAll(networkMapServiceHost, networkMapServicePort, myLegalName, NodeName, NodeNamespace, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaPartyNodeTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/postgres/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createPartyNodePostgresForm(
            @RequestParam(value = "NodeName", required = true) String NodeName,
            @RequestParam(value = "NodeNamespace", required = true) String NodeNamespace,
            @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
            @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
            @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
            @RequestParam(value = "cpuLimit", required = true) String cpuLimit) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createNodePostgresLoadAll(NodeName, NodeNamespace, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaNodePostgresTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/partyWebserver/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createPartyWebserverForm(@RequestParam(value = "NodeName", required = true) String NodeName,
                                           @RequestParam(value = "NodeNamespace", required = true) String NodeNamespace,
                                           @RequestParam(value = "WebserverNodeport", required = true) String WebserverNodeport,
                                           @RequestParam(value = "PartyRPCPort", required = true) String PartyRPCPort,
                                           @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                           @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                           @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                           @RequestParam(value = "cpuLimit", required = true) String cpuLimit) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyWebserverLoadAll(NodeName, NodeNamespace, WebserverNodeport, PartyRPCPort, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaPartyWebserverTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/webserver/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createWebserverForm(@RequestParam(value = "NodeName", required = true) String NodeName,
                                      @RequestParam(value = "NodeNamespace", required = true) String NodeNamespace,
                                      @RequestParam(value = "WebserverNodeport", required = true) String WebserverNodeport,
                                      @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                      @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                      @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                      @RequestParam(value = "cpuLimit", required = true) String cpuLimit) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createCordaClientServerLoadAll(NodeNamespace, WebserverNodeport, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaPartyWebserverv2Template);
        return GSonUtil.getInstance().object2Json(result);
    }
}
