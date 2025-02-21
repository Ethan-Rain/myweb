package cn.helloworld1999.myweb.controller;

import cn.helloworld1999.myweb.dto.ResponseResult;
import cn.helloworld1999.myweb.service.ImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.io.IOException;

@RestController
@RequestMapping("/images")
public class ImagesController {
    @Autowired
    private ImagesService imagesService;

    // 获取随机图片,且不携带任何信息
    @RequestMapping("/getRandomImage")
    public ResponseResult<String> getRandomImage() throws Exception {
        if (imagesService.getRandomImage() == null) {
            return ResponseResult.error(500, "获取图片失败");
        } else {
            return ResponseResult.success(imagesService.getRandomImage());
        }
    }

    @PostMapping("/scan")
    public ResponseResult<Integer> scanDirectory(
            @RequestParam @NotBlank String path) {
        try {
            int newRecords = imagesService.scanImages(path);
            return ResponseResult.success(newRecords);
        } catch (IOException e) {
            return ResponseResult.error(500, "扫描失败: " + e.getMessage());
        }
    }
}
