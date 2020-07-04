package com.ml.kubernetes.client.multichain.controller;

import com.ml.kubernetes.client.multichain.model.MasterNodeParametersObj;
import com.ml.kubernetes.client.multichain.model.MultichainNodeCreationResult;
import com.ml.kubernetes.client.multichain.model.SlaveNodeParametersObj;
import com.ml.kubernetes.client.multichain.nodeCreator.MasterNodeCreator;
import com.ml.kubernetes.client.multichain.nodeCreator.SlaveNodeCreator;
import com.ml.kubernetes.util.GSonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * @author luoliudan
 */
@RestController
public class MultichainNodeCreatorController {
    @Value("${multichain.master.template.file}")
    private String multichainMasterTemplate;
    @Value("${multichain.slave.template.file}")
    private String multichainSlaveTemplate;

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
        MultichainNodeCreationResult result = MasterNodeCreator.getInstance().createMasterLoadAll(masterNodeName, namespace, chainName, memoryRequest, cpuRequest, memoryLimit, cpuLimit, nodeportnetworkPort, nodeportrpcPort, multichainMasterTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    /**
     * @param message
     * @return
     */
    @RequestMapping(value = "/multichain/master/create/json", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String createMasterNodeJson(@RequestBody MasterNodeParametersObj message) {
        MultichainNodeCreationResult result = MasterNodeCreator.getInstance().createMasterLoadAll(message.getMasterNodeName(), message.getNamespace(), message.getChainName(), message.getMemoryRequest(), message.getCpuRequest(), message.getMemoryLimit(), message.getCpuLimit(), message.getNodeportnetworkPort(), message.getNodeportrpcPort(), multichainMasterTemplate);
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
        MultichainNodeCreationResult result = SlaveNodeCreator.getInstance().createSlaveLoadAll(slaveNodeName, slaveNodeNamespace, chainName, masterNodeName, masterNamespace, memoryRequest, cpuRequest, memoryLimit, cpuLimit, nodeportnetworkPort, nodeportrpcPort, multichainSlaveTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    /**
     * @param message
     * @return
     */
    @RequestMapping(value = "/multichain/slave/create/json", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public String createSlaveNodeJson(@RequestBody SlaveNodeParametersObj message) {
        MultichainNodeCreationResult result = SlaveNodeCreator.getInstance().createSlaveLoadAll(message.getSlaveNodeName(), message.getSlaveNodeNamespace(), message.getChainName(), message.getMasterNodeName(), message.getMasterNamespace(), message.getMemoryRequest(), message.getCpuRequest(), message.getMemoryLimit(), message.getCpuLimit(), message.getNodeportnetworkPort(), message.getNodeportrpcPort(), multichainSlaveTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }
}
