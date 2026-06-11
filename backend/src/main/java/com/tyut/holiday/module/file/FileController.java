package com.tyut.holiday.module.file;

import com.tyut.holiday.common.BizException;
import com.tyut.holiday.common.Result;
import com.tyut.holiday.config.AppProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传接口：图片本地磁盘存储，返回可访问 URL。
 */
@Tag(name = "文件", description = "图片上传")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private static final Set<String> ALLOWED_EXT = Set.of("png", "jpg", "jpeg");

    private final AppProperties appProperties;

    @Operation(summary = "上传图片（png/jpg，≤30MB）")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw BizException.badRequest("文件为空");
        }
        if (file.getSize() > appProperties.getUpload().getMaxSize()) {
            throw BizException.badRequest("文件超过 30MB 限制");
        }
        String original = file.getOriginalFilename();
        String ext = ext(original);
        if (!ALLOWED_EXT.contains(ext)) {
            throw BizException.badRequest("仅支持 png/jpg 图片");
        }
        // 目录：uploads/yyyyMM/uuid.ext —— 不依赖时间分桶也可，这里用扁平 uuid
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path dir = Paths.get(appProperties.getUpload().getDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(fileName);
            file.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BizException("文件保存失败：" + e.getMessage());
        }
        String url = appProperties.getUpload().getUrlPrefix() + "/" + fileName;
        return Result.ok(Map.of("url", url, "name", original == null ? fileName : original));
    }

    private String ext(String name) {
        if (name == null || !name.contains(".")) {
            return "";
        }
        return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
    }
}
