package com.dianxian.school.dao;

import com.dianxian.school.dto.AnnouncementDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AnnouncementDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(AnnouncementDto record);

    int insertSelective(AnnouncementDto record);

    AnnouncementDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AnnouncementDto record);

    int updateByPrimaryKeyWithBLOBs(AnnouncementDto record);

    int updateByPrimaryKey(AnnouncementDto record);

    /**
     * 获取一个班级的学生，所能看到的公告
     * @return
     */
    List<AnnouncementDto> getAnnouncementsOfClass(@Param("schoolId")Long schoolId, @Param("enrolYear")Integer enrolYear, @Param("classId")Long classId);
}