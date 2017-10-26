package com.dianxian.test;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.school.domain.SetCourseScheduleItem;
import com.dianxian.school.domain.SchoolInfo;
import com.dianxian.school.dto.*;
import com.dianxian.school.facade.ParentFacade;
import com.dianxian.school.facade.StudentFacade;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.school.manager.CourseMgr;
import com.dianxian.school.manager.SchoolMgr;
import com.dianxian.school.request.*;
import com.dianxian.school.request.teacher.*;
import com.dianxian.school.request.sysadmin.CreateSchoolRequest;
import com.dianxian.school.service.CourseService;
import com.dianxian.school.service.StudentService;
import com.dianxian.sms.service.OtpService;
import com.dianxian.sms.service.SmsService;
import com.dianxian.storage.service.StorageAppProperties;
import com.dianxian.testframework.*;
import com.dianxian.testframework.biz.UserGenerator;
import com.dianxian.user.dto.ParentChildRelation;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.facade.bean.UpdateUserInfoRequest;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by xuwenhao on 2016/5/9.
 */
public class EmulateTest extends AbstractServiceTest {
    private static class Group {
        public static final String INIT_SCHOOL = "INIT_SCHOOL";
        public static final String TEACHER_REGISTER = "TEACHER_REGISTER";
        public static final String TEACHER_JOIN_SCHOOL = "TEACHER_JOIN_SCHOOL";
        public static final String BEFORE_TEACHER_JOIN_CLASS = "BEFORE_TEACHER_JOIN_CLASS";
        public static final String TEACHER_JOIN_CLASS = "TEACHER_JOIN_CLASS";
        public static final String BEFORE_TEACHER_SET_COURSE_SCHEDULE = "BEFORE_TEACHER_SET_COURSE_SCHEDULE";
        public static final String TEACHER_SET_COURSE_SCHEDULE = "TEACHER_SET_COURSE_SCHEDULE";
        public static final String BEFORE_TEACHER_SET_QUIZ_SCORE = "BEFORE_TEACHER_SET_QUIZ_SCORE";
        public static final String TEACHER_SET_QUIZ_SCORE = "TEACHER_SET_QUIZ_SCORE";
        public static final String ADMIN_REGISTER = "ADMIN_REGISTER";
        public static final String BEFORE_ADMIN_CREATE_QUIZ = "BEFORE_ADMIN_CREATE_QUIZ";
        public static final String ADMIN_CREATE_QUIZ = "ADMIN_CREATE_QUIZ";
        public static final String STUDENT_REGISTER = "STUDENT_REGISTER";
        public static final String BEFORE_STUDENT_JOIN_CLASS = "BEFORE_STUDENT_JOIN_CLASS";
        public static final String STUDENT_JOIN_CLASS = "STUDENT_JOIN_CLASS";
        public static final String BEFORE_ADMIN_CREATE_SCHOOL_ANNOUNCEMENT = "BEFORE_ADMIN_CREATE_SCHOOL_ANNOUNCEMENT";
        public static final String ADMIN_CREATE_SCHOOL_ANNOUNCEMENT = "ADMIN_CREATE_SCHOOL_ANNOUNCEMENT";
        public static final String BEFORE_TEACHER_CREATE_COURSE_MATERIAL = "BEFORE_TEACHER_CREATE_COURSE_MATERIAL";
        public static final String TEACHER_CREATE_COURSE_MATERIAL = "TEACHER_CREATE_COURSE_MATERIAL";
        public static final String BEFORE_TEACHER_UPDATE_COURSE_MATERIAL = "BEFORE_TEACHER_UPDATE_COURSE_MATERIAL";
        public static final String TEACHER_UPDATE_COURSE_MATERIAL = "TEACHER_UPDATE_COURSE_MATERIAL";
        public static final String BEFORE_TEACHER_CREATE_HOMEWORK = "BEFORE_TEACHER_CREATE_HOMEWORK";
        public static final String TEACHER_CREATE_HOMEWORK = "TEACHER_CREATE_HOMEWORK";
        public static final String BEFORE_TEACHER_UPDATE_HOMEWORK = "BEFORE_TEACHER_UPDATE_HOMEWORK";
        public static final String TEACHER_UPDATE_HOMEWORK = "TEACHER_UPDATE_HOMEWORK";
        public static final String PARENT_REGISTER = "PARENT_REGISTER";
        public static final String BEFORE_PARENT_ADD_CHILD = "BEFORE_PARENT_ADD_CHILD";
        public static final String PARENT_ADD_CHILD = "PARENT_ADD_CHILD";
        public static final String BEFORE_PARENT_REVIEW_HOMEWORK = "BEFORE_PARENT_REVIEW_HOMEWORK";
        public static final String PARENT_REVIEW_HOMEWORK = "PARENT_REVIEW_HOMEWORK";
        public static final String STUDENT_SEND_SNEAKING_MSG = "STUDENT_SEND_SNEAKING_MSG";
        public static final String STUDENT_SEND_SOS_MSG = "STUDENT_SEND_SOS_MSG";
    }

    @Autowired
    StorageAppProperties storageAppProperties;
    @Autowired
    SchoolMgr schoolMgr;
    @Autowired
    TeacherFacade teacherFacade;
    @Autowired
    CourseMgr courseMgr;
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
//    @Autowired
//    OneTimePasswordDtoMapper oneTimePasswordDtoMapper;
    @Autowired
    UserGenerator userGenerator;
    @Autowired
    CourseService courseService;

    private ScenarioContext data = new ScenarioContext();

    public EmulateTest() {
        data.adminId = 1L;
        data.headTeacherCourseId = 1L;
        data.teacherCourseId = 2L;
        data.enrolYear = 2000;
        data.classNumber = 1;
        data.studyYear = 2016;
        data.studyTerm = 1;
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        GsonBuilder gb = new GsonBuilder();
        gb.setPrettyPrinting();
        Gson gson = gb.create();
        logger.info(gson.toJson(data));
    }

    public Long generateTeacher() {
        return userGenerator.registerTeacher();
    }

    @Test(groups = {Group.INIT_SCHOOL})
    public void test_SysAdmin_CreateSchool() {
        CreateSchoolRequest request = new CreateSchoolRequest();
        request.setName("School_" + StringUtils.getRandomString(12));
        request.setCategory(SchoolCategory.JUNIOR.value());
        SchoolDto dto = schoolMgr.createSchool(1L, request);
        Assert.assertNotNull(dto);
        Assert.assertNotNull(dto.getId());
        data.schoolId = dto.getId();
        data.schoolCode = dto.getCode();
    }

    @Test(dependsOnGroups = {Group.INIT_SCHOOL})
    public void test_SysAdmin_getSchoolInfo() {
        SchoolInfo info = schoolMgr.getSchoolInfoById(data.schoolId);
        Assert.assertNotNull(info);
    }

    @Test(groups = {Group.TEACHER_REGISTER}, dependsOnGroups = {Group.INIT_SCHOOL})
    public void test_Teacher_Register() {
        data.headTeacherUserId = generateTeacher();
        data.teacherUserId = generateTeacher();
    }

    @Test(dependsOnGroups = {Group.TEACHER_REGISTER})
    public void test_Teacher_getUser() {
        userFacade.getUser(data.headTeacherUserId);
    }

    @Test(dependsOnGroups = {Group.TEACHER_REGISTER})
    public void test_Teacher_updateUserInfo() {
        Long resourceFileId = 1L;
        if (storageAppProperties.getCheckResourceFile()) {
            resourceFileId = userFacade.uploadAvatar(data.headTeacherUserId
                    , ClassPathResourceLoader.getResourceAsStream("test.jpg"), new MockFormDataContentDisposition("file", "测试图片.jpg"), 100, 100, 100);
        }
        userFacade.setAvatar(data.headTeacherUserId, resourceFileId);

        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        request.setRealName("Teacher_" + StringUtils.getRandomString(8));

        userFacade.updateUserInfo(data.headTeacherUserId, request);
    }

    @Test(groups = {Group.TEACHER_JOIN_SCHOOL}, dependsOnGroups = {Group.TEACHER_REGISTER})
    public void test_Teacher_JoinSchool() {
        teacherFacade.joinSchool(data.headTeacherUserId, data.schoolCode, data.schoolId);
        teacherFacade.joinSchool(data.teacherUserId, data.schoolCode, data.schoolId);
    }

    @Test(groups = {Group.BEFORE_TEACHER_JOIN_CLASS}, dependsOnGroups = {Group.TEACHER_JOIN_SCHOOL})
    public void test_Teacher_getCourseAssignments() {
        teacherFacade.getCourseAssignments(data.headTeacherUserId, data.enrolYear, data.classNumber);
    }

    @Test(groups = {Group.TEACHER_JOIN_CLASS}, dependsOnGroups = {Group.TEACHER_JOIN_SCHOOL, Group.BEFORE_TEACHER_JOIN_CLASS})
    public void test_Teacher_JoinClass() {
        {
            JoinClassRequest joinClassRequest = new JoinClassRequest();
            joinClassRequest.setEnrolYear(data.enrolYear);
            joinClassRequest.setClassNumber(Lists.newArrayList(data.classNumber));
            joinClassRequest.setCourseIds(Lists.newArrayList(data.headTeacherCourseId));
            data.classDto = teacherFacade.joinClass(data.headTeacherUserId, joinClassRequest).getSuccessClasses().get(0);
            data.classId = data.classDto.getId();
        }
        teacherFacade.getCourseAssignments(data.headTeacherUserId, data.enrolYear, data.classNumber);
        {
            JoinClassRequest joinClassRequest = new JoinClassRequest();
            joinClassRequest.setEnrolYear(data.enrolYear);
            joinClassRequest.setClassNumber(Lists.newArrayList(data.classNumber));
            joinClassRequest.setCourseIds(Lists.newArrayList(data.teacherCourseId));
            teacherFacade.joinClass(data.teacherUserId, joinClassRequest);
        }
    }

    @Test(groups = {Group.ADMIN_REGISTER}, dependsOnGroups = {Group.INIT_SCHOOL})
    public void test_SysAdmin_createSchoolAdmin() {
        data.schoolAdminUserId = userGenerator.registerSchoolAdmin(data.schoolId);
    }

    @Test(groups = {Group.STUDENT_REGISTER})
    public void test_Student_Register() {
        data.studentUserId = userGenerator.registerStudent();
        notNull(data.studentUserId);
    }

    @Test(dependsOnGroups = {Group.STUDENT_REGISTER})
    public void test_Student_updateUserInfo() {
        Long resourceFileId = 1L;
        if (storageAppProperties.getCheckResourceFile()) {
            resourceFileId = userFacade.uploadAvatar(data.studentUserId
                    , ClassPathResourceLoader.getResourceAsStream("test.jpg"), new MockFormDataContentDisposition("file", "测试图片.jpg"), 100, 100, 100);
        }
        userFacade.setAvatar(data.studentUserId, resourceFileId);

        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        request.setRealName("Stu_" + StringUtils.getRandomString(8));
        userFacade.updateUserInfo(data.studentUserId, request);
    }

    @Test(groups = {Group.PARENT_REGISTER})
    public void test_Parent_Register() {
        data.parentUserId = userGenerator.registerParent();
        notNull(data.parentUserId);
    }

    @Test(dependsOnGroups = {Group.PARENT_REGISTER})
    public void test_Parent_updateUserInfo() {
        Long resourceFileId = 1L;
        if (storageAppProperties.getCheckResourceFile()) {
            resourceFileId = userFacade.uploadAvatar(data.parentUserId
                    , ClassPathResourceLoader.getResourceAsStream("test.jpg"), new MockFormDataContentDisposition("file", "测试图片.jpg"), 100, 100, 100);
        }
        userFacade.setAvatar(data.parentUserId, resourceFileId);

        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        request.setRealName("Parent_" + StringUtils.getRandomString(8));
        userFacade.updateUserInfo(data.parentUserId, request);
    }

    @Test(groups = {Group.BEFORE_PARENT_ADD_CHILD}, dependsOnGroups = {Group.PARENT_REGISTER})
    public void test_Parent_getChildren_befoerAddChild() {
        parentFacade.getChildren(data.parentUserId);
    }

    @Test(groups = {Group.PARENT_ADD_CHILD}, dependsOnGroups = {Group.BEFORE_PARENT_ADD_CHILD})
    public void test_Parent_addChild() {
        StudentDto studentDto = studentService.validateIsStudentByUserId(data.studentUserId);
        parentFacade.addChild(data.parentUserId, studentDto.getCode(), ParentChildRelation.FATHER);
    }

    @Test(groups = {Group.BEFORE_STUDENT_JOIN_CLASS}, dependsOnGroups = {Group.STUDENT_REGISTER})
    public void test_Student_getUser_beforeJoinClass() {
        userFacade.getUser(data.studentUserId);
    }

    @Test(groups = {Group.BEFORE_STUDENT_JOIN_CLASS}, dependsOnGroups = {Group.PARENT_ADD_CHILD})
    public void test_Parent_getChildren_beforeChildJoinClass() {
        parentFacade.getChildren(data.parentUserId);
    }

    @Test(groups = {Group.STUDENT_JOIN_CLASS}
            , dependsOnGroups = {Group.STUDENT_REGISTER, Group.BEFORE_STUDENT_JOIN_CLASS, Group.TEACHER_JOIN_CLASS})
    public void test_Student_JoinClass() {
        studentFacade.joinClass(data.studentUserId, data.classDto.getCode());
        StudentDto studentDto = studentService.validateIsStudentByUserId(data.studentUserId);
        logger.info(JsonUtils.toJson(studentDto));
    }

    @Test(dependsOnGroups = {Group.PARENT_ADD_CHILD, Group.STUDENT_JOIN_CLASS})
    public void test_Parent_getChild_afterChildJoinClass() {
        parentFacade.getChild(data.parentUserId, data.studentUserId);
    }

    @Test(dependsOnGroups = {Group.PARENT_ADD_CHILD, Group.STUDENT_JOIN_CLASS})
    public void test_Parent_getChildren_afterChildJoinClass() {
        parentFacade.getChildren(data.parentUserId);
    }

    @Test(dependsOnGroups = {Group.STUDENT_JOIN_CLASS})
    public void test_Student_getCourseAssignments() {
        studentFacade.getCourseAssignments(data.studentUserId);
    }

    @Test(dependsOnGroups = {Group.PARENT_ADD_CHILD, Group.STUDENT_JOIN_CLASS})
    public void test_Parent_getCourseAssignments() {
        parentFacade.getCourseAssignments(data.parentUserId, data.studentUserId);
    }

    @Test(groups = {Group.BEFORE_TEACHER_SET_COURSE_SCHEDULE}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS})
    public void test_Student_getCourseSchedule_beforeSet() {
        studentFacade.getCourseSchedule(data.studentUserId);
    }

    @Test(groups = {Group.BEFORE_TEACHER_SET_COURSE_SCHEDULE}, dependsOnGroups = {Group.PARENT_ADD_CHILD, Group.STUDENT_JOIN_CLASS})
    public void test_Parent_getCourseSchedule_beforeSet() {
        parentFacade.getCourseSchedule(data.parentUserId, data.studentUserId);
    }

    @Test(groups = {Group.TEACHER_SET_COURSE_SCHEDULE}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.BEFORE_TEACHER_SET_COURSE_SCHEDULE})
    public void test_Teacher_setCourseSchedule() {
        Random random = new Random();
        random.nextInt(4);
        List<SetCourseScheduleItem> params = Lists.newArrayList();
        for (int i = 1; i< 6; i++) {
            for (int j = 1; j< 9; j++) {
                SetCourseScheduleItem param = new SetCourseScheduleItem();
                param.setCourseId(Long.valueOf(random.nextInt(3) + 1));
                param.setDayOfWeek(i);
                param.setLessonOfDay(j);
                params.add(param);
            }
        }

        teacherFacade.setCourseSchedule(data.headTeacherUserId, data.classId, params);
    }

    @Test(groups = {Group.TEACHER_SET_COURSE_SCHEDULE}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.BEFORE_TEACHER_SET_COURSE_SCHEDULE}
            , expectedExceptions = {BizLogicException.class})
    public void test_Teacher_setCourseSchedule_NormalTeacher() {
        List<SetCourseScheduleItem> params = Lists.newArrayList();
        for (int i = 1; i< 4; i++) {
            SetCourseScheduleItem param = new SetCourseScheduleItem();
            param.setCourseId(Long.valueOf(i));
            param.setDayOfWeek(i);
            param.setLessonOfDay(i);
            params.add(param);
        }
        teacherFacade.setCourseSchedule(data.teacherUserId, data.classId, params);
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_COURSE_SCHEDULE})
    public void test_Student_getCourseSchedule_afterSet() {
        studentFacade.getCourseSchedule(data.studentUserId);
    }

    @Test(dependsOnGroups = {Group.PARENT_ADD_CHILD, Group.TEACHER_SET_COURSE_SCHEDULE})
    public void test_Parent_getCourseSchedule_afterSet() {
        parentFacade.getCourseSchedule(data.parentUserId, data.studentUserId);
    }

    @Test(groups = {Group.BEFORE_ADMIN_CREATE_QUIZ}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS})
    public void test_Student_getRecentQuizes_beforeCreate() {
        studentFacade.getRecentQuizes(data.studentUserId);
    }

    @Test(groups = {Group.BEFORE_ADMIN_CREATE_QUIZ}, dependsOnGroups = {Group.PARENT_ADD_CHILD, Group.STUDENT_JOIN_CLASS})
    public void test_Parent_getRecentQuizes_beforeCreate() {
        parentFacade.getCourseSchedule(data.parentUserId, data.studentUserId);
    }

    @Test(groups = {Group.BEFORE_ADMIN_CREATE_QUIZ}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.STUDENT_JOIN_CLASS})
    public void test_Teacher_getQuizesOfClass() {
        teacherFacade.getQuizOfClassByTeacher(data.headTeacherUserId, data.classId);
    }

    @Test(groups = {Group.BEFORE_ADMIN_CREATE_QUIZ}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.STUDENT_JOIN_CLASS})
    public void test_Teacher_getQuizOfClassByCourse() {
        teacherFacade.getQuizOfClassByCourse(data.headTeacherUserId, data.classId, data.headTeacherCourseId, new QueryPaging());
    }

    @Test(groups = {Group.BEFORE_ADMIN_CREATE_QUIZ}, dependsOnGroups = {Group.ADMIN_REGISTER, Group.STUDENT_JOIN_CLASS})
    public void test_Admin_getAllCourses() {
        teacherFacade.getAllCourses();
    }

    @Test(groups = {Group.BEFORE_ADMIN_CREATE_QUIZ}, dependsOnGroups = {Group.ADMIN_REGISTER, Group.STUDENT_JOIN_CLASS})
    public void test_Admin_getQuizList() {
        teacherFacade.getQuizList(data.schoolAdminUserId, new QueryPaging());
    }

    @Test(groups = {Group.ADMIN_CREATE_QUIZ}, dependsOnGroups = {Group.BEFORE_ADMIN_CREATE_QUIZ})
    public void test_Admin_createQuiz() {
        CourseDto courseDto = courseService.validateCoursesExist(data.headTeacherCourseId);
        CreateQuizRequest request = new CreateQuizRequest();
        request.setEnrolYear(data.enrolYear);
        request.setName(DataUtils.getQuizName(courseDto));
        request.setCategory(QuizCategory.FinalExam.value());
        request.setCourseId(data.headTeacherCourseId);
        request.setStudyYear(data.studyYear);
        request.setStudyTerm(data.studyTerm);
        request.setStudyMonth(0);
        request.setStartTime(new Date());
        request.setEndTime(new Date());
        QuizDto result = teacherFacade.createQuiz(data.schoolAdminUserId, request);
        data.quizId = result.getId();
    }

    @Test(dependsOnGroups = {Group.ADMIN_CREATE_QUIZ})
    public void test_Admin_getQuizList_afterCreateQuiz() {
        teacherFacade.getQuizList(data.schoolAdminUserId, new QueryPaging());
    }

    @Test(dependsOnGroups = {Group.ADMIN_CREATE_QUIZ})
    public void test_Student_getRecentQuizes_afterCreate() {
        studentFacade.getRecentQuizes(data.studentUserId);
    }

    @Test(dependsOnGroups = {Group.PARENT_ADD_CHILD, Group.ADMIN_CREATE_QUIZ})
    public void test_Parent_getRecentQuizes_afterCreate() {
        parentFacade.getCourseSchedule(data.parentUserId, data.studentUserId);
    }

    @Test(dependsOnGroups = {Group.ADMIN_CREATE_QUIZ})
    public void test_Teacher_getQuizesOfClass_afterCreate() {
        teacherFacade.getQuizOfClassByTeacher(data.headTeacherUserId, data.classId);
    }

    @Test(dependsOnGroups = {Group.ADMIN_CREATE_QUIZ})
    public void test_Teacher_getQuizOfClassByCourse_afterCreate() {
        teacherFacade.getQuizOfClassByCourse(data.headTeacherUserId, data.classId, data.headTeacherCourseId, new QueryPaging());
    }

    @Test(groups = {Group.BEFORE_TEACHER_SET_QUIZ_SCORE}, dependsOnGroups = {Group.ADMIN_CREATE_QUIZ})
    public void test_Teacher_getQuizScoreOfClass() {
        teacherFacade.getQuizScoreOfClass(data.headTeacherUserId, data.quizId, data.classId);
    }

    @Test(groups = {Group.TEACHER_SET_QUIZ_SCORE}, dependsOnGroups = {Group.BEFORE_TEACHER_SET_QUIZ_SCORE})
    public void test_Teacher_setQuizScore() {
        StudentDto studentDto = studentService.validateIsStudentByUserId(data.studentUserId);
        Map<Long, Float> scoreMap = Maps.newHashMap();
        scoreMap.put(studentDto.getId(), 99.5f);
        teacherFacade.setQuizScore(data.headTeacherUserId, data.quizId, data.classId, scoreMap);
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_QUIZ_SCORE})
    public void test_Teacher_getQuizScoreOfClass_AftetSetScore() {
        teacherFacade.getQuizScoreOfClass(data.headTeacherUserId, data.quizId, data.classId);
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_QUIZ_SCORE})
    public void test_Student_getQuizScoreReport() {
        studentFacade.getQuizScoreReport(data.studentUserId, data.studyYear, data.studyTerm, QuizCategory.FinalExam.value());
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_QUIZ_SCORE})
    public void test_Parent_getQuizScoreReport() {
        parentFacade.getQuizScoreReport(data.parentUserId,
                data.studentUserId, data.studyYear, data.studyTerm, QuizCategory.FinalExam.value());
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_QUIZ_SCORE})
    public void test_Student_getCourseScoreReport() {
        studentFacade.getCourseScoreReport(data.studentUserId, 1L, data.studyYear, QuizCategory.FinalExam.value());
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_QUIZ_SCORE})
    public void test_Parent_getCourseScoreReport() {
        parentFacade.getCourseScoreReport(data.parentUserId,
                data.studentUserId, 1L, data.studyYear, QuizCategory.FinalExam.value());
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_QUIZ_SCORE})
    public void test_Student_getCourseScoreTrends() {
        studentFacade.getCourseScoreTrends(data.studentUserId, 1L, data.studyYear);
    }

    @Test(dependsOnGroups = {Group.TEACHER_SET_QUIZ_SCORE})
    public void test_Parent_getCourseScoreTrends() {
        parentFacade.getCourseScoreTrends(data.parentUserId, data.studentUserId, 1L, data.studyYear);
    }

    @Test(groups = {Group.ADMIN_CREATE_SCHOOL_ANNOUNCEMENT}, dependsOnGroups = {Group.TEACHER_REGISTER})
    public void test_Admin_createSchoolAnnouncement() {
        CreateAnnouncementRequest request = new CreateAnnouncementRequest();
        request.setType(AnnouncementType.SCHOOL);
        String postfix = DataUtils.getRandomPostfix();
        request.setTitle("公告_" + postfix);
        request.setContent("公告内容_" + postfix);
        teacherFacade.createSchoolAnnouncement(data.schoolAdminUserId, request);
    }

    @Test(dependsOnGroups = {Group.ADMIN_CREATE_SCHOOL_ANNOUNCEMENT, Group.STUDENT_JOIN_CLASS})
    public void test_Student_getAnnouncements() {
        studentFacade.getAnnouncements(data.studentUserId, new QueryPaging());
    }

    @Test(dependsOnGroups = {Group.ADMIN_CREATE_SCHOOL_ANNOUNCEMENT, Group.STUDENT_JOIN_CLASS})
    public void test_Parent_getAnnouncements() {
        parentFacade.getAnnouncements(data.parentUserId, data.studentUserId, new QueryPaging());
    }

    @Test(groups = {Group.TEACHER_CREATE_COURSE_MATERIAL}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS})
    public void test_Teacher_createCourseMaterial_NoAttachment() {
        CreateCourseMaterialRequest request = new CreateCourseMaterialRequest();
        request.setClassId(data.classId);
        request.setCourseId(data.headTeacherCourseId);
        request.setType(CourseMaterialType.COURSEWARE);
        request.setName("CourseMaterial_" + StringUtils.getRandomString(16));
        request.setDescription("CourseMaterial_Des_" + StringUtils.getRandomString(16));
        teacherFacade.createCourseMaterial(data.headTeacherUserId, request);
    }

    @Test(groups = {Group.TEACHER_CREATE_COURSE_MATERIAL}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS})
    public void test_Teacher_createCourseMaterial_WithAttachment() throws UnsupportedEncodingException {
        Long resourceFileId = 1L;
        if (storageAppProperties.getCheckResourceFile()) {
            resourceFileId = teacherFacade.uploadCourseMaterial(data.headTeacherUserId
                    , ClassPathResourceLoader.getResourceAsStream("test.jpg"), new MockFormDataContentDisposition("file"
                    , new String("测试图片.jpg".getBytes("UTF-8"), "ISO-8859-1")));
        }

        CreateCourseMaterialRequest request = new CreateCourseMaterialRequest();
        request.setClassId(data.classId);
        request.setCourseId(data.headTeacherCourseId);
        request.setType(CourseMaterialType.COURSEWARE);
        request.setName("CourseMaterial_" + StringUtils.getRandomString(16));
        request.setDescription("CourseMaterial_Des_" + StringUtils.getRandomString(16));
        request.setResourceFileId(resourceFileId);
        data.courseMaterialId = teacherFacade.createCourseMaterial(data.headTeacherUserId, request);
    }

    @Test(groups = {Group.BEFORE_TEACHER_UPDATE_COURSE_MATERIAL}, dependsOnGroups = {Group.TEACHER_CREATE_COURSE_MATERIAL})
    public void test_Teacher_getCourseMaterialDetail() {
        teacherFacade.getCourseMaterialDetail(data.headTeacherUserId, data.courseMaterialId);
    }

    @Test(groups = {Group.BEFORE_TEACHER_UPDATE_COURSE_MATERIAL}, dependsOnGroups = {Group.TEACHER_CREATE_COURSE_MATERIAL})
    public void test_Teacher_getCourseMaterials() {
        teacherFacade.getCourseMaterials(data.headTeacherUserId, new QueryPaging());
    }

    @Test(groups = {Group.BEFORE_TEACHER_UPDATE_COURSE_MATERIAL}, dependsOnGroups = {Group.TEACHER_CREATE_COURSE_MATERIAL})
    public void test_Student_getCourseMaterials() {
        studentFacade.getCourseMaterials(data.studentUserId, data.headTeacherCourseId, CourseMaterialType.COURSEWARE, null, null, new QueryPaging());
    }

    @Test(groups = {Group.BEFORE_TEACHER_UPDATE_COURSE_MATERIAL}, dependsOnGroups = {Group.TEACHER_CREATE_COURSE_MATERIAL})
    public void test_Parent_getCourseMaterials() {
        parentFacade.getCourseMaterials(data.parentUserId, data.studentUserId, data.headTeacherCourseId, CourseMaterialType.COURSEWARE, null, null, new QueryPaging());
    }

    @Test(groups = {Group.TEACHER_UPDATE_COURSE_MATERIAL}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.BEFORE_TEACHER_UPDATE_COURSE_MATERIAL})
    public void test_Teacher_updateCourseMaterial_WithAttachment() {
        UpdateCourseMaterialRequest request = new UpdateCourseMaterialRequest();
        request.setId(data.courseMaterialId);
        request.setClassId(data.classId);
        request.setCourseId(data.headTeacherCourseId);
        request.setType(CourseMaterialType.COURSEWARE);
        request.setName("CourseMaterial_Updated_" + StringUtils.getRandomString(16));
        request.setDescription("CourseMaterial_Des_Updated_" + StringUtils.getRandomString(16));
        teacherFacade.updateCourseMaterial(data.headTeacherUserId, request);
    }

    @Test(groups = {Group.BEFORE_TEACHER_CREATE_HOMEWORK}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS})
    public void test_Student_getHomeworkReviewStatus_beforeCreateHomework() {
        studentFacade.getHomeworkReviewStatus(data.studentUserId, new Date());
    }

    @Test(groups = {Group.BEFORE_TEACHER_CREATE_HOMEWORK}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS})
    public void test_Parent_getHomeworkReviewStatus_beforeCreateHomework() {
        parentFacade.getHomeworkReviewStatus(data.parentUserId, data.studentUserId, new Date());
    }

    @Test(groups = {Group.TEACHER_CREATE_HOMEWORK}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.BEFORE_TEACHER_CREATE_HOMEWORK})
    public void test_Teacher_createHomework_NoAttachment() {
        CourseDto courseDto = courseService.validateCoursesExist(data.headTeacherCourseId);
        CreateHomeworkRequest request = new CreateHomeworkRequest();
        request.setClassId(data.classId);
        request.setCourseId(data.headTeacherCourseId);
        request.setName(DataUtils.getHomeworkName(courseDto));
        request.setDescription(DataUtils.getHomeworkDesc(courseDto));
        request.setStudyDate(new Date());
        HomeworkDto homeworkDto = teacherFacade.createHomework(data.headTeacherUserId, request);
        data.homeworkId = homeworkDto.getId();
    }

    @Test(groups = {Group.TEACHER_UPDATE_HOMEWORK}, dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.TEACHER_CREATE_HOMEWORK})
    public void test_Teacher_udpateHomework_NoAttachment() {
        CourseDto courseDto = courseService.validateCoursesExist(data.headTeacherCourseId);
        UpdateHomeworkRequest request = new UpdateHomeworkRequest();
        request.setId(data.homeworkId);
        request.setClassId(data.classId);
        request.setCourseId(data.headTeacherCourseId);
        request.setName("Updated_" + DataUtils.getHomeworkName(courseDto));
        request.setDescription("Updated_" + DataUtils.getHomeworkDesc(courseDto));
        request.setStudyDate(new Date());
        teacherFacade.updateHomework(data.headTeacherUserId, request);
    }

    @Test(groups = {Group.BEFORE_PARENT_REVIEW_HOMEWORK}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS, Group.TEACHER_CREATE_HOMEWORK})
    public void test_Student_getHomeworkReviewStatus_beforeReview() {
        studentFacade.getHomeworkReviewStatus(data.studentUserId, new Date());
    }

    @Test(groups = {Group.BEFORE_PARENT_REVIEW_HOMEWORK}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS, Group.TEACHER_CREATE_HOMEWORK})
    public void test_Parent_getHomeworkReviewStatus_beforeReview() {
        parentFacade.getHomeworkReviewStatus(data.parentUserId, data.studentUserId, new Date());
    }

    // TODO teacher getReviewStatus

    @Test(groups = {Group.PARENT_REVIEW_HOMEWORK}
            , dependsOnGroups = {Group.STUDENT_JOIN_CLASS, Group.TEACHER_CREATE_HOMEWORK, Group.BEFORE_PARENT_REVIEW_HOMEWORK})
    public void test_Parent_reviewHomework() {
        parentFacade.reviewHomeworkByCourse(data.parentUserId, data.studentUserId, data.headTeacherCourseId, new Date());
    }

    @Test(dependsOnGroups = {Group.PARENT_REVIEW_HOMEWORK})
    public void test_Student_getHomeworkReviewStatus_afterReview() {
        studentFacade.getHomeworkReviewStatus(data.studentUserId, new Date());
    }

    @Test(dependsOnGroups = {Group.PARENT_REVIEW_HOMEWORK})
    public void test_Parent_getHomeworkReviewStatus_afterReview() {
        parentFacade.getHomeworkReviewStatus(data.parentUserId, data.studentUserId, new Date());
    }

    @Test(groups = {Group.STUDENT_SEND_SNEAKING_MSG}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS})
    public void test_Student_sendSneakingMessage() {
        studentFacade.sendSneakingMessage(data.studentUserId, "SnkMsg_" + StringUtils.getRandomString(16));
    }

    @Test(dependsOnGroups = {Group.TEACHER_JOIN_CLASS, Group.STUDENT_SEND_SNEAKING_MSG})
    public void test_Teacher_getSneakingMessages() {
        teacherFacade.getSneakingMessages(data.headTeacherUserId, new QueryPaging());
    }

    @Test(groups = {Group.STUDENT_SEND_SOS_MSG}, dependsOnGroups = {Group.STUDENT_JOIN_CLASS, Group.PARENT_ADD_CHILD})
    public void test_Student_sendSosMessage() {
        CreateSosMessageRequest request = new CreateSosMessageRequest();
        request.setContent("Sos_" + StringUtils.getRandomString(16));
        request.setLatitude(1.1f);
        request.setLongitude(2.2f);
        studentFacade.sendSosMessage(data.studentUserId, request);
    }

}
