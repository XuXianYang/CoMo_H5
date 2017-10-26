package com.dianxian.school.resource;

import com.dianxian.core.resource.*;
import com.dianxian.core.server.BaseResource;
import com.dianxian.school.consts.Comments;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.CourseMaterialType;
import com.dianxian.school.dto.SneakingMessageDto;
import com.dianxian.school.dto.SosMessageByDayDto;
import com.dianxian.school.dto.SosMessageDto;
import com.dianxian.school.facade.ParentFacade;
import com.dianxian.school.request.RequestDate;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.ParentChildRelation;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by XuWenHao on 6/15/2016.
 */
@Api(value="/parent")
@Path("parent")
public class ParentResource extends BaseResource {
    @Autowired
    ParentFacade parentFacade;

    @ApiOperation(value = "获取子女列表")
    @GET
    @Path("child/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<User>> getChildren() {
        List<User> result = parentFacade.getChildren(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "添加子女")
    @POST
    @Path("child/add")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> addChild(
            @ApiParam(value = Comments.STUDENT_CODE)
            @FormParam("studentCode")
            @NotBlank @NotNull
            String studentCode,
            @ApiParam(value = Comments.PARENT_CHILD_RELATION, allowableValues = ParentChildRelation.FATHER + "," + ParentChildRelation.MOTHER)
            @FormParam("relation")
            @Min(value = ParentChildRelation.FATHER)
            @Max(value = ParentChildRelation.MOTHER)
            Integer relation) {
        parentFacade.addChild(getCurrentUserId(), studentCode, relation);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "获取子女信息")
    @GET
    @Path("child")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<User> getChildren(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId) {
        User result = parentFacade.getChild(getCurrentUserId(), studentUserId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "选择一个孩子,查看他的数据")
    @POST
    @Path("child/select")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> selectChild(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @FormParam("studentUserId")
            @NotNull
            Long studentUserId) {
        parentFacade.selectChild(getCurrentUserId(), studentUserId);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "作业签字")
    @POST
    @Path("homework/review")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> reviewHomework(
            @ApiParam(value = Comments.COURSE_ID)
            @FormParam("courseId")
            @NotNull
            Long courseId,
            @ApiParam(value = Comments.HOMEWORK_STUDY_DATE)
            @FormParam("studyDate")
            RequestDate studyDate) {
        parentFacade.reviewHomeworkByCourse(getCurrentUserId(), getCurrentStudentUserId(), courseId, studyDate);
        return ResponseBuilder.buildSuccessResponse();
    }
    @ApiOperation(value = "查看一个孩子的sos信息")
    @GET
    @Path("sos/msg/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<SosMessageByDayDto>> getSosMessages(@BeanParam QueryPaging queryPaging) {
        PagingInfo<SosMessageByDayDto> result = parentFacade.getSosMessages(getCurrentUserId(), queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }
    /*
    @ApiOperation(value = "提供学生首页需要的全部数据")
    @GET
    @Path("home/info")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<StudentHomeData> getHomeData(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId,
            @ApiParam(value = Comments.HOMEWORK_STUDY_DATE)
            @QueryParam("studyDate")
            RequestDate studyDate) {
        StudentHomeData result = parentFacade.getHomeData(getCurrentUserId(), studentUserId, studyDate);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取班级的教师任课信息")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("course/assignment/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseAssignment>> getCourseAssignments(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId) {
        List<CourseAssignment> result = parentFacade.getCourseAssignments(getCurrentUserId(), studentUserId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取课程表信息")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("course/schedule/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseSchedule>> getCourseSchedule(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId) {
        List<CourseSchedule> result = parentFacade.getCourseSchedule(getCurrentUserId(), studentUserId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取最近的考试安排信息")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("quiz/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseQuiz>> getRecentQuizes(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId) {
        List<CourseQuiz> result = parentFacade.getRecentQuizes(getCurrentUserId(), studentUserId);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "所有课程的考试成绩汇总报告")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("quiz/score/summary")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<QuizScoreSummary> getQuizScoreReport(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId,
            @ApiParam(value = Comments.STUDY_YEAR)
            @QueryParam("studyYear")
            @NotNull
            Integer studyYear,
            @ApiParam(value = Comments.QUIZ_CATEGORY)
            @QueryParam("category")
            @NotNull
            Integer category
    ) {
        QuizScoreSummary result = parentFacade.getQuizScoreReport(getCurrentUserId(), studentUserId, studyYear, category);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "单门课程的考试成绩报告")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("course/score/report")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<CourseScoreReport> getCourseScoreReport(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId,
            @ApiParam(value = Comments.COURSE_ID)
            @QueryParam("courseId")
            @NotNull
            Long courseId,
            @ApiParam(value = Comments.STUDY_YEAR)
            @QueryParam("studyYear")
            @NotNull
            Integer studyYear,
            @ApiParam(value = Comments.QUIZ_CATEGORY)
            @QueryParam("category")
            @NotNull
            Integer category
    ) {
        CourseScoreReport result = parentFacade.getCourseScoreReport(getCurrentUserId(), studentUserId, courseId, studyYear, category);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "单门课程在一学年中的考试成绩趋势")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("course/score/trends")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseQuizScore>> getCourseScoreTrends(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId,
            @ApiParam(value = Comments.COURSE_ID)
            @QueryParam("courseId")
            @NotNull
            Long courseId,
            @ApiParam(value = Comments.STUDY_YEAR)
            @QueryParam("studyYear")
            @NotNull
            Integer studyYear
    ) {
        List<CourseQuizScore> result = parentFacade.getCourseScoreTrends(getCurrentUserId(), studentUserId, courseId, studyYear);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取公告列表")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("announcement/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<Announcement>> getAnnouncements(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId,
            @BeanParam QueryPaging queryPaging) {
        PagingInfo<Announcement> result = parentFacade.getAnnouncements(getCurrentUserId(), studentUserId, queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "课程材料列表")
    @GET
    @Path("course/material/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseMaterial>> getCourseMaterials(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId,
            @ApiParam(value = Comments.COURSE_ID)
            @QueryParam("courseId")
            @NotNull
            Long courseId,

            @ApiParam(value = Comments.COURSE_MATERIAL_TYPE, allowableValues = CourseMaterialType.PREVIEW + ","
                    + CourseMaterialType.NOTE + "," + CourseMaterialType.COURSEWARE)
            @QueryParam("type")
            @Min(value = CourseMaterialType.PREVIEW)
            @Max(value = CourseMaterialType.COURSEWARE)
            @NotNull
            Integer type,

            @BeanParam
            QueryPaging queryPaging) {
        PagingInfo<CourseMaterial> result = parentFacade.getCourseMaterials(getCurrentUserId(), studentUserId, courseId, type, queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "签字本数据")
    @GET
    @Path("homework/review/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<HomeworkReview>> getHomeworkReviewStatus(
            @ApiParam(value = Comments.STUDENT_USER_ID)
            @QueryParam("studentUserId")
            @NotNull
            Long studentUserId,
            @ApiParam(value = Comments.HOMEWORK_STUDY_DATE)
            @QueryParam("studyDate")
            @NotNull
            RequestDate studyDate) {
        List<HomeworkReview> result = parentFacade.getHomeworkReviewStatus(getCurrentUserId(), studentUserId, studyDate);
        return ResponseBuilder.buildSuccessResponse(result);
    }
    */


}
