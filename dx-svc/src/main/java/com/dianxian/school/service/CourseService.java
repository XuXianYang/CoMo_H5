package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.spring.cache.Caches;
import com.dianxian.core.utils.time.DateUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.CourseDtoMapper;
import com.dianxian.school.dao.CourseMaterialDtoMapper;
import com.dianxian.school.dao.CourseScheduleDtoMapper;
import com.dianxian.school.dao.CourseTeacherDtoMapper;
import com.dianxian.school.domain.SetCourseScheduleItem;
import com.dianxian.school.dto.CourseDto;
import com.dianxian.school.dto.CourseMaterialDto;
import com.dianxian.school.dto.CourseScheduleDto;
import com.dianxian.school.dto.CourseTeacherDto;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by XuWenHao on 4/27/2016.
 */
@Component
public class CourseService {
    @Autowired
    CourseDtoMapper courseDtoMapper;
    @Autowired
    CourseTeacherDtoMapper courseTeacherDtoMapper;
    @Autowired
    CourseScheduleDtoMapper courseScheduleDtoMapper;
    @Autowired
    CourseMaterialDtoMapper courseMaterialDtoMapper;

    @Cacheable(value = {Caches.COURSE_CACHE}, keyGenerator = Caches.DEFAULT_KEY_GENERATOR)
    public List<CourseDto> getAll() {
        return courseDtoMapper.getAll();
    }

    @Cacheable(value = {Caches.COURSE_CACHE}, keyGenerator = Caches.DEFAULT_KEY_GENERATOR)
    public CourseDto validateCoursesExist(Long courseId) {
        CourseDto courseDto = courseDtoMapper.selectByPrimaryKey(courseId);
        if (null == courseDto) {
            throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS, "Invalid course: " + courseId);
        }
        return courseDto;
    }

    public void validateCoursesExist(List<Long> courseIds) {
        List<CourseDto> courseDtos = courseDtoMapper.getByIds(courseIds);
        int resultSize = 0;
        if (!CollectionUtils.isEmpty(courseDtos)) {
            resultSize = courseDtos.size();
        }
        // 查询条件和结果不一致，说明有记录不存在
        if (resultSize != courseIds.size()) {
            throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS, "Invalid course.");
        }
    }
    
    public CourseMaterialDto validateCourseMaterialExist(Long courseMeterialId) {
    	CourseMaterialDto courseMaterialDto = courseMaterialDtoMapper.selectByPrimaryKey(courseMeterialId);
        if (null == courseMaterialDto) {
            throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS, "Invalid course material: " + courseMeterialId);
        }
        return courseMaterialDto;
    }

    public void validateForAssignCourses(Long teacherId, Long classId, List<Long> courseIds) {
        int assignedCourses = courseTeacherDtoMapper.countAssignedCoursesByClass(classId, courseIds);
        if (0 < assignedCourses) {
            throw new BizLogicException(ServiceCodes.COURSE_ALREADY_ASSIGNED, "The course had been assigned to someone.");
        }
    }

    public void assginCourses(Long teacherId, Long classId, List<Long> courseIds) {
        List<CourseTeacherDto> dtos = Lists.newArrayList();
        for (Long courseId : courseIds) {
            CourseTeacherDto dto = new CourseTeacherDto();

            dto.setClassId(classId);
            dto.setTeacherId(teacherId);
            dto.setCourseId(courseId);
            dto.setCreatedBy(teacherId);
            dtos.add(dto);
        }
        courseTeacherDtoMapper.batchInsert(dtos);
    }

    public List<CourseTeacherDto> getAssignedCoursesOfClass(Long classId) {
        return courseTeacherDtoMapper.getAssignedCoursesOfClass(classId);
    }

    public List<CourseTeacherDto> getAssignedCoursesOfTeacherInClass(Long teacherId, Long classId) {
        return courseTeacherDtoMapper.getAssignedCourses(teacherId, classId);
    }

    public void validateForCreateCourseSchedule(List<SetCourseScheduleItem> params) {
        Map<Integer, Map<Integer, SetCourseScheduleItem>> scheduleMap = Maps.newHashMap();
        for (int i = 0; i < 7; i++) {
            scheduleMap.put(i, new HashMap<Integer, SetCourseScheduleItem>());
        }
        List<Long> courseIds = Lists.newArrayList();
        for (SetCourseScheduleItem item : params) {
            Map<Integer, SetCourseScheduleItem> daySchedule = scheduleMap.get(item.getDayOfWeek());
            if (daySchedule.containsKey(item.getLessonOfDay())) {
                throw new BizLogicException(ServiceCodes.COURSE_SCHEDULE_CONFLICTED
                        , String.format("Conflicted schedule at lesson %s on day %s.", item.getLessonOfDay(), item.getDayOfWeek()));
            }
            courseIds.add(item.getCourseId());
        }
        Set<Long> courseIdSet = new HashSet<Long>(courseIds);
        List<Long> distinctCourseIds = new ArrayList<Long>(courseIdSet);
        this.validateCoursesExist(distinctCourseIds);
    }

    public void setCourseSchedule(Long userId, Long classId, List<CourseScheduleDto> courseSchedules) {
        for (CourseScheduleDto dto : courseSchedules) {
            dto.setClassId(classId);
            dto.setCreatedBy(userId);
        }
        courseScheduleDtoMapper.deleteByClassId(classId);
        courseScheduleDtoMapper.batchInsert(courseSchedules);
    }

    public List<CourseScheduleDto> getCourseSchedule(Long classId) {
        return courseScheduleDtoMapper.getCourseScheduleByClass(classId);
    }

    public CourseMaterialDto addCourseMaterial(Long userId, CourseMaterialDto courseMaterialDto) {
        courseMaterialDto.setCreatedBy(userId);
        courseMaterialDtoMapper.insert(courseMaterialDto);
        return courseMaterialDtoMapper.selectByPrimaryKey(courseMaterialDto.getId());
    }

    public void updateCourseMaterial(Long userId, CourseMaterialDto courseMaterialDto) {
        courseMaterialDto.setUpdatedBy(userId);
        courseMaterialDtoMapper.updateByPrimaryKeySelective(courseMaterialDto);
    }
    
    public void deleteCourseMaterial(Long courseMaterialId) {
        courseMaterialDtoMapper.deleteByPrimaryKey(courseMaterialId);
    }

    public CourseMaterialDto getCourseMaterialById(Long id) {
        return courseMaterialDtoMapper.selectByPrimaryKey(id);
    }

    public Page<CourseMaterialDto> getCourseMaterialsOfClass(Long classId, Long courseId, Integer type, Date beginDate, Date endDate,  QueryPaging queryPaging) {
        Page<CourseMaterialDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());

        if (null != beginDate || null != endDate) {
            Calendar begin = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            if (null != beginDate) {
                begin.setTime(beginDate);
            } else {
                begin.set(Calendar.YEAR, 2016);
                begin.set(Calendar.MONTH, Calendar.JANUARY);
                begin.set(Calendar.DAY_OF_MONTH, 1);
            }
            begin = DateUtils.truncate(begin, Calendar.DATE);
            beginDate = begin.getTime();

            if (null != endDate) {
                end.setTime(endDate);
            }
            endDate = end.getTime();
        }
        courseMaterialDtoMapper.getCourseMaterialsOfClass(classId, courseId, type, beginDate, endDate);
        return page;
    }

    public Page<CourseMaterialDto> getCourseMaterialsOfTeacher(Long teacherUserId, QueryPaging queryPaging) {
        Page<CourseMaterialDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        courseMaterialDtoMapper.getCourseMaterialsOfTeacher(teacherUserId);
        return page;
    }
}
