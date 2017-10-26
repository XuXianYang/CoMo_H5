package com.dianxian;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.school.consts.Comments;
import com.dianxian.school.dto.QuizCategory;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.school.request.RequestDate;
import com.dianxian.testframework.ScenarioContext;
import com.dianxian.testframework.biz.ScenarioGenerator;
import com.dianxian.testframework.biz.SchoolGenerator;
import com.dianxian.testframework.biz.UserGenerator;
import com.dianxian.testframework.request.AddClassesToSchoolRequest;
import com.dianxian.testframework.request.AddStudentsToClassRequest;
import com.dianxian.testframework.request.GenerateTestScenarioRequest;
import com.dianxian.user.domain.User;
import com.dianxian.user.facade.UserFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.BeanParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.List;

/**
 * Created by xuwenhao on 2016/7/5.
 */
@Api(value="/test")
@Path("test")
public class TestResource {
    @Autowired
    ScenarioGenerator scenarioGenerator;
    @Autowired
    SchoolGenerator schoolGenerator;
    @Autowired
    UserGenerator userGenerator;
    @Autowired
    TeacherFacade teacherFacade;
    @Autowired
    UserFacade userFacade;

    @ApiOperation(value = "创建学校管理员")
    @POST
    @Path("user/registerSchoolAdmin")
    public GenericResponse<User> registerSchoolAdmin(
            @ApiParam(value = Comments.SCHOOL_ID)
            @FormParam("schoolId")
            Long schoolId
    ) {
        Long userId = userGenerator.registerSchoolAdmin(schoolId);
        User user = userFacade.getUser(userId);
        return ResponseBuilder.buildSuccessResponse(user);
    }

    @ApiOperation(value = "注册一个家长")
    @POST
    @Path("user/registerParent")
    public GenericResponse<User> registerParent() {
        Long userId = userGenerator.registerParent();
        User user = userFacade.getUser(userId);
        return ResponseBuilder.buildSuccessResponse(user);
    }

    @ApiOperation(value = "注册一个老师")
    @POST
    @Path("user/registerTeacher")
    public GenericResponse<User> registerTeacher() {
        Long userId = userGenerator.registerTeacher();
        User user = userFacade.getUser(userId);
        return ResponseBuilder.buildSuccessResponse(user);
    }

    @ApiOperation(value = "注册一个学生")
    @POST
    @Path("user/registerStudent")
    public GenericResponse<User> registerStudent() {
        Long userId = userGenerator.registerStudent();
        User user = userFacade.getUser(userId);
        return ResponseBuilder.buildSuccessResponse(user);
    }

    @ApiOperation(value = "给学生添加一个家长")
    @POST
    @Path("user/addParentToStudent")
    public GenericResponse<User> addParentToStudent(
            @ApiParam(value = "学生的userId")
            @FormParam("studentUserId")
            @NotNull
            Long studentUserId) {
        Long userId = userGenerator.addParentToStudent(studentUserId);
        User user = userFacade.getUser(userId);
        return ResponseBuilder.buildSuccessResponse(user);
    }

    @ApiOperation(value = "从班级中移出一个学生")
    @POST
    @Path("class/removeStudent")
    public GenericResponse<Object> addQuizFromStudent(
            @ApiParam(value = "学生的userId")
            @FormParam("studentUserId")
            Long studentUserId,
            @ApiParam(value = "学生的userName")
            @FormParam("studentUserName")
            String studentUserName) {
        schoolGenerator.revomeStudentFromClass(studentUserId, studentUserName);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "给学生添加一条考试信息")
    @POST
    @Path("quiz/addFromStudent")
    public GenericResponse<ScenarioContext> addQuizFromStudent(
            @ApiParam(value = "学生的userId")
            @FormParam("studentUserId")
            Long studentUserId,
            @ApiParam(value = "学生的userName")
            @FormParam("studentUserName")
            String studentUserName,
            @ApiParam(value = Comments.COURSE_ID + " 不指定就取班主任的课")
            @FormParam("courseId")
            Long courseId,
            @ApiParam(value = Comments.QUIZ_CATEGORY, allowableValues = QuizCategory.FINAL_EXAM + "," + QuizCategory.MID_TERM + "," + QuizCategory.MONTHLY)
            @FormParam("category")
            Integer quizCategory,
            @ApiParam(value = "学年")
            @FormParam("studyYear")
            Integer studyYear,
            @ApiParam(value = "学期")
            @FormParam("studyTerm")
            Integer studyTerm,
            @ApiParam(value = "月考的月份。如果类型不是月考可以不填")
            @FormParam("studyMonth")
            Integer studyMonth
    ) {
        ScenarioContext context = schoolGenerator.addQuizFromStudent(studentUserId, studentUserName, courseId
                , quizCategory, studyYear, studyTerm, studyMonth);
        return ResponseBuilder.buildSuccessResponse(context);
    }

    @ApiOperation(value = "给学生添加一条考试成绩.")
    @POST
    @Path("quiz/score/addFromStudent")
    public GenericResponse<ScenarioContext> addQuizScoreFromStudent(
            @ApiParam(value = "学生的userId")
            @FormParam("studentUserId")
            Long studentUserId,
            @ApiParam(value = "学生的userName")
            @FormParam("studentUserName")
            String studentUserName,
            @ApiParam(value = Comments.QUIZ_ID + " 如果不填则根据其他信息创建一个考试")
            @FormParam("quizId")
            Long quizId,
            @ApiParam(value = "成绩")
            @FormParam("score")
            Float score,
            @ApiParam(value = Comments.COURSE_ID + " 需要新建一次考试时用，不指定就取班主任的课")
            @FormParam("courseId")
            Long courseId,
            @ApiParam(value = Comments.QUIZ_CATEGORY, allowableValues = QuizCategory.FINAL_EXAM + "," + QuizCategory.MID_TERM + "," + QuizCategory.MONTHLY)
            @FormParam("category")
            Integer quizCategory,
            @ApiParam(value = "学年")
            @FormParam("studyYear")
            Integer studyYear,
            @ApiParam(value = "学期")
            @FormParam("studyTerm")
            Integer studyTerm,
            @ApiParam(value = "月考的月份。如果类型不是月考可以不填")
            @FormParam("studyMonth")
            Integer studyMonth
    ) {
        ScenarioContext context = schoolGenerator.addQuizScoreFromStudent(studentUserId, studentUserName, quizId, score
                , courseId, quizCategory, studyYear, studyTerm, studyMonth);
        return ResponseBuilder.buildSuccessResponse(context);
    }

    @POST
    @Path("homework/addFromStudent")
    public GenericResponse<ScenarioContext> addHomeworkFromStudent(
            @ApiParam(value = "学生的userId")
            @FormParam("studentUserId")
            Long studentUserId,
            @ApiParam(value = "学生的userName")
            @FormParam("studentUserName")
            String studentUserName,
            @ApiParam(value = Comments.COURSE_ID)
            @FormParam("courseId")
            Long courseId,
            @ApiParam(value = Comments.HOMEWORK_STUDY_DATE, example = RequestDate.DATE_FORMAT)
            @FormParam("studyDate")
            RequestDate studyDate,
            @ApiParam(value = Comments.ATTACHMENT_ID)
            @FormParam("resourceFileId")
            Long resourceFileId
    ) {
        ScenarioContext context = schoolGenerator.addHomeworkFromStudent(studentUserId, studentUserName, courseId, studyDate, resourceFileId);
        return ResponseBuilder.buildSuccessResponse(context);
    }

    @ApiOperation(value = "生成班级并分配老师")
    @POST
    @Path("scenario/addClassesToSchool")
    public GenericResponse<List<Long>> addClassesToSchool(@BeanParam AddClassesToSchoolRequest request) {
        List<Long> result = scenarioGenerator.addClassesToSchool(request);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "生成学生并添加到班级中")
    @POST
    @Path("scenario/addStudentsToClass")
    public GenericResponse<List<Long>> addStudentsToClass(@BeanParam AddStudentsToClassRequest request) {
        List<Long> result = scenarioGenerator.addStudentsToClass(request);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "生成复杂场景. 创建学校，老师和班级。向班级里加入学生和家长。创建公告，课程材料，作业等数据")
    @POST
    @Path("scenario/generateScenario")
    public GenericResponse<Object> generateScenario(@BeanParam GenerateTestScenarioRequest request) {
        scenarioGenerator.generateScenario(request);
        return ResponseBuilder.buildSuccessResponse();
    }

}
