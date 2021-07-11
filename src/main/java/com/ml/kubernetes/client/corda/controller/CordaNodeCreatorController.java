package com.ml.kubernetes.client.corda.controller;

import com.ml.kubernetes.client.corda.model.CordaNodeCreationResult;
import com.ml.kubernetes.client.corda.nodeCreator.CordaNodeCreator;
import com.ml.kubernetes.util.GSonUtil;
import org.springframework.web.bind.annotation.*;

/**
 * @author luoliudan
 */
@RestController
public class CordaNodeCreatorController {
    @RequestMapping(value = "/corda/networkmapservice/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createNetworkMapNodeForm(@RequestParam(value = "networkMapServiceNodeName", required = true) String networkMapServiceNodeName,
                                           @RequestParam(value = "networkMapServiceNamespace", required = true) String networkMapServiceNamespace,
                                           @RequestParam(value = "networkMapServiceNodeport", required = true) String networkMapServiceNodeport,
                                           @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                           @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                           @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                           @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                           @RequestParam(value = "cordaNetworkMapServiceTemplate", required = true) String cordaNetworkMapServiceTemplate) {
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
                                       @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                       @RequestParam(value = "cordaNotaryTemplate", required = true) String cordaNotaryTemplate) {
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
                                      @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                      @RequestParam(value = "cordaPartyNodeTemplate", required = true) String cordaPartyNodeTemplate) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyLoadAll(networkMapServiceHost, networkMapServicePort, myLegalName, NodeName, NodeNamespace, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaPartyNodeTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/party/create/formv2", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createPartyNodeFormV2(@RequestParam(value = "networkMapServiceHost", required = true) String networkMapServiceHost,
                                         @RequestParam(value = "networkMapServicePort", required = true) String networkMapServicePort,
                                         @RequestParam(value = "myLegalName", required = true) String myLegalName,
                                         @RequestParam(value = "NodeName", required = true) String NodeName,
                                         @RequestParam(value = "NodeNamespace", required = true) String NodeNamespace,
                                         @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                         @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                         @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                         @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                         @RequestParam(value = "downloadURL", required = true) String downloadURL,
                                         @RequestParam(value = "cordapps", required = true) String cordapps,
                                         @RequestParam(value = "namespace", required = true) String namespace,
                                         @RequestParam(value = "user", required = true) String user,
                                         @RequestParam(value = "cordaPartyNodeTemplate", required = true) String cordaPartyNodeTemplate) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyLoadAllv2(networkMapServiceHost, networkMapServicePort, myLegalName, NodeName, NodeNamespace, memoryRequest, cpuRequest, memoryLimit, cpuLimit, downloadURL, cordapps, namespace, user, cordaPartyNodeTemplate);
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
            @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
            @RequestParam(value = "cordaNodePostgresTemplate", required = true) String cordaNodePostgresTemplate) {
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
                                           @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                           @RequestParam(value = "cordaPartyWebserverTemplate", required = true) String cordaPartyWebserverTemplate) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createPartyWebserverLoadAll(NodeName, NodeNamespace, WebserverNodeport, PartyRPCPort, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaPartyWebserverTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/cordaClient/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createCordaClientForm(@RequestParam(value = "NodeName", required = true) String NodeName,
                                        @RequestParam(value = "NodeNamespace", required = true) String NodeNamespace,
                                        @RequestParam(value = "cordaClientServerNodeport", required = true) String cordaClientServerNodeport,
                                        @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                        @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                        @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                        @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                        @RequestParam(value = "cordaPartyCordaClientTemplate", required = true) String cordaPartyCordaClientTemplate) {
        CordaNodeCreationResult result = CordaNodeCreator.getInstance().createCordaClientServerLoadAll(NodeNamespace, cordaClientServerNodeport, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordaPartyCordaClientTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }
}
