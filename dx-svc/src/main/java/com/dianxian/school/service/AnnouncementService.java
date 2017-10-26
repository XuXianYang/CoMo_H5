package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.AnnouncementDtoMapper;
import com.dianxian.school.dto.AnnouncementDto;
import com.dianxian.school.dto.AnnouncementType;
import com.dianxian.school.request.teacher.CreateAnnouncementRequest;
import com.dianxian.school.request.teacher.UpdateAnnouncementRequest;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by XuWenHao on 5/26/2016.
 */
@Component
public class AnnouncementService {
    @Autowired
    AnnouncementDtoMapper announcementDtoMapper;

    public Long create(Long operatorId, Long schoolId, CreateAnnouncementRequest request) {
        AnnouncementDto announcementDto = new AnnouncementDto();
        announcementDto.setSchoolId(schoolId);
        announcementDto.setCreatedBy(operatorId);
        setRequestParams(announcementDto, request);
        announcementDtoMapper.insert(announcementDto);
        return announcementDto.getId();
    }

    public void update(Long operatorId, Long schoolId, UpdateAnnouncementRequest request) {
        AnnouncementDto announcementDto = new AnnouncementDto();
        announcementDto.setId(request.getId());
        announcementDto.setSchoolId(schoolId);
        announcementDto.setUpdatedBy(operatorId);
        announcementDto.setUpdatedAt(new Date());

        setRequestParams(announcementDto, request);
        announcementDtoMapper.updateByPrimaryKeySelective(announcementDto);
    }

    private void setRequestParams(AnnouncementDto announcementDto, CreateAnnouncementRequest request) {
        announcementDto.setEnrolYear(0);
        announcementDto.setClassId(0L);

        announcementDto.setType(request.getType());
        announcementDto.setTitle(request.getTitle());
        if (AnnouncementType.SCHOOL == request.getType()) {
            // nothing
        } else if (AnnouncementType.GRADE == request.getType()) {
            announcementDto.setEnrolYear(request.getEnrolYear());
        } else {
            announcementDto.setClassId(request.getClassId());
        }
        announcementDto.setContent(request.getContent());
    }

    public AnnouncementDto validateAnnouncementExists(Long id) {
        AnnouncementDto result = announcementDtoMapper.selectByPrimaryKey(id);
        if (null == result) {
            throw new BizLogicException(ServiceCodes.ANNOUNCEMENT_NOT_EXISTS, String.format("Announcement %s doesnot exist.", id));
        }
        return result;
    }

    public Page<AnnouncementDto> getAnnouncementsOfClass(Long schoolId, Integer enrolYear, Long classId, QueryPaging queryPaging) {
        Page<AnnouncementDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        announcementDtoMapper.getAnnouncementsOfClass(schoolId, enrolYear, classId);
        return page;
    }
}
