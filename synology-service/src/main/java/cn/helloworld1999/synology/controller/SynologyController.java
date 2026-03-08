package cn.helloworld1999.synology.controller;

import cn.helloworld1999.synology.api.SynologyAuthService;
import cn.helloworld1999.synology.api.SynologyReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/synology")
public class SynologyController {

    private final SynologyAuthService synologyAuthService;
    private  final SynologyReadService synologyReadService;

    @GetMapping("/login")
    public Map<String, Object> loginDefault() {
        return synologyAuthService.login(null, null);
    }

    @GetMapping("/login/custom")
    public Map<String, Object> loginCustom(@RequestParam String account,
                                           @RequestParam String passwd) {
        return synologyAuthService.login(account, passwd);
    }
    @GetMapping("/filelist")
    public Map<String, Object> getFileList(@RequestParam String folderPath) {
        return synologyReadService.getFileList(folderPath);
    }
}
