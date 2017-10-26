package com.dianxian.user.dao;

import com.dianxian.school.domain.Student;
import com.dianxian.user.dto.StudentDto;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface StudentDtoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(StudentDto record);

    int insertSelective(StudentDto record);

    StudentDto selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StudentDto record);

    int updateByPrimaryKeyWithBLOBs(StudentDto record);

    int updateByPrimaryKey(StudentDto record);

    int updateWarningTimeById(StudentDto record);

    StudentDto getByUserId(Long userId);

    int countByStudentNo(String studentNo);

    int countByCode(String code);

    int joinClass(StudentDto record);

    int removeFromClass(Long id);

    int updateStudentNo(StudentDto record);

    StudentDto getByCode(String code);

    StudentDto getByStudentNo(String studentNo);

    List<StudentDto> getStudentsByIds(List<Long> studentIds);

    List<StudentDto> getStudentsOfClass(Long classId);

    List<StudentDto> findUnConfigExceptedHomeTimeStu(Date dateTime);

    List<StudentDto> findStudentTimeout(@Param("dateTime") Date dateTime ,@Param("midnight") Date midnight);

}