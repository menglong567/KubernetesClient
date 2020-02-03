package com.ml.kubernetes.client.model.multichain;

/**
 * @author luoliudan
 */
public class SlaveNodeParametersObj {
    private String slaveNodeName;
    private String slaveNodeNamespace;
    private String chainName;
    private String masterNodeName;
    private String masterNamespace;
    private String memoryRequest;
    private String cpuRequest;
    private String memoryLimit;
    private String cpuLimit;
    private String nodeportnetworkPort;
    private String nodeportrpcPort;

    public String getSlaveNodeName() {
        return slaveNodeName;
    }

    public void setSlaveNodeName(String slaveNodeName) {
        this.slaveNodeName = slaveNodeName;
    }

    public String getSlaveNodeNamespace() {
        return slaveNodeNamespace;
    }

    public void setSlaveNodeNamespace(String slaveNodeNamespace) {
        this.slaveNodeNamespace = slaveNodeNamespace;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public String getMasterNodeName() {
        return masterNodeName;
    }

    public void setMasterNodeName(String masterNodeName) {
        this.masterNodeName = masterNodeName;
    }

    public String getMasterNamespace() {
        return masterNamespace;
    }

    public void setMasterNamespace(String masterNamespace) {
        this.masterNamespace = masterNamespace;
    }

    public String getMemoryRequest() {
        return memoryRequest;
    }

    public void setMemoryRequest(String memoryRequest) {
        this.memoryRequest = memoryRequest;
    }

    public String getCpuRequest() {
        return cpuRequest;
    }

    public void setCpuRequest(String cpuRequest) {
        this.cpuRequest = cpuRequest;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public String getCpuLimit() {
        return cpuLimit;
    }

    public void setCpuLimit(String cpuLimit) {
        this.cpuLimit = cpuLimit;
    }

    public String getNodeportnetworkPort() {
        return nodeportnetworkPort;
    }

    public void setNodeportnetworkPort(String nodeportnetworkPort) {
        this.nodeportnetworkPort = nodeportnetworkPort;
    }

    public String getNodeportrpcPort() {
        return nodeportrpcPort;
    }

    public void setNodeportrpcPort(String nodeportrpcPort) {
        this.nodeportrpcPort = nodeportrpcPort;
    }

    @Override
    public String toString() {
        return "SlaveNodeParametersObj{" +
                "slaveNodeName='" + slaveNodeName + '\'' +
                ", slaveNodeNamespace='" + slaveNodeNamespace + '\'' +
                ", chainName='" + chainName + '\'' +
                ", masterNodeName='" + masterNodeName + '\'' +
                ", masterNamespace='" + masterNamespace + '\'' +
                ", memoryRequest='" + memoryRequest + '\'' +
                ", cpuRequest='" + cpuRequest + '\'' +
                ", memoryLimit='" + memoryLimit + '\'' +
                ", cpuLimit='" + cpuLimit + '\'' +
                ", nodeportnetworkPort='" + nodeportnetworkPort + '\'' +
                ", nodeportrpcPort='" + nodeportrpcPort + '\'' +
                '}';
    }
}
