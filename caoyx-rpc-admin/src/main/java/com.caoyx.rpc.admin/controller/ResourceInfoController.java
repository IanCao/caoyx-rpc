package com.caoyx.rpc.admin.controller;

import com.caoyx.rpc.admin.data.ClassInfo;
import com.caoyx.rpc.admin.data.ProviderInfo;
import com.caoyx.rpc.admin.service.RegisterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-14 00:42
 */
@Controller
public class ResourceInfoController {

    @Resource
    private RegisterService registerService;

    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public String show(ModelMap map) {
        String providerName = registerService.getAllExportServices().get(0);
        ClassInfo classInfo = registerService.getClassInfosByServiceName(providerName).get(0);
        List<ProviderInfo> providerInfos = registerService.getProviderInfosByServiceNameAndClassInfo(providerName, classInfo);

        map.addAttribute("providerName", providerName);
        map.addAttribute("classInfo", classInfo);
        map.addAttribute("providerInfos", providerInfos);
        return "service";
    }
}