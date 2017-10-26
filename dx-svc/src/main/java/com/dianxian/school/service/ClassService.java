package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.ClassDtoMapper;
import com.dianxian.school.dao.TeacherRoleDtoMapper;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.ClassDtoExample;
import com.dianxian.school.dto.SchoolCategory;
import com.dianxian.school.dto.TeacherRoleDto;
import com.dianxian.school.utils.SchoolUtils;
import com.dianxian.user.dto.TeacherDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by xuwenhao on 2016/4/27.
 */
@Component
public class ClassService {
    @Autowired
    ClassDtoMapper classDtoMapper;

    public ClassDto create(ClassDto params) {
        params.setCode(genCode());
        params.setCreatedAt(Calendar.getInstance().getTime());
        classDtoMapper.insert(params);
        return classDtoMapper.selectByPrimaryKey(params.getId());
    }

    private String genCode() {
        String code = null;
        for (int i = 0; i < 10; i++) {
            code = SchoolUtils.genSchoolCode();
            int count = classDtoMapper.countByCode(code);
            if (0 == count) {
                return code;
            }
        }
        throw new BizLogicException(ServiceCodes.SCHOOL_GEN_CODE_FAIL, "Generate code fail.");
    }

    public ClassDto getByClassNumber(Long schoolId, int enrolYear, int classNumber) {
        return classDtoMapper.getByClassNumber(schoolId, enrolYear, classNumber);
    }
    
    public ClassDto getByClassId(Long classId) {
        return classDtoMapper.selectByPrimaryKey(classId);
    }

    public List<ClassDto> getByIds(List<Long> classIds) {
        if (CollectionUtils.isEmpty(classIds)) {
            return null;
        }
        ClassDtoExample example = new ClassDtoExample();
        example.createCriteria().andIdIn(classIds);
        return classDtoMapper.selectByExample(example);
    }

    public void validateForCreateClass(ClassDto params) {
        ClassDto classDto = classDtoMapper.getByClassNumber(params.getSchoolId()
                , params.getEnrolYear(), params.getClassNumber());
        if (null != classDto) {
            throw new BizLogicException(ServiceCodes.CLASS_ALREADY_EXISTS, "Class already exists.");
        }
    }

    public ClassDto validateClassExists(Long classId) {
        ClassDto dto = classDtoMapper.selectByPrimaryKey(classId);
        if (null == dto) {
            throw new BizLogicException(ServiceCodes.CLASS_NOT_EXISTS
                    , String.format("Class %s not exists.", classId));
        }
        return dto;
    }

    public ClassDto getByCode(String code) {
        return classDtoMapper.getByCode(code);
    }

    public String genDefaultClassName(Integer enrolYear, Integer classNumber) {
        return String.format("%s届(%s)班", enrolYear, classNumber);
    }
}
