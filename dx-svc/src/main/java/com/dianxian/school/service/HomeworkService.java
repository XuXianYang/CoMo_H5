package com.dianxian.school.service;

import com.beust.jcommander.internal.Maps;
import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.HomeworkDtoMapper;
import com.dianxian.school.dao.HomeworkReviewDtoMapper;
import com.dianxian.school.dto.HomeworkDto;
import com.dianxian.school.dto.HomeworkReviewDetailByCourseDto;
import com.dianxian.school.dto.HomeworkReviewDto;
import com.dianxian.user.dao.ParentChildDtoMapper;
import com.dianxian.user.dao.TeacherDtoMapper;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.UserDto;
import com.dianxian.user.service.UserService;
import com.dianxian.web.utils.DisplayUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XuWenHao on 6/7/2016.
 */
@Component
public class HomeworkService {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    @Autowired
    HomeworkDtoMapper homeworkDtoMapper;
    @Autowired
    HomeworkReviewDtoMapper homeworkReviewDtoMapper;
    @Autowired
    UserService userService;
    @Autowired
    StudentService studentService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    TeacherDtoMapper teacherDtoMapper;
    @Autowired
    ParentChildDtoMapper parentChildDtoMapper;
    @Autowired
    PlaygroundActService playgroundActService;

    public HomeworkDto createHomework(Long userId, HomeworkDto homeworkDto) {
        homeworkDto.setCreatedBy(userId);
        homeworkDtoMapper.insert(homeworkDto);
        return homeworkDtoMapper.selectByPrimaryKey(homeworkDto.getId());
    }

    public void updateHomework(Long userId, HomeworkDto homeworkDto) {
        homeworkDto.setUpdatedBy(userId);
        homeworkDtoMapper.updateByPrimaryKeySelective(homeworkDto);
    }

    public HomeworkDto validateHomeworkExists(Long homeworkId) {
        HomeworkDto homeworkDto = homeworkDtoMapper.selectByPrimaryKey(homeworkId);
        if (null == homeworkDto) {
            throw new BizLogicException(ServiceCodes.HOMEWORK_NOT_EXISTS
                    , String.format("Homework %s does not exist.", homeworkId));
        }
        return homeworkDto;
    }

    public List<HomeworkDto> getHomeworkOfClass(Long classId, Date studyDate) {
        return homeworkDtoMapper.getHomeworkOfClass(classId, dateFormat.format(studyDate));
    }

    public List<HomeworkDto> getHomeworkOfClassByCourse(Long classId, Long courseId, Date studyDate) {
        return homeworkDtoMapper.getHomeworkOfClassByCourse(classId, courseId, dateFormat.format(studyDate));
    }

    public Page<HomeworkDto> getHomeworkList(Long teacherId, Long classId, QueryPaging queryPaging) {
        Page<HomeworkDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        homeworkDtoMapper.getHomeworkOfClassByTeacherId(classId, teacherId);
        return page;
    }

    public HomeworkReviewDetailByCourseDto getHomeworkReviewDetail(Long teacherId, Long homeworkId, Long classId) {
        HomeworkDto homeworkDto = validateHomeworkExists(homeworkId);
        if(homeworkDto.getTeacherId() != teacherId){
            throw new BizLogicException(ServiceCodes.HOMEWORK_AND_TEACHER_NOT_MATCH, "作业教师信息不匹配");
        }
        List<HomeworkReviewDto> homeworkReviewList = this.getReviewRecordsByHomeworkId(homeworkId);
        int signedCount = 0;
        List<StudentDto> studentDtos = studentService.getStudentsOfClass(classId);
        HashMap<Long, String> studentIdName = new HashMap<Long, String>();
        for (StudentDto studentDto : studentDtos){
            UserDto userDto = userService.getUserById(studentDto.getUserId());
            studentIdName.put(studentDto.getId(),userDto.getUsername());
        }
        HomeworkReviewDetailByCourseDto  detailByCourseDto = new HomeworkReviewDetailByCourseDto();
        List<String> stundentsNameList = new ArrayList<String>();

        detailByCourseDto.setStudentsNames(stundentsNameList);
        for (HomeworkReviewDto homeworkReviewDto : homeworkReviewList ){
            Long studentId = homeworkReviewDto.getStudentId();
            studentIdName.remove(studentId);
            if (homeworkReviewDto.getReviewTime() != null){
                signedCount += 1;
            }
        }
        BigDecimal signRatio = BigDecimal.ZERO;
        if (studentDtos.size() > 0){
            signRatio = new BigDecimal(signedCount).divide(new BigDecimal(studentDtos.size()), 4, BigDecimal.ROUND_DOWN);
        }
        detailByCourseDto.setSignRatio(signRatio);
        for(Long studentId : studentIdName.keySet()){
            stundentsNameList.add(studentIdName.get(studentId));
        }
        return detailByCourseDto;
    }

    public HomeworkReviewDto getReviewRecord(Long homeworkId, Long studentId) {
        return homeworkReviewDtoMapper.getReviewRecord(homeworkId, studentId);
    }

    public List<HomeworkReviewDto> getReviewRecordsOfStudent(Long studentId, Date studyDate) {
        return homeworkReviewDtoMapper.getReviewRecordsOfStudent(studentId, dateFormat.format(studyDate));
    }

    public List<HomeworkReviewDto> getReviewRecordsByHomeworkId(Long homeworkId) {
        return homeworkReviewDtoMapper.getReviewRecordsByHomeworkId(homeworkId);
    }

    public void reviewHomework(Long parentUserId, Long studentId, Long homeworkId) {
        if (null != getReviewRecord(homeworkId, studentId)) {
            // 已阅
            return;
        }
        HomeworkDto homeworkDto = homeworkDtoMapper.selectByPrimaryKey(homeworkId);
        reviewHomework(parentUserId, studentId, homeworkDto);
    }

    private void reviewHomework(Long parentUserId, Long studentId, HomeworkDto homeworkDto) {
        HomeworkReviewDto homeworkReviewDto = new HomeworkReviewDto();
        homeworkReviewDto.setStudentId(studentId);
        homeworkReviewDto.setHomeworkId(homeworkDto.getId());
        homeworkReviewDto.setStudyDate(homeworkDto.getStudyDate());
        homeworkReviewDto.setCreatedBy(parentUserId);

        homeworkReviewDtoMapper.insert(homeworkReviewDto);
    }

    public void reviewHomework(Long parentUserId, Long studentId, List<HomeworkDto> homeworkDtos) {
        if (CollectionUtils.isEmpty(homeworkDtos)) {
            return;
        }
        for (HomeworkDto homeworkDto : homeworkDtos) {
            if (null != getReviewRecord(homeworkDto.getId(), studentId)) {
                // 已阅
                continue;
            }
            reviewHomework(parentUserId, studentId, homeworkDto);
        }
    }

    /**
     * 获取作业签字详情
     * chenzhong
     * @param homeworkId
     * @return
     */
    public List<HomeworkReviewDto> findReviewList(Long homeworkId) {
        //获取这个作业信息
        HomeworkDto homeworkDto = homeworkDtoMapper.selectByPrimaryKey(homeworkId);
        if (homeworkDto == null) throw new BizLogicException(ServiceCodes.HOMEWORK_NOT_EXISTS, "HOMEWORK_NOT_EXISTS");
        //所有的签字数据
        List<HomeworkReviewDto> dtos = homeworkReviewDtoMapper.getReviewRecordsByHomeworkId(homeworkId);
        //一个班的所有学生
        List<StudentDto> ofClass = userService.getStudentsOfClass(homeworkDto.getClassId());
        List<Long> userIds = Lists.newLinkedList();/*用户id*/
        for (HomeworkReviewDto dto : dtos) {
            userIds.add(dto.getCreatedBy());/*签字人id*/
        }

        // studentId -- userId 显示所有的学生-->做签字返回数据
        Map<Long, Long> idUserIdMap = Maps.newHashMap();
        for (StudentDto ofClas : ofClass) {
            userIds.add(ofClas.getUserId());
            idUserIdMap.put(ofClas.getId(), ofClas.getUserId());
        }

        Map<Long, User> users = userService.getUsersByIdsToMap(userIds);/*所有用户*/

        for (HomeworkReviewDto dto : dtos) {
            dto.setParentName(users.get(dto.getCreatedBy()).getRealName());/*签字人信息*/
            dto.setStrReviewTime(DisplayUtils.formatDateTime(dto.getReviewTime()));/*日期格式化*/
        }

        //返回结果集
        List<HomeworkReviewDto> result = Lists.newLinkedList();
        for (Map.Entry<Long, Long> entry : idUserIdMap.entrySet()) {
            HomeworkReviewDto dto = new HomeworkReviewDto();
            for (HomeworkReviewDto reviewDto : dtos) {
                if (entry.getKey() == reviewDto.getStudentId()) {
                    BeanUtils.copyProperties(reviewDto, dto);
                    dto.setId(null);
                    dto.setSign(true);
                }
            }
            dto.setStudentName(users.get(entry.getValue()).getRealName());
            result.add(dto);
        }
        return result;
    }
}
