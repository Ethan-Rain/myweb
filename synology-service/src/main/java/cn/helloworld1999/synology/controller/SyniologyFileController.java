package cn.helloworld1999.synology.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/SynoiologyFile")
public class SyniologyFileController {
    @RequestMapping("/getFileList")
    public String getFileList() {
        return "getFileList";
    }
}
