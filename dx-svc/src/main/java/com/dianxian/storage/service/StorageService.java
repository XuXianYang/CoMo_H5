package com.dianxian.storage.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.storage.consts.StorageServiceCodes;
import com.dianxian.storage.dao.ResourceFileDtoMapper;
import com.dianxian.storage.dto.ResourceFileCategory;
import com.dianxian.storage.dto.ResourceFileDto;
import com.dianxian.user.consts.UserServiceCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by XuWenHao on 6/2/2016.
 */
@Component
public class StorageService {
    @Autowired
    StorageAppProperties storageAppProperties;
    @Autowired
    QiniuFileStorage qiniuFileStorage;

    @Autowired
    ResourceFileDtoMapper resourceFileDtoMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceFileDto save(Long userId, File file, String destFileName) {
        URL result = qiniuFileStorage.save(file, destFileName);
        if (null == result) {
            throw new BizLogicException(StorageServiceCodes.STORAGE_SYSTEM_ERROR, "Save file failed.");
        }
        ResourceFileDto fileDto = new ResourceFileDto();
        fileDto.setCreatedBy(userId);
        fileDto.setRelativePath(destFileName);
        resourceFileDtoMapper.insert(fileDto);
        return fileDto;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public ResourceFileDto save(Long userId, InputStream inputStream, String originFileName, String destFileName, String category) {
        URL result = qiniuFileStorage.save(inputStream, destFileName);
        if (null == result) {
            throw new BizLogicException(StorageServiceCodes.STORAGE_SYSTEM_ERROR, "Save file failed.");
        }
        ResourceFileDto fileDto = new ResourceFileDto();
        fileDto.setBucket(qiniuFileStorage.getBucketName());
        fileDto.setUrlPrefix(qiniuFileStorage.getBucketURL());
        fileDto.setCreatedBy(userId);
        fileDto.setRelativePath(destFileName);
        fileDto.setName(originFileName);
        fileDto.setCategory(category);
        resourceFileDtoMapper.insert(fileDto);
        return fileDto;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public ResourceFileDto getResourceById(Long id) {
        return resourceFileDtoMapper.selectByPrimaryKey(id);
    }

    public ResourceFileDto validateResource(Long resourceId, Long creatorId, String category) {
        ResourceFileDto resourceFileDto = this.getResourceById(resourceId);
        if (null == resourceFileDto) {
            throw new BizLogicException(StorageServiceCodes.RESOURCE_FILE_NOT_EXIST
                    , String.format("Resource file %s does not exist.", resourceId));
        }
        if (storageAppProperties.getCheckResourceFile()) {
            if (!creatorId.equals(resourceFileDto.getCreatedBy())) {
                throw new BizLogicException(StorageServiceCodes.RESOURCE_FILE_INVALID, "Invalid file.");
            }
            if (!StringUtils.equals(category, resourceFileDto.getCategory())) {
                throw new BizLogicException(StorageServiceCodes.RESOURCE_FILE_INVALID_CATEGORY
                        , String.format("Resource file %s is not %s.", resourceId, category));
            }
        }

        return resourceFileDto;
    }
}
