package com.dianxian.school.facade;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.exception.InvalidParamException;
import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.im.facade.IMFacade;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.*;
import com.dianxian.school.manager.AnnouncementMgr;
import com.dianxian.school.manager.CourseMgr;
import com.dianxian.school.manager.HomeworkMgr;
import com.dianxian.school.manager.QuizMgr;
import com.dianxian.school.service.*;
import com.dianxian.school.utils.SchoolUtils;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.ParentChildDto;
import com.dianxian.user.dto.ParentDto;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.service.UserService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by xuwenhao on 2016/6/9.
 */
@Component
public class ParentFacade {
    @Autowired
    UserFacade userFacade;
    @Autowired
    UserService userService;
    @Autowired
    StudentService studentService;
    @Autowired
    SchoolService schoolService;
    @Autowired
    CourseMgr courseMgr;
    @Autowired
    CourseService courseService;
    @Autowired
    QuizMgr quizMgr;
    @Autowired
    AnnouncementMgr announcementMgr;
    @Autowired
    HomeworkMgr homeworkMgr;
    @Autowired
    ClassService classService;
    @Autowired
    HomeworkService homeworkService;
    @Autowired
    StudentFacade studentFacade;
    @Autowired
    IMFacade imFacade;
    @Autowired
    MessageService messageService;
    @Autowired
    SchoolAppProperties schoolAppProperties;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<User> getChildren(Long parentUserId) {
        ParentDto parentDto = userService.validateParentInfoExists(parentUserId);

        List<User> result = Lists.newArrayList();
        List<ParentChildDto> parentChildDtos = userService.getChildren(parentUserId);

        if (!CollectionUtils.isEmpty(parentChildDtos)) {
            for (ParentChildDto parentChildDto : parentChildDtos) {
                User user = convert(parentChildDto);
                if (user.getId().equals(parentDto.getDefaultChildUserId())) {
                    user.setIsDefaultChild(true);
                }
                result.add(user);
            }
        }

        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addChild(Long parentUserId, String studentCode, Integer relation) {
        userService.validateIsParent(parentUserId);
        StudentDto studentDto = userService.getStudentByCode(studentCode);
        if (null == studentDto) {
            throw new BizLogicException(ServiceCodes.STUDENT_CODE_NOT_MATCH, "Student code does not match.");
        }
        List<ParentChildDto> parents = userService.getParents(studentDto.getUserId());
        if (!CollectionUtils.isEmpty(parents)) {
            if (parents.size() >= schoolAppProperties.getStudentParentMaxCount()) {
                throw new BizLogicException(ServiceCodes.STUDENT_MAX_PARENT
                        , String.format("Student already has %s parents.", parents.size()));
            }
        }
        userService.addChild(parentUserId, studentDto.getUserId(), relation);
        // 设为默认的孩子
        userService.updateDefaultChild(parentUserId, studentDto.getUserId());

        // 加入孩子班级的家长班群
        if (!NumberUtils.isNullOrZero(studentDto.getClassId())) {
            ClassDto classDto = classService.validateClassExists(studentDto.getClassId());
            if (!NumberUtils.isNullOrZero(classDto.getParentImGroupId())) {
                imFacade.joinGroup(parentUserId, classDto.getParentImGroupId());
            }
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void selectChild(Long parentUserId, Long studentUserId) {
        validateChildRelation(parentUserId, studentUserId);
        userService.updateDefaultChild(parentUserId, studentUserId);
        // TODO 修改session中的默认孩子
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public PagingInfo<SosMessageByDayDto> getSosMessages(Long parentUserId, QueryPaging queryPaging) {
        ParentDto parentDto = userService.getParentInfo(parentUserId);
        Long studentUserId = parentDto.getDefaultChildUserId();
        if(studentUserId == null){
            Page<SosMessageByDayDto> sosMessageDtos = new Page<SosMessageByDayDto>();
            return new PagingInfo<SosMessageByDayDto>(sosMessageDtos, sosMessageDtos);
        }
        StudentDto studentDto = userService.getStudentByUserId(studentUserId);
        Page<SosMessageByDayDto> sosMessageDtos = messageService.getSosMessagesOfStudent(studentDto.getId(), queryPaging);
        return new PagingInfo<SosMessageByDayDto>(sosMessageDtos, sosMessageDtos);

    }
    @Transactional(propagation = Propagation.SUPPORTS)
    public User getChild(Long parentUserId, Long studentUserId) {
        ParentChildDto parentChildDto = validateChildRelation(parentUserId, studentUserId);
        return convert(parentChildDto);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public StudentHomeData getHomeData(Long parentUserId, Long studentUserId, Date studyDate) {
        this.validateChildRelation(parentUserId, studentUserId);
        return studentFacade.getHomeData(parentUserId, studentUserId, studyDate);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseAssignment> getCourseAssignments(Long parentUserId, Long studentUserId) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        ClassDto classDto = classService.validateClassExists(studentDto.getClassId());
        return courseMgr.getCourseAssignmentsOfClass(classDto);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseSchedule> getCourseSchedule(Long parentUserId, Long studentUserId) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        return courseMgr.getCourseSchedule(studentDto.getClassId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseQuiz> getRecentQuizes(Long parentUserId, Long studentUserId) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        return quizMgr.getRecentQuizes(studentDto.getClassId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public QuizScoreSummary getQuizScoreReport(Long parentUserId, Long studentUserId, Integer studyYear, Integer studyTerm, Integer category) {
        this.validateChildRelation(parentUserId, studentUserId);
        return quizMgr.getQuizScoreReportOfStudent(studentUserId, studyYear, studyTerm, category);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public CourseScoreReport getCourseScoreReport(Long parentUserId, Long studentUserId, Long courseId, Integer studyYear, Integer category) {
        this.validateChildRelation(parentUserId, studentUserId);
        return quizMgr.getCourseScoreReport(studentUserId, courseId, studyYear, category);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseQuizScore> getCourseScoreTrends(Long parentUserId, Long studentUserId, Long courseId, Integer studyYear) {
        this.validateChildRelation(parentUserId, studentUserId);
        return quizMgr.getCourseScoreTrends(studentUserId, courseId, studyYear);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<Announcement> getAnnouncements(Long parentUserId, Long studentUserId, QueryPaging queryPaging) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        return announcementMgr.getAnnouncementsOfClass(studentDto.getClassId(), queryPaging);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<CourseMaterial> getCourseMaterials(Long parentUserId, Long studentUserId, Long courseId, Integer type, Date beginDate, Date endDate, QueryPaging queryPaging) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        return courseMgr.getCourseMaterials(studentDto.getClassId(), courseId, type, beginDate, endDate, queryPaging);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public HomeworkReviewListResult getHomeworkReviewStatus(Long parentUserId, Long studentUserId, Date studyDate) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        return homeworkMgr.getHomeworkReviewStatus(studentDto.getId(), studentDto.getClassId(), studyDate);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reviewHomework(Long parentUserId, Long studentUserId, Long homeworkId) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        HomeworkDto homeworkDto = homeworkService.validateHomeworkExists(homeworkId);
        if (!studentDto.getClassId().equals(homeworkDto.getClassId())) {
            throw new InvalidParamException(String.format("Student %s does not have homework %s.", studentUserId, homeworkId));
        }
        homeworkService.reviewHomework(parentUserId, studentDto.getId(), homeworkId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void reviewHomeworkByCourse(Long parentUserId, Long studentUserId, Long courseId, Date studyDate) {
        this.validateChildRelation(parentUserId, studentUserId);
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        courseService.validateCoursesExist(courseId);
        Calendar queryDate = SchoolUtils.calculateQueryDate(studyDate);
        List<HomeworkDto> homeworkDtos = homeworkService.getHomeworkOfClassByCourse(studentDto.getClassId(), courseId, queryDate.getTime());
        homeworkService.reviewHomework(parentUserId, studentDto.getId(), homeworkDtos);
    }

    private ParentChildDto validateChildRelation(Long parentUserId, Long studentUserId) {
        userService.validateIsParent(parentUserId);
        ParentChildDto parentChildDto = userService.validateIsChild(parentUserId, studentUserId);
        return parentChildDto;
    }

    private User convert(ParentChildDto parentChildDto) {
        return userFacade.getUser(parentChildDto.getChildUserId());
    }
}
