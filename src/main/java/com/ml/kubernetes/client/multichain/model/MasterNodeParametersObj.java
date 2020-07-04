package com.ml.kubernetes.client.multichain.model;

/**
 * @author luoliudan
 */
public class MasterNodeParametersObj {
    private String masterNodeName;
    private String namespace;
    private String chainName;
    private String memoryRequest;
    private String cpuRequest;
    private String memoryLimit;
    private String cpuLimit;
    private String nodeportnetworkPort;
    private String nodeportrpcPort;

    public String getMasterNodeName() {
        return masterNodeName;
    }

    public void setMasterNodeName(String masterNodeName) {
        this.masterNodeName = masterNodeName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
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
        return "MasterNodeParametersObj{" +
                "masterNodeName='" + masterNodeName + '\'' +
                ", namespace='" + namespace + '\'' +
                ", chainName='" + chainName + '\'' +
                ", memoryRequest='" + memoryRequest + '\'' +
                ", cpuRequest='" + cpuRequest + '\'' +
                ", memoryLimit='" + memoryLimit + '\'' +
                ", cpuLimit='" + cpuLimit + '\'' +
                ", nodeportnetworkPort='" + nodeportnetworkPort + '\'' +
                ", nodeportrpcPort='" + nodeportrpcPort + '\'' +
                '}';
    }
}
