package com.ml.kubernetes.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.text.StringSubstitutor;

/**
 *
 * @author Liudan_Luo
 */
public class PlaceHolderUtil {
    private static final PlaceHolderUtil instance = new PlaceHolderUtil();
    private static final Map valuesMap = new HashMap();

    public static PlaceHolderUtil getInstance() {
        return instance;
    }

    private PlaceHolderUtil() {
    }

    public Map getVlauesMap() {
        return valuesMap;
    }

    public void clearValues() {
        valuesMap.clear();
    }

    public void addValues(String key, String value) {
        valuesMap.put(key, value);
    }

    public void delValues(String key) {
        valuesMap.remove(key);
    }

    public void replaceValue(String key, String value) {
        valuesMap.replace(key, value);
    }

    public void testMaster() {
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("masterNodeName", "master");
        PlaceHolderUtil.getInstance().addValues("namespace", "testNamespace");
        PlaceHolderUtil.getInstance().addValues("chainName", "demoChain");
        PlaceHolderUtil.getInstance().addValues("memoryRequest", "400");
        PlaceHolderUtil.getInstance().addValues("cpuRequest", "300");
        PlaceHolderUtil.getInstance().addValues("memoryLimit", "400");
        PlaceHolderUtil.getInstance().addValues("cpuLimit", "300");
        String templateString = FileReaderUtil.getInstance().readFileByLines("D:\\netbeans11-workspace\\multichain-docker-kubernetes\\k8s\\template\\k8s-multichain-headless-template.yaml");
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        String resolvedString = sub.replace(templateString);
        System.out.println(resolvedString);
    }

    public void testSlave() {
        PlaceHolderUtil.getInstance().clearValues();
        PlaceHolderUtil.getInstance().addValues("slaveNodeName", "citi-slave-1");
        PlaceHolderUtil.getInstance().addValues("slaveNodeNamespace", "citi-ns");
        PlaceHolderUtil.getInstance().addValues("chainName", "testChain");
        PlaceHolderUtil.getInstance().addValues("masterNodeName", "testMasterNode");
        PlaceHolderUtil.getInstance().addValues("masterNamespace", "testmaster-namespace");
        PlaceHolderUtil.getInstance().addValues("memoryRequest", "400");
        PlaceHolderUtil.getInstance().addValues("cpuRequest", "300");
        PlaceHolderUtil.getInstance().addValues("memoryLimit", "400");
        PlaceHolderUtil.getInstance().addValues("cpuLimit", "300");
        PlaceHolderUtil.getInstance().addValues("networkPort", "31002");
        PlaceHolderUtil.getInstance().addValues("rpcPort", "31003");
        String templateString = FileReaderUtil.getInstance().readFileByLines("D:\\netbeans11-workspace\\multichain-docker-kubernetes\\k8s\\template\\k8s-multichain-slave-template.yaml");
        StringSubstitutor sub = new StringSubstitutor(PlaceHolderUtil.getInstance().getVlauesMap());
        String resolvedString = sub.replace(templateString);
        System.out.println(resolvedString);
    }

    public static void main(String... args) {
        PlaceHolderUtil.instance.testSlave();
    }
}
