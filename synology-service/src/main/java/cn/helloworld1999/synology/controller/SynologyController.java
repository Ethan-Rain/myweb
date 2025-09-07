package cn.helloworld1999.synology.controller;

import cn.helloworld1999.synology.service.SynologyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/synology")
public class SynologyController {

    private final SynologyService synologyService;


    @GetMapping("/login")
    public Map<String, Object> loginDefault() {
        return synologyService.login(null, null);
    }

    @GetMapping("/login/custom")
    public Map<String, Object> loginCustom(@RequestParam String account,
                                           @RequestParam String passwd) {
        return synologyService.login(account, passwd);
    }
    @GetMapping("/filelist")
    public Map<String, Object> getFileList(@RequestParam String folderPath) {
        return synologyService.getFileList(folderPath);
    }
}
