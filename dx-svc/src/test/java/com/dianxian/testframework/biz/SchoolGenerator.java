package com.dianxian.testframework.biz;

import com.dianxian.core.exception.InvalidParamException;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.school.dao.CourseTeacherDtoMapper;
import com.dianxian.school.dto.*;
import com.dianxian.school.facade.ParentFacade;
import com.dianxian.school.facade.StudentFacade;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.school.manager.SchoolMgr;
import com.dianxian.school.request.teacher.*;
import com.dianxian.school.request.sysadmin.CreateSchoolRequest;
import com.dianxian.school.service.*;
import com.dianxian.sms.dao.OneTimePasswordDtoMapper;
import com.dianxian.sms.service.OtpService;
import com.dianxian.sms.service.SmsService;
import com.dianxian.testframework.DataUtils;
import com.dianxian.testframework.ScenarioContext;
import com.dianxian.user.domain.Roles;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.TeacherDto;
import com.dianxian.user.dto.UserDto;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.service.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by xuwenhao on 2016/6/10.
 */
@Component
public class SchoolGenerator {
    @Autowired
    SchoolMgr schoolMgr;
    @Autowired
    TeacherFacade teacherFacade;
    @Autowired
    StudentFacade studentFacade;
    @Autowired
    UserFacade userFacade;
    @Autowired
    ParentFacade parentFacade;
    @Autowired
    SmsService smsService;
    @Autowired
    OtpService otpService;
    @Autowired
    StudentService studentService;
    @Autowired
    OneTimePasswordDtoMapper oneTimePasswordDtoMapper;
    @Autowired
    UserGenerator userGenerator;
    @Autowired
    TeacherService teacherService;
    @Autowired
    UserService userService;
    @Autowired
    CourseTeacherDtoMapper courseTeacherDtoMapper;
    @Autowired
    CourseService courseService;
    @Autowired
    ClassService classService;
    @Autowired
    QuizService quizService;

    public SchoolDto createSchool() {
        CreateSchoolRequest request = new CreateSchoolRequest();
        request.setName("School_" + DataUtils.getRandomPostfix());
        request.setCategory(SchoolCategory.JUNIOR.value());
        SchoolDto dto = schoolMgr.createSchool(1L, request);
        return dto;
    }

    public ClassDto joinClass(ScenarioContext data, Long teacherUserId, Long courseId) {
        JoinClassRequest joinClassRequest = new JoinClassRequest();
        joinClassRequest.setEnrolYear(data.enrolYear);
        joinClassRequest.setClassNumber(Lists.newArrayList(data.classNumber));
        joinClassRequest.setCourseIds(Lists.newArrayList(courseId));
        return teacherFacade.joinClass(teacherUserId, joinClassRequest).getSuccessClasses().get(0);
    }

    public void revomeStudentFromClass(Long studentUserId, String studentUserName) {
        StudentDto studentDto = getStudentByIdOrName(studentUserId, studentUserName);
        TeacherDto teacherDto = teacherService.getHeadTeacherOfClass(studentDto.getClassId());
        teacherFacade.removeStudentFromClass(teacherDto.getUserId(), studentDto.getClassId(), studentDto.getId());
    }

    private StudentDto getStudentByIdOrName(Long studentUserId, String studentUserName) {
        if (null == studentUserId) {
            UserDto userDto = userService.getUserByName(studentUserName);
            studentUserId = userDto.getId();
        }
        return studentService.validateIsStudentByUserId(studentUserId);
    }

    public ScenarioContext addQuizFromStudent(Long studentUserId, String studentUserName
            , Long courseId, Integer quizCategory, Integer studyYear, Integer studyTerm, Integer studyMonth) {
        CreateQuizRequest request = new CreateQuizRequest();
        request.setCategory(quizCategory);
        request.setCourseId(courseId);
        request.setStudyYear(studyYear);
        request.setStudyTerm(studyTerm);
        request.setStudyMonth(studyMonth);

        return addQuizFromStudent(studentUserId, studentUserName, request);
    }

    public ScenarioContext addQuizFromStudent(Long studentUserId, String studentUserName, CreateQuizRequest request) {
        ScenarioContext context = new ScenarioContext();
        StudentDto studentDto = getStudentByIdOrName(studentUserId, studentUserName);
        Long schoolId = studentDto.getSchoolId();
        Long schoolAdminTeacherId = getSchoolAdmin(schoolId);
        TeacherDto schoolAdminTeacherDto = teacherService.validateIsTeacherById(schoolAdminTeacherId);
        Long classId = studentDto.getClassId();
        ClassDto classDto = classService.validateClassExists(classId);
        if (null == request.getCourseId()) {
            TeacherDto teacherDto = teacherService.getHeadTeacherOfClass(classId);
            List<CourseTeacherDto> courseTeacherDtos = courseTeacherDtoMapper.getAssignedCourses(teacherDto.getId(), classId);
            request.setCourseId(courseTeacherDtos.get(0).getCourseId());
        }
        if (null == request.getCategory()) {
            request.setCategory(QuizCategory.FinalExam.value());
        }
//        if (null == request.getStudyYear()) {
//            request.setStudyYear(Calendar.getInstance().get(Calendar.YEAR));
//        }
//        if (null == request.getStudyTerm()) {
//            request.setStudyTerm(1);
//        }
        if (null == request.getStudyMonth()) {
            request.setStudyMonth(0);
        }

        CourseDto courseDto = courseService.validateCoursesExist(request.getCourseId());
        request.setEnrolYear(classDto.getEnrolYear());
        request.setName(DataUtils.getQuizName(courseDto));
        request.setStartTime(new Date());
        request.setEndTime(new Date());
        QuizDto quizDto = teacherFacade.createQuiz(schoolAdminTeacherDto.getUserId(), request);

        context.studentUserId = studentDto.getUserId();
        context.schoolAdminUserId = schoolAdminTeacherDto.getUserId();
        context.schoolId = schoolId;
        context.teacherCourseId = request.getCourseId();
        context.classId = classId;
        context.quizId = quizDto.getId();

        return context;
    }

    public ScenarioContext addQuizScoreFromStudent(Long studentUserId, String studentUserName
            , Long quizId, Float quizScore
            , Long courseId, Integer quizCategory, Integer studyYear, Integer studyTerm, Integer studyMonth) {

        ScenarioContext context = new ScenarioContext();
        StudentDto studentDto = getStudentByIdOrName(studentUserId, studentUserName);
        if (null == quizId) {
            context = addQuizFromStudent(studentUserId, studentUserName, courseId, quizCategory, studyYear, studyTerm, studyMonth);
        } else {
            QuizDto quizDto = quizService.validateQuizExists(quizId);

            context.quizId = quizId;
            context.teacherCourseId = quizDto.getCourseId();
            context.schoolId = quizDto.getSchoolId();
            context.studentUserId = studentDto.getUserId();
            context.classId = studentDto.getClassId();
        }
        CourseTeacherDto courseTeacherDto = courseTeacherDtoMapper.getAssignedCourseOfClassByCourse(studentDto.getClassId(), context.teacherCourseId);
        if (null == courseTeacherDto) {
            throw new InvalidParamException("No teacher of course" + context.teacherCourseId);
        }
        TeacherDto teacherDto = teacherService.validateIsTeacherById(courseTeacherDto.getTeacherId());
        context.teacherUserId = teacherDto.getUserId();

        if (null == quizScore) {
            quizScore = DataUtils.getRandomScore();
        }
        Map<Long, Float> scoreMap = Maps.newHashMap();
        scoreMap.put(studentDto.getId(), quizScore);
        teacherFacade.setQuizScore(context.teacherUserId, context.quizId, context.classId, scoreMap);

        return context;
    }

    public ScenarioContext addHomeworkFromStudent(Long studentUserId, String studentUserName, Long courseId, Date studyDate, Long resourceFileId) {
        ScenarioContext context = new ScenarioContext();
        StudentDto studentDto = getStudentByIdOrName(studentUserId, studentUserName);
        Long classId = studentDto.getClassId();
        TeacherDto teacherDto;
        if (null == courseId) {
            teacherDto = teacherService.getHeadTeacherOfClass(classId);
            List<CourseTeacherDto> courseTeacherDtos = courseTeacherDtoMapper.getAssignedCourses(teacherDto.getId(), classId);
            courseId = courseTeacherDtos.get(0).getCourseId();
        } else {
            CourseTeacherDto courseTeacherDto = courseTeacherDtoMapper.getAssignedCourseOfClassByCourse(classId, courseId);
            if (null == courseTeacherDto) {
                throw new InvalidParamException(String.format("Course %s is not assigned to any teacher in class %s.", courseId, classId));
            }
            Long teacherId = courseTeacherDto.getTeacherId();
            teacherDto = userService.getTeacherById(teacherId);
        }
        if (null == studyDate) {
            studyDate = new Date();
        }
        CourseDto courseDto = courseService.validateCoursesExist(courseId);
        CreateHomeworkRequest request = new CreateHomeworkRequest();
        request.setClassId(classId);
        request.setCourseId(courseId);
        request.setName(DataUtils.getHomeworkName(courseDto));
        request.setDescription(DataUtils.getHomeworkDesc(courseDto));
        request.setStudyDate(studyDate);
        request.setResourceFileId(resourceFileId);
        HomeworkDto homeworkDto = teacherFacade.createHomework(teacherDto.getUserId(), request);

        context.studentUserId = studentDto.getUserId();
        context.teacherUserId = teacherDto.getUserId();
        context.classId = classId;
        context.teacherCourseId = courseId;
        context.homeworkId = homeworkDto.getId();

        return context;
    }

    public Long addSchoolAnnouncement(Long schoolId) {
        Long schoolAdminUserId = getSchoolAdmin(schoolId);
        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setType(AnnouncementType.SCHOOL);
        String postfix = DataUtils.getRandomPostfix();
        request.setTitle("学校公告_" + postfix);
        request.setContent("学校公告内容_" + postfix);
        return teacherFacade.createSchoolAnnouncement(schoolAdminUserId, request);
    }

    private Long getSchoolAdmin(Long schoolId) {
        List<Long> adminTeacherIds = teacherService.getTeachersOfSchoolByRole(schoolId, Roles.SCHOOL_ADMIN);
        if (CollectionUtils.isEmpty(adminTeacherIds)) {
            throw new InvalidParamException("No school admin in school " + schoolId);
        }
        return adminTeacherIds.get(0);
    }

    public Long addClassAnnouncement(Long classId) {
        TeacherDto teacherDto = teacherService.getHeadTeacherOfClass(classId);
        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setType(AnnouncementType.CLASS);
        String postfix = DataUtils.getRandomPostfix();
        request.setTitle("班级公告_" + postfix);
        request.setContent("班级公告内容_" + postfix);
        return teacherFacade.createSchoolAnnouncement(teacherDto.getUserId(), request);
    }

    public void addCourseMaterial(Long classId, Long courseId) {
        CourseTeacherDto courseTeacherDto = courseTeacherDtoMapper.getAssignedCourseOfClassByCourse(classId, courseId);
        if (null == courseTeacherDto) {
            throw new InvalidParamException(String.format("Course %s is not assigned to any teacher in class %s.", courseId, classId));
        }
        TeacherDto teacherDto = userService.getTeacherById(courseTeacherDto.getTeacherId());
        CreateCourseMaterialRequest request = new CreateCourseMaterialRequest();
        request.setClassId(classId);
        request.setCourseId(courseId);
        request.setType(CourseMaterialType.COURSEWARE);
        request.setName("CourseMaterial_" + StringUtils.getRandomString(16));
        request.setDescription("CourseMaterial_Des_" + StringUtils.getRandomString(16));
        teacherFacade.createCourseMaterial(teacherDto.getUserId(), request);
    }

    public void addHomework(Long classId, Long courseId) {
        CourseTeacherDto courseTeacherDto = courseTeacherDtoMapper.getAssignedCourseOfClassByCourse(classId, courseId);
        if (null == courseTeacherDto) {
            throw new InvalidParamException(String.format("Course %s is not assigned to any teacher in class %s.", courseId, classId));
        }
        TeacherDto teacherDto = userService.getTeacherById(courseTeacherDto.getTeacherId());
        CourseDto courseDto = courseService.validateCoursesExist(courseId);
        CreateHomeworkRequest request = new CreateHomeworkRequest();
        request.setClassId(classId);
        request.setCourseId(courseId);
        request.setName(DataUtils.getHomeworkName(courseDto));
        request.setDescription(DataUtils.getHomeworkDesc(courseDto));
        request.setStudyDate(new Date());
        teacherFacade.createHomework(teacherDto.getUserId(), request);
    }
}
