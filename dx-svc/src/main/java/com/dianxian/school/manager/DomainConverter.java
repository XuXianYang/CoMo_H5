package com.dianxian.school.manager;

import com.dianxian.school.domain.ClassInfo;
import com.dianxian.school.domain.SchoolClassBasicInfo;
import com.dianxian.school.domain.SchoolInfo;
import com.dianxian.school.domain.SimpleStudent;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.SchoolDto;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.UserInfoDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by xuwenhao on 2016/10/9.
 */
@Component
public class DomainConverter {
    public ClassInfo toClassInfo(ClassDto classDto) {
        ClassInfo classInfo = new ClassInfo();
        classInfo.setClassId(classDto.getId());
        classInfo.setClassName(classDto.getName());
        return classInfo;
    }

    public SimpleStudent convert(StudentDto studentDto, UserInfoDto userInfoDto) {
        SimpleStudent simpleStudent = new SimpleStudent();
        simpleStudent.setStudentId(studentDto.getId());

        if (null != userInfoDto) {
            simpleStudent.setRealName(userInfoDto.getName());
        }
        return simpleStudent;
    }

    public SchoolInfo conver(SchoolDto dto) {
        SchoolInfo info = new SchoolInfo();
        BeanUtils.copyProperties(dto, info);
        return info;
    }

    public SchoolClassBasicInfo convert(ClassDto classDto) {
        SchoolClassBasicInfo basicInfo = new SchoolClassBasicInfo();
        BeanUtils.copyProperties(classDto, basicInfo);
        return basicInfo;
    }
}
