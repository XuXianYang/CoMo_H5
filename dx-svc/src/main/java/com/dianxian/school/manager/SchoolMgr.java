package com.dianxian.school.manager;

import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.im.dto.ImGroupType;
import com.dianxian.im.facade.IMFacade;
import com.dianxian.school.domain.SchoolClassBasicInfo;
import com.dianxian.school.domain.SchoolClassOfTeacher;
import com.dianxian.school.domain.SchoolInfo;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.school.request.sysadmin.CreateSchoolRequest;
import com.dianxian.school.request.sysadmin.UpdateSchoolRequest;
import com.dianxian.school.service.*;
import com.dianxian.user.domain.Permissions;
import com.dianxian.user.dto.TeacherDto;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.facade.bean.RegisterUserRequest;
import com.dianxian.user.service.PermissionService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * Created by xuwenhao on 2016/4/27.
 */
@Component
public class SchoolMgr {
    @Autowired
    UserFacade userFacade;
    @Autowired
    SchoolService schoolService;
    @Autowired
    ClassService classService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    TeacherRoleService teacherRoleService;
    @Autowired
    PermissionService permissionService;
    @Autowired
    IMFacade imFacade;
    @Autowired
    CourseService courseService;
    @Autowired
    DomainConverter domainConverter;

    public SchoolDto createSchool(Long userId, CreateSchoolRequest request) {
        permissionService.validatePermission(userId, Permissions.CREATE_SCHOOL);
        SchoolDto params = new SchoolDto();
        BeanUtils.copyProperties(request, params);
        params.setCreatedBy(userId);
        return schoolService.create(params);
    }

    public void updateSchool(Long userId, UpdateSchoolRequest request) {
        SchoolDto params = new SchoolDto();
        BeanUtils.copyProperties(request, params);
        schoolService.update(userId, params);
    }

    public Long createSchoolAdmin(Long operatorId, RegisterUserRequest request, Long schoolId) {
        permissionService.validatePermission(operatorId, Permissions.CREATE_SCHOOL_ADMIN);
        schoolService.validateSchoolExists(schoolId);
        return userFacade.createSchoolAdmin(operatorId, request, schoolId);
    }

    public SchoolInfo getSchoolInfoById(Long schoolId) {
        SchoolDto dto = schoolService.getById(schoolId);
        if (null != dto) {
            return domainConverter.conver(dto);
        }
        return null;
    }

    public ClassDto createClass(Long userId, ClassDto params) {
        schoolService.validateSchoolExists(params.getSchoolId());
        classService.validateForCreateClass(params);
        Long parentGroupId = imFacade.createGroup(userId, params.getName() + "家长群", ImGroupType.Parent);
        Long studentGroupId = imFacade.createGroup(userId, params.getName() + "学生群", ImGroupType.Student);
        params.setParentImGroupId(parentGroupId);
        params.setStudentImGroupId(studentGroupId);

        return classService.create(params);
    }

    public ClassDto createClass(Long userId, Long schoolId, Integer enrolYear, Integer classNumber) {
        ClassDto classParams = new ClassDto();
        classParams.setSchoolId(schoolId);
        classParams.setEnrolYear(enrolYear);
        classParams.setClassNumber(classNumber);
        classParams.setCreatedBy(userId);

        classParams.setName(classService.genDefaultClassName(enrolYear, classNumber));

        return this.createClass(userId, classParams);
    }

    public PagingInfo<SchoolInfo> getSchoolInfoList(QueryPaging queryPaging) {
        Page<SchoolDto> dtos = schoolService.getSchoolList(queryPaging);
        List<SchoolInfo> resultDomains = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(dtos)) {
            for (SchoolDto schoolDto : dtos) {
                resultDomains.add(domainConverter.conver(schoolDto));
            }
        }

        return new PagingInfo<SchoolInfo>(dtos, resultDomains);
    }

    public SchoolClassOfTeacher convertToClassOfTeacher(Long teacherId, ClassDto dto) {
        SchoolClassOfTeacher domain = new SchoolClassOfTeacher();
        BeanUtils.copyProperties(dto, domain);
        List<Long> permissions = teacherRoleService.getPermissionsInClass(teacherId, dto.getId());
        if (null == permissions) {
            permissions = Lists.newArrayList();
        }
        domain.setPermissions(permissions);
        return domain;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ClassDto joinClass(TeacherDto teacherDto, Integer enrolYear, Integer classNumber, List<Long> distinctCourseIds) {
        ClassDto classDto = classService.getByClassNumber(teacherDto.getSchoolId()
                , enrolYear, classNumber);
        if (null == classDto) {
            // 班级未创建，则创建班级
            classDto = this.createClass(teacherDto.getUserId(), teacherDto.getSchoolId(), enrolYear, classNumber);
            teacherRoleService.setAsHeadTeacher(teacherDto, classDto.getId());
        } else {
            // 检查任教关系
            courseService.validateForAssignCourses(teacherDto.getId(), classDto.getId(), distinctCourseIds);
        }
        courseService.assginCourses(teacherDto.getId(), classDto.getId(), distinctCourseIds);

        if (!NumberUtils.isNullOrZero(classDto.getParentImGroupId())) {
            imFacade.joinGroup(teacherDto.getUserId(), classDto.getParentImGroupId());
        }

        // TODO 确认任课老师是否要加入到学生群

        return classDto;
    }
}
