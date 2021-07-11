package com.ml.kubernetes.client.corda.controller;

import com.ml.kubernetes.client.corda.model.CordaNodeCreationResult;
import com.ml.kubernetes.client.corda.nodeCreator.CordappRepoServerCreator;
import com.ml.kubernetes.util.GSonUtil;
import org.springframework.web.bind.annotation.*;

/**
 * @author luoliudan
 */
@RestController
public class CordappRepoServerController {
    @RequestMapping(value = "/corda/cordapp-repo-server/create/form", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createCordappRepoServerForm(@RequestParam(value = "cordappRepoServerNamespace", required = true) String cordappRepoServerNamespace,
                                              @RequestParam(value = "cordappRepoServerNodeport", required = true) String cordappRepoServerNodeport,
                                              @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                              @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                              @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                              @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                              @RequestParam(value = "cordappRepoServerTemplate", required = true) String cordappRepoServerTemplate) {
        CordaNodeCreationResult result = CordappRepoServerCreator.getInstance().createCordappRepoServerLoadAll(cordappRepoServerNamespace, cordappRepoServerNodeport, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordappRepoServerTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }

    @RequestMapping(value = "/corda/cordapp-repo-server/create/formv2", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public String createCordappRepoServerFormv2(@RequestParam(value = "cordappRepoServerNamespace", required = true) String cordappRepoServerNamespace,
                                                @RequestParam(value = "cordappRepoServerNodeport", required = true) String cordappRepoServerNodeport,
                                                @RequestParam(value = "memoryRequest", required = true) String memoryRequest,
                                                @RequestParam(value = "cpuRequest", required = true) String cpuRequest,
                                                @RequestParam(value = "memoryLimit", required = true) String memoryLimit,
                                                @RequestParam(value = "cpuLimit", required = true) String cpuLimit,
                                                @RequestParam(value = "cordappRepoServerTemplate", required = true) String cordappRepoServerTemplate) {
        CordaNodeCreationResult result = CordappRepoServerCreator.getInstance().createCordappRepoServerLoadAllv2(cordappRepoServerNamespace, cordappRepoServerNodeport, memoryRequest, cpuRequest, memoryLimit, cpuLimit, cordappRepoServerTemplate);
        return GSonUtil.getInstance().object2Json(result);
    }
}
