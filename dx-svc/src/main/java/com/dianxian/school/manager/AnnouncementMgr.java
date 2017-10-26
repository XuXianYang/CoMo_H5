package com.dianxian.school.manager;

import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.domain.Announcement;
import com.dianxian.school.dto.AnnouncementDto;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.service.AnnouncementService;
import com.dianxian.school.service.ClassService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by XuWenHao on 5/27/2016.
 */
@Component
public class AnnouncementMgr {
    @Autowired
    ClassService classService;
    @Autowired
    AnnouncementService announcementService;

    public Announcement getAnnouncementDetail(Long id) {
        AnnouncementDto dto = announcementService.validateAnnouncementExists(id);
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(dto, announcement);
        return announcement;
    }

    public PagingInfo<Announcement> getAnnouncementsOfClass(Long classId, QueryPaging queryPaging) {
        ClassDto classDto = classService.validateClassExists(classId);
        Page<AnnouncementDto> announcementDtos = announcementService.getAnnouncementsOfClass(classDto.getSchoolId(), classDto.getEnrolYear(), classId, queryPaging);
        List<Announcement> resultDomains = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(announcementDtos)) {
            for (AnnouncementDto dto : announcementDtos) {
                Announcement announcement = new Announcement();
                BeanUtils.copyProperties(dto, announcement);
                resultDomains.add(announcement);
            }
        }
        return new PagingInfo<Announcement>(announcementDtos, resultDomains);
    }

}
