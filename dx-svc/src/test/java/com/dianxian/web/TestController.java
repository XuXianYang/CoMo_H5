package com.dianxian.web;

import com.dianxian.storage.dao.ResourceFileDtoMapper;
import com.dianxian.storage.dto.ResourceFileDto;
import com.dianxian.storage.service.QiniuFileStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Created by xuwenhao on 2016/8/21.
 */
@Controller
@RequestMapping(value = "test")
public class TestController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    QiniuFileStorage qiniuFileStorage;
    @Autowired
    ResourceFileDtoMapper resourceFileDtoMapper;

    @ResponseBody
    @RequestMapping("/fileUpload")
    public Object fileUpload(@RequestParam("name") String name, @RequestParam("file")MultipartFile uploadFile) {
        logger.info(name);
        logger.info(uploadFile.getOriginalFilename());
        logger.info("" + uploadFile.getSize());
        logger.info(uploadFile.getName());

        ResourceFileDto fileDto = new ResourceFileDto();
        fileDto.setCreatedBy(1L);
        fileDto.setRelativePath(UUID.randomUUID().toString());
        fileDto.setBucket(qiniuFileStorage.getBucketName());
        fileDto.setUrlPrefix(qiniuFileStorage.getBucketURL());
        fileDto.setName(uploadFile.getOriginalFilename());
        fileDto.setCategory("TEST");
        resourceFileDtoMapper.insert(fileDto);

        return "hello world!";
    }
}
