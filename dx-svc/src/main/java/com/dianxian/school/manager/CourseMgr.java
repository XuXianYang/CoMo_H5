package com.dianxian.school.manager;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.lang.MapUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.*;
import com.dianxian.school.service.ClassService;
import com.dianxian.school.service.CourseService;
import com.dianxian.school.service.TeacherService;
import com.dianxian.storage.dto.ResourceFileDto;
import com.dianxian.storage.service.StorageService;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.TeacherDto;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.service.UserService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by XuWenHao on 4/29/2016.
 */
@Component
public class CourseMgr {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClassService classService;
    @Autowired
    CourseService courseService;
    @Autowired
    UserFacade userFacade;
    @Autowired
    StorageService storageService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    UserService userService;

    public List<CourseAssignment> getCourseAssignmentsOfClass(ClassDto classDto) {
        List<CourseDto> courseDtos = courseService.getAll();
        List<CourseAssignment> result = Lists.newArrayList();
        List<CourseTeacherDto> courseTeacherDtos = null;
        if (null != classDto) {
            courseTeacherDtos = courseService.getAssignedCoursesOfClass(classDto.getId());
        }
        if (CollectionUtils.isEmpty(courseTeacherDtos)) {
            for (CourseDto courseDto : courseDtos) {
                CourseAssignment courseAssignment = new CourseAssignment();
                courseAssignment.setCourseId(courseDto.getId());
                courseAssignment.setCourseName(courseDto.getName());
                result.add(courseAssignment);
            }
        } else {
            Map<Long, CourseTeacherDto> courseTeacherDtoMap = Maps.newHashMap();
            for (CourseTeacherDto dto : courseTeacherDtos) {
                courseTeacherDtoMap.put(dto.getCourseId(), dto);
            }
            for (CourseDto courseDto : courseDtos) {
                CourseAssignment courseAssignment = new CourseAssignment();
                courseAssignment.setCourseId(courseDto.getId());
                courseAssignment.setCourseName(courseDto.getName());
                CourseTeacherDto courseTeacherDto = courseTeacherDtoMap.get(courseDto.getId());
                if (null != courseTeacherDto) {
                    User user = userFacade.getUserByTeacherId(courseTeacherDto.getTeacherId());
                    courseAssignment.setTeacherId(courseTeacherDto.getTeacherId());
                    courseAssignment.setTeacherName(user.getRealName());
                    courseAssignment.setTeacherMobile(user.getMobileNo());
                }
                result.add(courseAssignment);
            }
        }
        return result;
    }

    /**
     * 查询一个老师所有的任课信息
     * @param teacherId
     * @return
     */
    public List<TeacherCourseAssignment> getCourseAssignmentsOfTeacher(Long teacherId) {
        List<CourseTeacherDto> courseTeacherDtos = teacherService.getCourseAssignmentsOfTeacher(teacherId);
        List<TeacherCourseAssignment> result = Lists.newArrayList();
        Map<Long, TeacherCourseAssignment> classCourseAssignmentMap = Maps.newLinkedHashMap();
        if (!CollectionUtils.isEmpty(courseTeacherDtos)) {
            for (CourseTeacherDto courseTeacherDto : courseTeacherDtos) {
                TeacherCourseAssignment domain = classCourseAssignmentMap.get(courseTeacherDto.getClassId());
                if (null == domain) {
                    domain = new TeacherCourseAssignment();
                    domain.setCourses(new ArrayList<Course>());

                    ClassDto classDto = classService.validateClassExists(courseTeacherDto.getClassId());
                    SimpleSchoolClass schoolClass = new SimpleSchoolClass();
                    schoolClass.setId(classDto.getId());
                    schoolClass.setName(classDto.getName());
                    domain.setClassInfo(schoolClass);

                    result.add(domain);
                    classCourseAssignmentMap.put(courseTeacherDto.getClassId(), domain);
                }
                CourseDto courseDto = courseService.validateCoursesExist(courseTeacherDto.getCourseId());
                Course course = convert(courseDto);
                domain.getCourses().add(course);
            }
        }
        return result;
    }

    public List<Course> getAssignedCoursesOfTeacherInClass(Long teacherId, Long classId) {
        List<CourseTeacherDto> courseTeacherDtos = courseService.getAssignedCoursesOfTeacherInClass(teacherId, classId);
        List<Course> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(courseTeacherDtos)) {
            List<CourseDto> courseDtos = courseService.getAll();
            Map<Long, CourseDto> courseDtoMap = MapUtils.toMap(courseDtos);
            for (CourseTeacherDto courseTeacherDto : courseTeacherDtos) {
                CourseDto courseDto = courseDtoMap.get(courseTeacherDto.getCourseId());
                if (null == courseDto) {
                    logger.warn("Course {} is not invalid in CourseTeacherDto {}."
                            , courseTeacherDto.getCourseId(), courseTeacherDto.getId());
                    continue;
                }
                result.add(convert(courseDto));
            }
        }
        return result;
    }

    public List<CourseSchedule> getCourseSchedule(Long classId) {
        classService.validateClassExists(classId);
        List<CourseScheduleDto> dtos = courseService.getCourseSchedule(classId);
        List<CourseSchedule> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(dtos)) {
            List<CourseDto> courseDtos = courseService.getAll();
            Map<Long, CourseDto> courseDtoMap = MapUtils.toMap(courseDtos);
            List<CourseTeacherDto> courseTeacherDtos = courseService.getAssignedCoursesOfClass(classId);
            Map<Long, CourseTeacherDto> courseTeacherDtoMap = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(courseTeacherDtos)) {
                for (CourseTeacherDto dto : courseTeacherDtos) {
                    courseTeacherDtoMap.put(dto.getCourseId(), dto);
                }
            }

            for (CourseScheduleDto dto : dtos) {
                CourseSchedule item = new CourseSchedule();
                result.add(item);

                item.setDayOfWeek(dto.getDayOfWeek());
                item.setLessonOfDay(dto.getLessonOfDay());

                CourseDto courseDto = courseDtoMap.get(dto.getCourseId());
                if (null == courseDto) {
                    throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS, "Invalid course: " + dto.getCourseId());
                }
                item.setCourse(convert(courseDto));

                CourseTeacherDto courseTeacherDto = courseTeacherDtoMap.get(dto.getCourseId());
                if (null != courseTeacherDto) {
                    SimpleTeacher teacher = teacherService.getTeacherDomainByTeacherId(courseTeacherDto.getTeacherId());
                    item.setTeacher(teacher);
                }
            }
        }
        return result;
    }

    public List<Course> getAllCourses() {
        List<CourseDto> dtos = courseService.getAll();
        List<Course> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(dtos)) {
            for (CourseDto dto : dtos) {
                result.add(convert(dto));
            }
        }
        return result;
    }

    public Course convert(CourseDto courseDto) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDto, course);
        return course;
    }

    public PagingInfo<CourseMaterial> getCourseMaterials(Long classId, Long courseId, Integer type, Date beginDate, Date endDate, QueryPaging queryPaging) {
        Page<CourseMaterialDto> courseMaterialDtos = courseService.getCourseMaterialsOfClass(classId, courseId, type, beginDate, endDate, queryPaging);
        List<CourseMaterial> resultDomains = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(courseMaterialDtos)) {
            for (CourseMaterialDto dto : courseMaterialDtos) {
                CourseMaterial courseMaterial = convert(dto);
                resultDomains.add(courseMaterial);
            }
        }
        return new PagingInfo<CourseMaterial>(courseMaterialDtos, resultDomains);
    }

    private CourseMaterial convert(CourseMaterialDto dto) {
        CourseMaterial courseMaterial = new CourseMaterial();
        BeanUtils.copyProperties(dto, courseMaterial);

        if (null != dto.getResourceFileId()) {
            courseMaterial.setAttachment(getResourceFileById(dto.getResourceFileId()));
        }

        CourseDto courseDto = courseService.validateCoursesExist(dto.getCourseId());
        courseMaterial.setCourse(convert(courseDto));

        TeacherDto teacherDto = userService.validateIsTeacher(dto.getCreatedBy());
        SimpleTeacher teacher = teacherService.getTeacherDomainByTeacherId(teacherDto.getId());
        courseMaterial.setTeacher(teacher);
        return courseMaterial;
    }

    public CourseMaterialManagementDetailInfo getCourseMaterialDetail(Long id) {
        CourseMaterialDto dto = courseService.getCourseMaterialById(id);
        if (null == dto) {
            throw new BizLogicException(ServiceCodes.COURSE_MATERIAL_NOT_EXISTS, String.format("Course material %s does not exist.", id));
        }
        return convertToDetailInfo(dto);
    }

    private CourseMaterialManagementDetailInfo convertToDetailInfo(CourseMaterialDto dto) {
        CourseMaterialManagementDetailInfo detailInfo = new CourseMaterialManagementDetailInfo();
        BeanUtils.copyProperties(dto, detailInfo);
        if (null != dto.getResourceFileId()) {
            detailInfo.setAttachment(getResourceFileById(dto.getResourceFileId()));
        }
        return detailInfo;
    }

    private ResourceFile getResourceFileById(Long id) {
        ResourceFile resourceFile = null;
        ResourceFileDto resourceFileDto = storageService.getResourceById(id);
        if (null != resourceFileDto) {
            resourceFile = new ResourceFile();

            resourceFile.setId(resourceFileDto.getId());
            resourceFile.setName(resourceFileDto.getName());

            UriBuilder uriBuilder = UriBuilder.fromUri(resourceFileDto.getUrlPrefix());
            uriBuilder.path(resourceFileDto.getRelativePath());
            resourceFile.setUrl(uriBuilder.build().toString());
        }
        return resourceFile;
    }

    public PagingInfo<CourseMaterialManagementListItem> getCourseMaterialList(Long userId, QueryPaging paging) {
        List<CourseMaterialManagementListItem> resultDomains = Lists.newArrayList();
        Page<CourseMaterialDto> courseMaterialDtos = courseService.getCourseMaterialsOfTeacher(userId, paging);
        if (!CollectionUtils.isEmpty(courseMaterialDtos)) {
            for (CourseMaterialDto dto : courseMaterialDtos) {
                CourseMaterialManagementListItem item = new CourseMaterialManagementListItem();
                BeanUtils.copyProperties(dto, item);

                ClassDto classDto = classService.validateClassExists(dto.getClassId());
                ClassInfo classInfo = new ClassInfo();
                classInfo.setClassId(classDto.getId());
                classInfo.setClassName(classDto.getName());
                item.setClassInfo(classInfo);

                resultDomains.add(item);
            }
        }
        return new PagingInfo<CourseMaterialManagementListItem>(courseMaterialDtos, resultDomains);
    }

}
