package com.dianxian.testframework.biz;

import com.dianxian.core.exception.InvalidParamException;
import com.dianxian.school.dao.CourseTeacherDtoMapper;
import com.dianxian.school.domain.Student;
import com.dianxian.school.dto.*;
import com.dianxian.school.facade.ParentFacade;
import com.dianxian.school.facade.StudentFacade;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.school.request.teacher.CreateQuizRequest;
import com.dianxian.school.request.teacher.JoinClassRequest;
import com.dianxian.school.service.*;
import com.dianxian.testframework.DataUtils;
import com.dianxian.testframework.ScenarioContext;
import com.dianxian.testframework.request.AddClassesToSchoolRequest;
import com.dianxian.testframework.request.AddStudentsToClassRequest;
import com.dianxian.testframework.request.GenerateTestScenarioRequest;
import com.dianxian.user.domain.Roles;
import com.dianxian.user.dto.ParentChildRelation;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.TeacherDto;
import com.dianxian.user.service.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by xuwenhao on 2016/6/10.
 */
@Component
public class ScenarioGenerator {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserGenerator userGenerator;
    @Autowired
    SchoolGenerator schoolGenerator;
    @Autowired
    TeacherFacade teacherFacade;
    @Autowired
    ParentFacade parentFacade;
    @Autowired
    StudentFacade studentFacade;
    @Autowired
    StudentService studentService;
    @Autowired
    ClassService classService;
    @Autowired
    SchoolService schoolService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    CourseService courseService;
    @Autowired
    UserService userService;
    @Autowired
    CourseTeacherDtoMapper courseTeacherDtoMapper;

    /**
     * 创建一个学校，包含一个学校管理员，1个班级，1个任课老师，一个学生，一个家长。
     * @return
     */
    public ScenarioContext defaultScenario() {
        ScenarioContext data = new ScenarioContext();
        SchoolDto schoolDto = schoolGenerator.createSchool();
        data.schoolId = schoolDto.getId();
        data.schoolCode = schoolDto.getCode();

        data.schoolAdminUserId = userGenerator.registerSchoolAdmin(schoolDto.getId());
        data.headTeacherUserId = userGenerator.registerTeacher();

        teacherFacade.joinSchool(data.headTeacherUserId, data.schoolCode, data.schoolId);

        ClassDto classDto = schoolGenerator.joinClass(data, data.headTeacherUserId, data.headTeacherCourseId);
        data.classDto = classDto;
        data.classId = classDto.getId();

        data.studentUserId = userGenerator.registerStudent();
        data.parentUserId = userGenerator.registerParent();

        StudentDto studentDto = studentService.validateIsStudentByUserId(data.studentUserId);
        parentFacade.addChild(data.parentUserId, studentDto.getCode(), ParentChildRelation.FATHER);

        return data;
    }

    public List<Long> addTeachersToSchool(Long schoolId, int teachersCount) {
        SchoolDto schoolDto = schoolService.validateSchoolExists(schoolId);
        List<Long> teacherUserIds = Lists.newArrayList();
        for (int i = 0; i < teachersCount; i++) {
            Long teacherUserId = userGenerator.registerTeacher();
            teacherUserIds.add(teacherUserId);
            teacherFacade.joinSchool(teacherUserId, schoolDto.getCode(), schoolDto.getId());
        }

        return teacherUserIds;
    }

    public List<Long> addClassesToSchool(AddClassesToSchoolRequest request) {
        List<Long> teacherUserIds = addTeachersToSchool(request.getSchoolId(), request.getTeachersCount());
        List<Long> classIds = Lists.newArrayList();
        for (int classNumber = 1; classNumber <= request.getClassesCount(); classNumber++) {
            long courseId = 1;
            for (Long teacherUserId : teacherUserIds) {
                ScenarioContext data = new ScenarioContext();
                data.enrolYear = request.getEnrolYear();
                data.classNumber = classNumber;

                ClassDto classDto = schoolGenerator.joinClass(data, teacherUserId, courseId);
                classIds.add(classDto.getId());
                courseId++;
            }
        }
        return classIds;
    }

    public List<Long> addStudentsToClass(AddStudentsToClassRequest request) {
        List<Long> studentUserIds = Lists.newArrayList();
        ClassDto classDto = classService.validateClassExists(request.getClassId());
        int parentsCount = 0;
        if (0 != request.getMaxParentsCount()) {
            if (request.getMinParentsCount().equals(request.getMaxParentsCount())) {
                parentsCount = request.getMinParentsCount();
            } else {
                Random random = new Random();
                parentsCount = random.nextInt(request.getMaxParentsCount() + 1);
                parentsCount = Math.max(parentsCount, request.getMinParentsCount());
            }
        }
        for (int i = 0; i < request.getStudentsCount(); i++) {
            Long studentUserId = userGenerator.registerStudent();
            studentUserIds.add(studentUserId);
            if (0 == parentsCount) {
                studentFacade.joinClass(studentUserId, classDto.getCode());
            } else if (1 == parentsCount) {
                userGenerator.addParentToStudent(studentUserId);
                studentFacade.joinClass(studentUserId, classDto.getCode());
            } else {
                studentFacade.joinClass(studentUserId, classDto.getCode());
                for (int j = 0; j < parentsCount; j++) {
                    userGenerator.addParentToStudent(studentUserId);
                }
            }
        }
        return studentUserIds;
    }

    public void generateScenario(GenerateTestScenarioRequest request) {
        // 创建学校
        Long schoolId = request.getSchoolId();
        if (null == request.getSchoolId()) {
            SchoolDto schoolDto = schoolGenerator.createSchool();
            schoolId = schoolDto.getId();
            request.setSchoolId(schoolId);
        } else {
            schoolService.validateSchoolExists(schoolId);
        }

        // 创建老师和班级
        AddClassesToSchoolRequest addClassesToSchoolRequest = new AddClassesToSchoolRequest();
        BeanUtils.copyProperties(request, addClassesToSchoolRequest);
        List<Long> classIds = this.addClassesToSchool(addClassesToSchoolRequest);
        Map<Long, List<CourseTeacherDto>> classCourseAssignmentsMap = Maps.newLinkedHashMap();
        for (Long classId : classIds) {
            List<CourseTeacherDto> courseTeacherDtos = courseService.getAssignedCoursesOfClass(classId);
            classCourseAssignmentsMap.put(classId, courseTeacherDtos);
        }

        // 创建学生和家长
        List<Long> studentUserIds = Lists.newArrayList();
        Map<Long, List<Long>> classStudentMap = Maps.newLinkedHashMap();
        for (Long classId : classIds) {
            AddStudentsToClassRequest addStudentsToClassRequest = new AddStudentsToClassRequest();
            BeanUtils.copyProperties(request, addClassesToSchoolRequest);
            addStudentsToClassRequest.setClassId(classId);

            List<Long> newStudentUserIds = this.addStudentsToClass(addStudentsToClassRequest);
            studentUserIds.addAll(newStudentUserIds);
            classStudentMap.put(classId, newStudentUserIds);
        }

        // 创建班级公告
        for (Long classId : classIds) {
            schoolGenerator.addClassAnnouncement(classId);
        }

        // 创建课件
        for (Long classId : classIds) {
            List<CourseTeacherDto> courseTeacherDtos = classCourseAssignmentsMap.get(classId);
            for (CourseTeacherDto courseTeacherDto : courseTeacherDtos) {
                schoolGenerator.addCourseMaterial(classId, courseTeacherDto.getCourseId());
            }
        }

        // 创建作业
        for (Long classId : classIds) {
            List<CourseTeacherDto> courseTeacherDtos = classCourseAssignmentsMap.get(classId);
            for (CourseTeacherDto courseTeacherDto : courseTeacherDtos) {
                schoolGenerator.addHomework(classId, courseTeacherDto.getCourseId());
            }
        }

        // 创建学校公告
        schoolGenerator.addSchoolAnnouncement(schoolId);

        // 创建考试
        List<Long> adminTeacherIds = teacherService.getTeachersOfSchoolByRole(schoolId, Roles.SCHOOL_ADMIN);
        Long schoolAdminUserId = null;
        if (CollectionUtils.isEmpty(adminTeacherIds)) {
            schoolAdminUserId = userGenerator.registerSchoolAdmin(schoolId);
        } else {
            Long schoolAdminTeacherId = adminTeacherIds.get(0);
            TeacherDto schoolAdminTeacherDto = teacherService.validateIsTeacherById(schoolAdminTeacherId);
            schoolAdminUserId = schoolAdminTeacherDto.getUserId();
        }

        List<Long> quizIds = Lists.newArrayList();
        List<QuizDto> quizDtos = Lists.newArrayList();
        long MAX_COURSE_ID = request.getTeachersCount();
        for (long i = 1; i <= MAX_COURSE_ID; i++) {
            CourseDto courseDto = courseService.validateCoursesExist(i);
            CreateQuizRequest createQuizRequest = new CreateQuizRequest();
            createQuizRequest.setEnrolYear(request.getEnrolYear());
            createQuizRequest.setName(DataUtils.getQuizName(courseDto));
            createQuizRequest.setCategory(QuizCategory.FinalExam.value());
            createQuizRequest.setCourseId(i);
            createQuizRequest.setStudyMonth(0);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, (int) i);
            createQuizRequest.setStartTime(calendar.getTime());
            createQuizRequest.setEndTime(calendar.getTime());
            QuizDto quizDto = teacherFacade.createQuiz(schoolAdminUserId, createQuizRequest);
            quizIds.add(quizDto.getId());
            quizDtos.add(quizDto);
        }

        // 添加成绩
        for (QuizDto quizDto : quizDtos) {
            for (Long classId : classIds) {
                CourseTeacherDto courseTeacherDto = courseTeacherDtoMapper.getAssignedCourseOfClassByCourse(classId, quizDto.getCourseId());
                if (null == courseTeacherDto) {
                    logger.info(String.format("Course %s is not assigned to any teacher in class %s.", quizDto.getCourseId(), classId));
                    continue;
                }
                Map<Long, Float> scoreMap = Maps.newHashMap();
                List<StudentDto> studentDtos = userService.getStudentsOfClass(classId);
                for (StudentDto studentDto : studentDtos) {
                    scoreMap.put(studentDto.getId(), DataUtils.getRandomScore());
                }
                teacherFacade.setQuizScore(courseTeacherDto.getTeacherId(), quizDto.getId(), classId, scoreMap);
            }
        }
    }
}
