package com.ml.kubernetes.util;

import com.google.gson.Gson;
import com.ml.kubernetes.client.multichain.model.MasterNodeParametersObj;
import com.ml.kubernetes.client.multichain.model.SlaveNodeParametersObj;

/**
 * @author luoliudan
 */
public class GSonUtil {
    private static final GSonUtil instance = new GSonUtil();
    private static final Gson gson = new Gson();

    private GSonUtil() {
    }

    public static GSonUtil getInstance() {
        return instance;
    }

    public String object2Json(Object obj) {
        return gson.toJson(obj);
    }

    public Object json2Object(String json, Class cla) {
        return gson.fromJson(json, cla);
    }

    public static void main(String[] args) {
        MasterNodeParametersObj obj = new MasterNodeParametersObj();
        obj.setMasterNodeName("demoMasterNode");
        obj.setChainName("demoChain");
        obj.setCpuLimit("400");
        obj.setCpuRequest("400");
        obj.setMemoryLimit("400");
        obj.setMemoryRequest("400");
        obj.setNamespace("demomaster-namespace");
        obj.setNodeportnetworkPort("31020");
        obj.setNodeportrpcPort("31021");
        String json = GSonUtil.getInstance().object2Json(obj);
        System.out.println(json);

        SlaveNodeParametersObj sobj = new SlaveNodeParametersObj();
        sobj.setChainName("demoChain");
        sobj.setCpuLimit("400");
        sobj.setCpuRequest("400");
        sobj.setMemoryRequest("400");
        sobj.setMemoryLimit("400");
        sobj.setMasterNodeName("demoMasterNode");
        sobj.setMasterNamespace("demomaster-namespace");
        sobj.setSlaveNodeNamespace("CITI-ns");
        sobj.setSlaveNodeName("CITI-slave-3");
        sobj.setNodeportnetworkPort("31010");
        sobj.setNodeportrpcPort("31011");
        json = GSonUtil.getInstance().object2Json(sobj);
        System.out.println(json);

        sobj.setChainName("demoChain");
        sobj.setCpuLimit("400");
        sobj.setCpuRequest("400");
        sobj.setMemoryRequest("400");
        sobj.setMemoryLimit("400");
        sobj.setMasterNodeName("demoMasterNode");
        sobj.setMasterNamespace("demomaster-namespace");
        sobj.setSlaveNodeNamespace("UBS-ns");
        sobj.setSlaveNodeName("UBS-slave-3");
        sobj.setNodeportnetworkPort("31012");
        sobj.setNodeportrpcPort("31013");
        json = GSonUtil.getInstance().object2Json(sobj);
        System.out.println(json);
    }
}
