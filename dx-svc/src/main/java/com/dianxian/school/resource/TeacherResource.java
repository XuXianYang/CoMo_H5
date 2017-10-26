package com.dianxian.school.resource;

import com.dianxian.core.resource.*;
import com.dianxian.core.server.BaseResource;
import com.dianxian.school.consts.Comments;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.*;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.school.request.teacher.*;
import io.swagger.annotations.*;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

/**
 * Created by xuwenhao on 2016/5/7.
 */
@Api(value="/teacher")
@Path("teacher")
public class TeacherResource extends BaseResource {
    @Autowired
    TeacherFacade teacherFacade;

    @ApiOperation(value = "已经加入的班级列表")
    @GET
    @Path("class/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<SchoolClassOfTeacher>> getJoinedClasses() {
        List<SchoolClassOfTeacher> result = teacherFacade.getJoinedClasses(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取班级的详细信息。包括学校和班主任信息")
    @GET
    @Path("class/detail")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<SchoolClassDetail> getSchoolClassDetail(
            @ApiParam(value = Comments.CLASS_ID)
            @QueryParam("classId")
            @NotNull
            Long classId
    ) {
        SchoolClassDetail result = teacherFacade.getSchoolClassDetail(getCurrentUserId(), classId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "加入学校")
    @ApiResponses(value = {
            @ApiResponse(code = ServiceCodes.SCHOOL_CODE_NOT_MATCH, message = "SCHOOL_CODE_NOT_MATCH"),
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.TEACHER_ALREADY_JOINED_SCHOOL, message = "TEACHER_ALREADY_JOINED_SCHOOL")})
    @POST
    @Path("school/join")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> joinSchool(
            @ApiParam(value = Comments.SCHOOL_CODE)
            @NotNull @FormParam("code") String code,
            @ApiParam(value = Comments.SCHOOL_ID)
            @NotNull @FormParam("id") Long id
            ) {
        teacherFacade.joinSchool(getCurrentUserId(), code, id);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "获取班级的任课信息")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.TEACHER_NOT_JOINED_SCHOOL, message = "TEACHER_NOT_JOINED_SCHOOL")})
    @GET
    @Path("class/course/assignment/")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseAssignment>> getCourseAssignments(
            @ApiParam(value = Comments.ENROL_YEAR)
            @NotNull @QueryParam("enrolYear") Integer enrolYear,
            @ApiParam(value = Comments.CLASS_NUMBER)
            @NotNull @QueryParam("classNumber") Integer classNumber) {
        List<CourseAssignment> result = teacherFacade.getCourseAssignments(getCurrentUserId(), enrolYear, classNumber);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取一个老师在指定的班级中所任教的课程列表")
    @GET
    @Path("class/course/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<Course>> getAssignedCoursesOfTeacherInClass(
            @ApiParam(value = Comments.CLASS_ID)
            @NotNull @QueryParam("classId") Long classId) {
        List<Course> courses = teacherFacade.getAssignedCoursesOfTeacherInClass(getCurrentUserId(), classId);
        return ResponseBuilder.buildSuccessResponse(courses);
    }

    @ApiOperation(value = "加入班级", notes = "加入班级的同时a会设置任教哪些课程，第一个加入到班级的老师将被设置为班主任。")
    @POST
    @Path("class/join")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<JoinClassResult> joinClass(@BeanParam JoinClassRequest request) {
        JoinClassResult result = teacherFacade.joinClass(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "设置班级的课程表")
    @POST
    @Path("class/course/schedule/set")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> setCourseSchedule(
            @ApiParam(value = "班级id")
            @FormParam("classId") @NotNull Long classId,
            @ApiParam(value = "课程表详细排期")
            @FormParam("courseSchedules") @NotEmpty @NotNull SetCourseScheduleItem.AsList courseSchedules) {
        teacherFacade.setCourseSchedule(getCurrentUserId(), classId, courseSchedules);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "[非接口] 只是为了在文档中添加数据类型的定义")
    @GET
    @Path("definition/CourseSchedule")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<SetCourseScheduleItem> setCourseSchedule() {
        return null;
    }

    @ApiOperation(value = "获取班级的课程表")
    @GET
    @Path("class/course/schedule")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseSchedule>> getCourseSchedule(
            @ApiParam(value = "班级id")
            @QueryParam("classId") @NotNull Long classId) {
        List<CourseSchedule> result = teacherFacade.getCourseSchedule(getCurrentUserId(), classId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取所有课程")
    @GET
    @Path("course/all")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<Course>> getAllCourses() {
        List<Course> result = teacherFacade.getAllCourses();
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "创建考试")
    @POST
    @Path("quiz/create")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<QuizDto> createQuiz(@BeanParam CreateQuizRequest request) {
        QuizDto quizDto = teacherFacade.createQuiz(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse(quizDto);
    }

    @ApiOperation(value = "编辑考试")
    @POST
    @Path("quiz/update")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> updateQuiz(@BeanParam UpdateQuizRequest request) {
        teacherFacade.updateQuiz(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "根据课程，获取一个班级的相关考试")
    @GET
    @Path("class/quiz/listByCourse")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseQuiz>> getQuizOfClassByCourse(
            @ApiParam(value = Comments.CLASS_ID)
            @QueryParam("classId") @NotNull
            Long classId,
            @ApiParam(value = Comments.COURSE_ID)
            @QueryParam("courseId") @NotNull
            Long courseId,
            @BeanParam
            QueryPaging queryPaging) {
        PagingInfo<CourseQuiz> result = teacherFacade.getQuizOfClassByCourse(getCurrentUserId(), classId, courseId, queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取一个班级某一场考试的成绩列表")
    @GET
    @Path("class/quiz/score/all")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<QuizScoreDto>> getQuizScoreOfClass(
            @ApiParam(value = Comments.QUIZ_ID)
            @QueryParam("quizId") @NotNull Long quizId,
            @ApiParam(value = Comments.CLASS_ID)
            @QueryParam("classId") @NotNull Long classId) {
        List<QuizScoreDto> result = teacherFacade.getQuizScoreOfClass(getCurrentUserId(), quizId, classId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "设置一个班级某一场考试的成绩列表")
    @POST
    @Path("class/quiz/score/set")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> setQuizScore(@BeanParam SetQuizScoreRequest request) {
        teacherFacade.setQuizScore(getCurrentUserId()
                , request.getQuizId(), request.getClassId(), request.getStudentScores());
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "创建公告")
    @POST
    @Path("announcement/create")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> createSchoolAnnouncement(@BeanParam CreateAnnouncementRequest request) {
        teacherFacade.createSchoolAnnouncement(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "公告列表")
    @POST
    @Path("announcement/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<AnnouncementDto>> getAnnouncementList(@ApiParam(value = Comments.CLASS_ID)
                                                                          @QueryParam("classId") @NotNull Long classId,@BeanParam QueryPaging queryPaging) {
        PagingInfo<AnnouncementDto> result = teacherFacade.getAnnouncementList(getCurrentUserId(), classId, queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "公告详情")
    @GET
    @Path("announcement/detail")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<AnnouncementDto> getAnnouncementList(
            @ApiParam(value = Comments.ANNOUNCEMENT_ID)
            @QueryParam("id")
            @NotNull Long id) {
        AnnouncementDto result = teacherFacade.getAnnouncementDetail(getCurrentUserId(), id);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "编辑公告")
    @POST
    @Path("announcement/update")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> updateCourseMaterial(@BeanParam UpdateAnnouncementRequest request) {
        teacherFacade.updateAnnouncement(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "上传课程材料附件")
    @POST
    @Path("course/material/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Long> uploadCourseMaterial(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader
    ) {
        Long resourceId = teacherFacade.uploadCourseMaterial(getCurrentUserId(), fileInputStream, contentDispositionHeader);
        return ResponseBuilder.buildSuccessResponse(resourceId);
    }

    @ApiOperation(value = "创建课程材料")
    @POST
    @Path("course/material/create")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> createCourseMaterial(@BeanParam CreateCourseMaterialRequest request) {
        teacherFacade.createCourseMaterial(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "课程材料列表")
    @GET
    @Path("course/material/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseMaterialManagementListItem>> getCourseMaterials(@BeanParam QueryPaging queryPaging) {
        PagingInfo<CourseMaterialManagementListItem> result = teacherFacade.getCourseMaterials(getCurrentUserId(), queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "编辑课程材料")
    @POST
    @Path("course/material/update")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> updateCourseMaterial(@BeanParam UpdateCourseMaterialRequest request) {
        teacherFacade.updateCourseMaterial(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "上传作业附件")
    @POST
    @Path("homework/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Long> uploadHomework(
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader
    ) {
        Long resourceId = teacherFacade.uploadHomework(getCurrentUserId(), fileInputStream, contentDispositionHeader);
        return ResponseBuilder.buildSuccessResponse(resourceId);
    }

    @ApiOperation(value = "布置作业")
    @POST
    @Path("homework/create")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> createHomework(@BeanParam CreateHomeworkRequest request) {
        teacherFacade.createHomework(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "编辑作业")
    @POST
    @Path("homework/update")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> updateHomework(@BeanParam UpdateHomeworkRequest request) {
        teacherFacade.updateHomework(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }
    @ApiOperation(value = "作业列表")
    @GET
    @Path("homework/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<HomeworkDto>> getHomeworkList( @ApiParam(value = "班级id")
                                                                   @QueryParam("classId") @NotNull Long classId,@BeanParam QueryPaging queryPaging) {
        PagingInfo<HomeworkDto> result = teacherFacade.getHomeworkList(getCurrentUserId(), classId ,queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "作业审阅状态")
    @GET
    @Path("homework/reviewDetail")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<HomeworkReviewDetailByCourseDto> getHomeworkReviewDetail( @ApiParam(value = "作业Id")
                                                               @QueryParam("homeworkId") @NotNull Long homeworkId,
                                                                                     @ApiParam(value = "班级Id")
                                                                                     @QueryParam("classId") @NotNull Long classId) {
        HomeworkReviewDetailByCourseDto result = teacherFacade.getHomeworkReviewDetail(getCurrentUserId(), homeworkId, classId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "小报告列表")
    @GET
    @Path("sneaking/msg/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<SneakingMessageListItem>> getSneakingMessages(@BeanParam QueryPaging queryPaging) {
        PagingInfo<SneakingMessageListItem> result = teacherFacade.getSneakingMessages(getCurrentUserId(), queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "是否有新的小报告")
    @GET
    @Path("sneaking/msg/hasNew")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<HasNewSneakingMessagesResult> hasNewSneakingMessages() {
        HasNewSneakingMessagesResult result = teacherFacade.hasNewSneakingMessages(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取班级中的学生列表")
    @GET
    @Path("class/student/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<StudentListItem>> getStudentsOfClass(
            @ApiParam(value = Comments.CLASS_ID)
            @QueryParam("classId")
            @NotNull
            Long classId
    ) {
        List<StudentListItem> result = teacherFacade.getStudentsOfClass(getCurrentUserId(), classId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "从班级中删除学生")
    @POST
    @Path("class/student/remove")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> removeStudentFromClass(
            @ApiParam(value = Comments.CLASS_ID)
            @FormParam("classId")
            @NotNull
            Long classId,
            @ApiParam(value = Comments.STUDENT_ID)
            @FormParam("studentId")
            @NotNull
            Long studentId) {
    	 teacherFacade.removeStudentFromClass(getCurrentUserId(), classId, studentId);
    	return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "删除课件")
    @POST
    @Path("courseware/remove")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> removeCourseware(
            @ApiParam(value = Comments.COURSE_MATERIAL_ID)
            @FormParam("id")
            @NotNull
            Long id) {
    	 teacherFacade.removeCourseMaterial(getCurrentUserId(), id);
    	return ResponseBuilder.buildSuccessResponse();
    }
 @ApiOperation(value = "上传考试成绩")
    @POST
    @Path("courseQuiz/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> uploadCourseQuiz(

            @FormDataParam("classId")

            final Long classId,

            @FormDataParam("quizId")

            final Long quizId,
            @FormDataParam("file") InputStream fileInputStream,
            @FormDataParam("file") FormDataContentDisposition contentDispositionHeader
    ) {
        getCurrentUserId();
        teacherFacade.uploadCourseQuiz(getCurrentUserId(), classId, quizId, fileInputStream,contentDispositionHeader);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "查询作业签字详情")
    @GET
    @Path("homework/reviewList/{homeworkId}")
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse findReviewList(
            @ApiParam("作业id")
            @NotNull
            @PathParam("homeworkId")
            Long homeworkId
    ) {
        List<HomeworkReviewDto> data = teacherFacade.findReviewList(getCurrentUserId(),homeworkId);
        return ResponseBuilder.buildSuccessResponse(data);
    }

    @ApiOperation(value = "获取老师周课表")
    @GET
    @Path("findOneWeekCourses")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse findOneWeekCourses(
            @ApiParam("班级id")
            @NotNull
            @QueryParam("classId")
            Long classId
    ) {
        List<CourseScheduleDto> courseScheduleDtos = teacherFacade.findOneWeekCourses(getCurrentUserId(), classId);
        return ResponseBuilder.buildSuccessResponse(courseScheduleDtos);
    }
}
