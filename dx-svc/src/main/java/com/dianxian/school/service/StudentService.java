package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.core.utils.time.DateUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.request.RequestTime;
import com.dianxian.user.dao.StudentDtoMapper;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.UserDto;
import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by XuWenHao on 5/18/2016.
 */
@Component
public class StudentService {
    @Autowired
    UserService userService;
    @Autowired
    StudentDtoMapper studentDtoMapper;

    public StudentDto validateIsStudentByUserId(Long userId) {
        StudentDto studentDto = userService.getStudentByUserId(userId);
        if (null == studentDto) {
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, String.format("User %s is not a student.", userId));
        }
        return studentDto;
    }
    public StudentDto validStudentByNoName(String studentNo, String userName) {
        StudentDto studentDto = userService.getByStudentNo(studentNo);
        if (null != studentDto) {
            UserDto userDto = userService.getUserById(studentDto.getUserId());
            UserInfoDto userInfoDto = userService.getUserInfoById(userDto.getId());
            String userNameDB = (userInfoDto == null ? "": userInfoDto.getName());
            if (!userNameDB.equals(userName)){
                return null;
            }
        }
        return studentDto;
    }
    public StudentDto validateIsStudentById(Long studentId) {
        StudentDto studentDto = userService.getStudentById(studentId);
        if (null == studentDto) {
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, String.format("Student %s does not exist.", studentId));
        }
        return studentDto;
    }

    public StudentDto validateHasJoinedClass(Long userId) {
        StudentDto studentDto = this.validateIsStudentByUserId(userId);
        validateHasJoinedClass(studentDto);
        return studentDto;
    }

    public void validateHasJoinedClass(StudentDto studentDto) {
        if (NumberUtils.isNullOrZero(studentDto.getClassId())) {
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_JOINED_CLASS, "Has not joined class.");
        }
    }

    public void joinClass(StudentDto studentDto, ClassDto classDto) {
        studentDto.setSchoolId(classDto.getSchoolId());
        studentDto.setEnrolYear(classDto.getEnrolYear());
        studentDto.setClassNumber(classDto.getClassNumber());
        studentDto.setClassId(classDto.getId());
        studentDto.setJoinClassAt(new Date());
        userService.joinClass(studentDto);
    }

    public List<StudentDto> getStudentsOfClass(Long classId) {
        return userService.getStudentsOfClass(classId);
    }

    public void removeFromClass(Long studentId) {
        userService.removeFromClass(studentId);
    }

    public void setExceptedHomeTime(StudentDto studentDto, Date exceptTime, Long timeout){
        studentDto.setExpectedAtHomeTime(exceptTime);
        studentDto.setExpectedAtHomeTimeBuffer(calculateTimeout(timeout));
        studentDto.setExpectedAtHomeTimeUpdateAt(new Date());
        studentDto.setExpectedAtHomeTimeout(calculateExceptTimeout(exceptTime, timeout));
        studentDtoMapper.updateByPrimaryKey(studentDto);
    }

    private Date calculateExceptTimeout(Date expectTime, Long timeout){
        long hourBuffer = timeout / 3600;
        long minuteBuffer = (timeout / 60) % 60;
        Calendar cal = Calendar.getInstance();
        cal.setTime(expectTime);
        cal.add(Calendar.HOUR_OF_DAY, (int)hourBuffer);
        cal.add(Calendar.MINUTE, (int)minuteBuffer);
        return cal.getTime();
    }
    private Date calculateTimeout(Long timeout){
        long hourBuffer = timeout / 3600;
        long minuteBuffer = (timeout / 60) % 60;
        long secondBuffer = timeout % 60;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, (int)hourBuffer);
        cal.set(Calendar.MINUTE, (int)minuteBuffer);
        cal.set(Calendar.SECOND, (int)secondBuffer);
        return cal.getTime();
    }

    public List<StudentDto> findUnConfigExceptedHomeTimeStu() {
        Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        List<StudentDto> studentList = studentDtoMapper.findUnConfigExceptedHomeTimeStu(cal.getTime());
        return studentList;
    }

    public List<StudentDto> findStudentTimeout() {
        Calendar cal = DateUtils.truncate(Calendar.getInstance(), Calendar.DATE);
        List<StudentDto> studentList = studentDtoMapper.findStudentTimeout(new Date(), cal.getTime());
        return studentList;
    }


}
