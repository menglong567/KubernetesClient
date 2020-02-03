package com.ml.kubernetes.client.controller;

import com.ml.kubernetes.client.model.multichain.MasterNodeParametersObj;
import com.ml.kubernetes.client.model.multichain.MultichainNodeCreationResult;
import com.ml.kubernetes.client.model.multichain.SlaveNodeParametersObj;
import com.ml.kubernetes.client.nodeCreator.multichain.MasterNodeCreator;
import com.ml.kubernetes.client.nodeCreator.multichain.SlaveNodeCreator;
import com.ml.kubernetes.client.util.GSonUtil;
import org.springframework.web.bind.annotation.*;

/**
 * @author luoliudan
 */
@RestController
public class MultichainNodeCreatorController {
    /**
     * @param masterNodeName
     * @param namespace
     * @param chainName
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param nodeportnetworkPort
     * @param nodeportrpcPort
     * @return
     */
    @RequestMapping(value = "/multichain/master/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createMasterNodeForm(@RequestParam(value = "masterNodeName", required = true) String masterNodeName,
                                       @RequestParam(value = "namespace", required = true) String namespace,
                                       @RequestParam(value = "chainName", required = true) String chainName,
                                       @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                       @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                       @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                       @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                       @RequestParam(value = "nodeportnetworkPort", required = true) String nodeportnetworkPort,
                                       @RequestParam(value = "nodeportrpcPort", required = true) String nodeportrpcPort) {
        MultichainNodeCreationResult result = MasterNodeCreator.getInstance().createMasterLoadAll(masterNodeName, namespace, chainName, memoryRequest, cpuRequest, memoryLimit, cpuLimit, nodeportnetworkPort, nodeportrpcPort, "D:\\ideaIU-2019.2.win-workspace\\multichain-docker-kubernetes\\k8s\\multichain\\template\\k8s-multichain-master-template.yaml");
        return GSonUtil.getInstance().object2Json(result);
    }

    /**
     * @param message
     * @return
     */
    @RequestMapping(value = "/multichain/master/create/json", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String createMasterNodeJson(@RequestBody MasterNodeParametersObj message) {
        MultichainNodeCreationResult result = MasterNodeCreator.getInstance().createMasterLoadAll(message.getMasterNodeName(), message.getNamespace(), message.getChainName(), message.getMemoryRequest(), message.getCpuRequest(), message.getMemoryLimit(), message.getCpuLimit(), message.getNodeportnetworkPort(), message.getNodeportrpcPort(), "D:\\ideaIU-2019.2.win-workspace\\multichain-docker-kubernetes\\k8s\\multichain\\template\\k8s-multichain-master-template.yaml");
        return GSonUtil.getInstance().object2Json(result);
    }


    /**
     * @param slaveNodeName
     * @param slaveNodeNamespace
     * @param chainName
     * @param masterNodeName
     * @param masterNamespace
     * @param memoryRequest
     * @param cpuRequest
     * @param memoryLimit
     * @param cpuLimit
     * @param nodeportnetworkPort
     * @param nodeportrpcPort
     * @return String
     */
    @RequestMapping(value = "/multichain/slave/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createSlaveNodeForm(@RequestParam(value = "slaveNodeName", required = true) String slaveNodeName,
                                      @RequestParam(value = "slaveNodeNamespace", required = true) String slaveNodeNamespace,
                                      @RequestParam(value = "chainName", required = true) String chainName,
                                      @RequestParam(value = "masterNodeName", required = true) String masterNodeName,
                                      @RequestParam(value = "masterNamespace", required = true) String masterNamespace,
                                      @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                      @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                      @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                      @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                      @RequestParam(value = "nodeportnetworkPort", required = true) String nodeportnetworkPort,
                                      @RequestParam(value = "nodeportrpcPort", required = true) String nodeportrpcPort) {
        MultichainNodeCreationResult result = SlaveNodeCreator.getInstance().createSlaveLoadAll(slaveNodeName, slaveNodeNamespace, chainName, masterNodeName, masterNamespace, memoryRequest, cpuRequest, memoryLimit, cpuLimit, nodeportnetworkPort, nodeportrpcPort, "D:\\ideaIU-2019.2.win-workspace\\multichain-docker-kubernetes\\k8s\\multichain\\template\\k8s-multichain-slave-template.yaml");
        return GSonUtil.getInstance().object2Json(result);
    }

    /**
     * @param message
     * @return
     */
    @RequestMapping(value = "/multichain/slave/create/json", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String createSlaveNodeJson(@RequestBody SlaveNodeParametersObj message) {
        MultichainNodeCreationResult result = SlaveNodeCreator.getInstance().createSlaveLoadAll(message.getSlaveNodeName(), message.getSlaveNodeNamespace(), message.getChainName(), message.getMasterNodeName(), message.getMasterNamespace(), message.getMemoryRequest(), message.getCpuRequest(), message.getMemoryLimit(), message.getCpuLimit(), message.getNodeportnetworkPort(), message.getNodeportrpcPort(), "D:\\ideaIU-2019.2.win-workspace\\multichain-docker-kubernetes\\k8s\\multichain\\template\\k8s-multichain-slave-template.yaml");
        return GSonUtil.getInstance().object2Json(result);
    }
}
