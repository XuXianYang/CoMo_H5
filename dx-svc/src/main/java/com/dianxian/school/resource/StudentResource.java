package com.dianxian.school.resource;

import com.dianxian.core.resource.*;
import com.dianxian.core.server.BaseResource;
import com.dianxian.school.consts.Comments;
import com.dianxian.school.consts.SchoolConstants;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.CalorieRecordInfoDto;
import com.dianxian.school.dto.CourseMaterialType;
import com.dianxian.school.dto.PlaygroundActDto;
import com.dianxian.school.dto.QuizCategory;
import com.dianxian.school.facade.StudentFacade;
import com.dianxian.school.request.CreateSosMessageRequest;
import com.dianxian.school.request.RequestDate;
import com.dianxian.school.request.RequestTime;
import com.dianxian.school.request.student.CreateCalorieRequest;
import com.dianxian.school.request.student.DiscussRequest;
import com.dianxian.user.domain.User;
import io.swagger.annotations.*;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xuwenhao on 2016/5/29.
 */
@Api(value="/student")
@Path("student")
public class StudentResource extends BaseResource {
    @Autowired
    StudentFacade studentFacade;


    @ApiOperation(value = "学生的个人信息。带有学生唯一标识码。")
    @GET
    @Path("my/info")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<User> getMyInfo() {
        User result = studentFacade.getMyInfo(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "提供学生首页需要的全部数据")
    @GET
    @Path("home/info")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<StudentHomeData> getHomeData(
            @ApiParam(value = Comments.HOMEWORK_STUDY_DATE + " 可以不填，不填则默认根据当天时间进行计算，如果是周末则取最近的周五")
            @QueryParam("studyDate")
            RequestDate studyDate) {
        StudentHomeData result = studentFacade.getHomeData(getCurrentUserId(), getCurrentStudentUserId(), studyDate);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "加入班级")
    @POST
    @Path("class/join")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> joinSchool(
            @ApiParam(value = Comments.CLASS_CODE)
            @NotNull @FormParam("code") String code) {
        studentFacade.joinClass(getCurrentUserId(), code);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "获取班级的教师任课信息")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("course/assignment/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseAssignment>> getCourseAssignments() {
        List<CourseAssignment> result = studentFacade.getCourseAssignments(getCurrentStudentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取课程表信息")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("course/schedule/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseSchedule>> getCourseSchedule() {
        List<CourseSchedule> result = studentFacade.getCourseSchedule(getCurrentStudentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取最近的考试安排信息")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("quiz/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseQuiz>> getRecentQuizes() {
        List<CourseQuiz> result = studentFacade.getRecentQuizes(getCurrentStudentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "学期列表")
    @GET
    @Path("term/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<Term>> getTerms() {
        List<Term> result = studentFacade.getTerms(getCurrentStudentUserId());
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
            @ApiParam(value = Comments.STUDY_YEAR)
            @QueryParam("studyYear")
            Integer studyYear,
            @ApiParam(value = Comments.STUDY_TERM)
            @QueryParam("studyTerm")
            Integer studyTerm,
            @ApiParam(value = Comments.QUIZ_CATEGORY)
            @QueryParam("category")
            @DefaultValue("" + QuizCategory.MONTHLY)
            Integer category
    ) {
        QuizScoreSummary result = studentFacade.getQuizScoreReport(getCurrentStudentUserId(), studyYear, studyTerm, category);
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
        CourseScoreReport result = studentFacade.getCourseScoreReport(getCurrentStudentUserId(), courseId, studyYear, category);
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
            @ApiParam(value = Comments.COURSE_ID)
            @QueryParam("courseId")
            @NotNull
            Long courseId,
            @ApiParam(value = Comments.STUDY_YEAR)
            @QueryParam("studyYear")
            @NotNull
            Integer studyYear
    ) {
        List<CourseQuizScore> result = studentFacade.getCourseScoreTrends(getCurrentStudentUserId(), courseId, studyYear);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取公告列表")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("announcement/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<Announcement>> getAnnouncements(@BeanParam QueryPaging queryPaging) {
        PagingInfo<Announcement> result = studentFacade.getAnnouncements(getCurrentStudentUserId(), queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取公告详情")
    @GET
    @Path("announcement/detail")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Announcement> getAnnouncementDetail(
            @ApiParam(value = Comments.ANNOUNCEMENT_ID)
            @QueryParam("id")
            @NotNull
            Long id
    ) {
        Announcement result = studentFacade.getAnnouncementDetail(getCurrentStudentUserId(), id);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "获取最新一条公告")
    @ApiResponses(value = {
            @ApiResponse(code = ResponseConstants.UN_AUTHORIZED, message = "UN_AUTHORIZED"),
            @ApiResponse(code = ServiceCodes.STUDENT_NOT_JOINED_CLASS, message = "STUDENT_NOT_JOINED_CLASS")})
    @GET
    @Path("announcement/latest")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<Announcement>> getLatestAnnouncement() {
        PagingInfo<Announcement> result = studentFacade.getLatestAnnouncement(getCurrentStudentUserId());
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "课程材料列表")
    @GET
    @Path("course/material/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<List<CourseMaterial>> getCourseMaterials(
            @ApiParam(value = Comments.COURSE_ID)
            @QueryParam("courseId")
            Long courseId,

            @ApiParam(value = Comments.COURSE_MATERIAL_TYPE, allowableValues = CourseMaterialType.PREVIEW + ","
                    + CourseMaterialType.NOTE + "," + CourseMaterialType.COURSEWARE)
            @QueryParam("type")
            @Min(value = CourseMaterialType.PREVIEW)
            @Max(value = CourseMaterialType.COURSEWARE)
            @DefaultValue("" + CourseMaterialType.COURSEWARE)
            Integer type,

            @ApiParam(value = Comments.REQUEST_DATE_FORMAT)
            @QueryParam("beginDate")
            RequestDate beginDate,
            @ApiParam(value = Comments.REQUEST_DATE_FORMAT)
            @QueryParam("endDate")
            RequestDate endDate,

            @BeanParam
            QueryPaging queryPaging) {
        PagingInfo<CourseMaterial> result = studentFacade.getCourseMaterials(getCurrentStudentUserId(), courseId, type, beginDate, endDate, queryPaging);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "签字本数据")
    @GET
    @Path("homework/review/list")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<HomeworkReviewListResult> getHomeworkReviewStatus(
            @ApiParam(value = Comments.HOMEWORK_STUDY_DATE + " 可以不填，不填则默认根据当天时间进行计算，如果是周末则取最近的周五")
            @QueryParam("studyDate")
            RequestDate studyDate) {
        HomeworkReviewListResult result = studentFacade.getHomeworkReviewStatus(getCurrentStudentUserId(), studyDate);
        return ResponseBuilder.buildSuccessResponse(result);
    }

    @ApiOperation(value = "发送小报告")
    @POST
    @Path("sneaking/msg/send")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> sendSneakingMessage(
            @ApiParam("内容")
            @FormParam("content")
            @Length(min = SchoolConstants.MIN_SNEAKING_MSG_LENGTH, max = SchoolConstants.MAX_SNEAKING_MSG_LENGTH)
            @NotBlank
            @NotNull
            String content) {
        studentFacade.sendSneakingMessage(getCurrentUserId(), content);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "发送求救消息")
    @POST
    @Path("sos/msg/send")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> sendSosMessage(@BeanParam CreateSosMessageRequest request) {
        studentFacade.sendSosMessage(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "设置发送超时未到家的告警信息")
    @POST
    @Path("sos/msg/warningConfig")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> configSosMessageWarningTime(
            @ApiParam(value = Comments.REQUEST_DATE_TIME_FORMAT + "期望到家时间")
            @FormParam("expectTime")
            @NotNull
            RequestTime exceptTime,
            @ApiParam(value = Comments.REQUEST_TIME_FORMAT + "timeout")
            @FormParam("timeout")
            Long timeout
            )
    {
        studentFacade.setSosMsgWarningTime(getCurrentUserId(), exceptTime, timeout);
        return ResponseBuilder.buildSuccessResponse();
    }
    @ApiOperation(value = "获取超时未到家的告警设置信息")
    @GET
    @Path("sos/msg/getWarningTime")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<SosMessageWarningConfigTime> getConfigSosMessageWarningTime()
    {
        SosMessageWarningConfigTime sosMessageWarningConfigTime = studentFacade.getSosMsgWarningTime(getCurrentUserId());
        return ResponseBuilder.buildSuccessResponse(sosMessageWarningConfigTime);
    }
    @ApiOperation(value = "发送已安全到家消息")
    @POST
    @Path("safe/msg/send")
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> sendSafeMessage(@BeanParam CreateSosMessageRequest request) {
        studentFacade.sendSafeMessage(getCurrentUserId(), request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "操场发布活动")
    @POST
    @Path("pg/sendPgAct")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public GenericResponse sendPgAct(
            @ApiParam("单图片名长度不应超过10个字符，最多不能上传超过9张图片")
            FormDataMultiPart form) {
        studentFacade.sendPgAct(getCurrentUserId(), form);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "显示操场活动列表")
    @Path("pg/listPgAct")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public GenericResponse listPgAct(@BeanParam QueryPaging pageInfo) {
        PagingInfo<PlaygroundActDto> pageData = studentFacade.listPgAct(getCurrentUserId(), pageInfo);
        return ResponseBuilder.buildSuccessResponse(pageData);
    }

    @ApiOperation(value = "删除单个操场活动")
    @Path("pg/deletePgAct")
    @POST
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> deletePgAct(
            @NotNull
            @FormParam("actId")
            @ApiParam(Comments.PLAYGROUND_ACT_ID)
            Long actId) {
        studentFacade.deletePgAct(getCurrentUserId(), actId);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "显示单个操场活动明细 -- 评论默认显示10条；无法修改--代码更正")
    @Path("pg/listDetailPgAct/{actId}")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<PlaygroundActDto> listDetailPgAct(
            @NotNull @PathParam("actId") @ApiParam(Comments.PLAYGROUND_ACT_ID)
            Long actId
    ) {
        PlaygroundActDto dto = studentFacade.listDetailPgAct(actId);
        return ResponseBuilder.buildSuccessResponse(dto);
    }

    @ApiOperation(value = "点赞")
    @Path("pg/saveZan")
    @POST
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse<Object> saveZan(
            @NotNull @FormParam("actId") @ApiParam(Comments.PLAYGROUND_ACT_ID)
            Long actId) {
        studentFacade.saveZan(getCurrentUserId(), actId);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "取消赞")
    @Path("pg/removeZan")
    @POST
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse<Object> removeZan(
            @NotNull @FormParam("actId") @ApiParam(Comments.PLAYGROUND_ACT_ID)
            Long actId) {

        studentFacade.removeZan(getCurrentUserId(), actId);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "添加评论")
    @Path("pg/saveDiscuss")
    @POST
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse<Object> saveDiscuss(
            @BeanParam DiscussRequest request
    ) {
        studentFacade.saveDiscuss(request);
        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "我评论的帖子")
    @Path("pg/discussPgAct2me")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse discussPgAct2me(@BeanParam QueryPaging pageInfo) {
        PagingInfo<PlaygroundActDto> pageData = studentFacade.discussPgAct(getCurrentUserId(), pageInfo);
        return ResponseBuilder.buildSuccessResponse(pageData);
    }

    @ApiOperation(value = "我赞的的帖子")
    @Path("pg/zanPgAct2me")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse zanPgAct2me(@BeanParam QueryPaging pageInfo) {
        PagingInfo<PlaygroundActDto> pageData = studentFacade.zanPgAct2me(getCurrentUserId(), pageInfo);
        return ResponseBuilder.buildSuccessResponse(pageData);
    }

    @ApiOperation(value = "我发布的的帖子")
    @Path("pg/sendPgAct2me")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse sendPgAct2me(@BeanParam QueryPaging pageInfo) {
        PagingInfo<PlaygroundActDto> pageData = studentFacade.sendPgAct2me(getCurrentUserId(), pageInfo);
        return ResponseBuilder.buildSuccessResponse(pageData);
    }

    @ApiOperation(value = "发现推荐的操场活动")
    @Path("pg/discoverPdAct")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse discoverPdAct(@BeanParam QueryPaging pageInfo) {
        PagingInfo<PlaygroundActDto> pageData = studentFacade.discoverPdAct(getCurrentUserId(), pageInfo);
        return ResponseBuilder.buildSuccessResponse(pageData);
    }

    @ApiOperation(value = "评论分页显示")
    @Path("pg/getPageDiscuss")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse getPageDiscuss(
            @NotNull @QueryParam("actId") @ApiParam(Comments.PLAYGROUND_ACT_ID)
            Long actId,
            @BeanParam QueryPaging pageInfo
    ) {
        PagingInfo<Discuss> discuss = studentFacade.getPageDiscuss(actId, pageInfo);
        return ResponseBuilder.buildSuccessResponse(discuss);
    }

    @ApiOperation(value = "可忽略--赞分页信息获取")
    @Path("pg/getPageZan")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse getPageZan(
            @NotNull @QueryParam("actId") @ApiParam(Comments.PLAYGROUND_ACT_ID)
            Long actId,
            @BeanParam QueryPaging pageInfo
    ) {
        PagingInfo<Zan> pageZan = studentFacade.getPageZan(actId, pageInfo);
        return ResponseBuilder.buildSuccessResponse(pageZan);
    }

//    ————————————————————————————————————————————————————————————————————————————————————————————————————————————————————

    @ApiOperation(value = "学生签到")
    @Path("calorie/saveMark")
    @POST
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse saveMark() {

        studentFacade.saveMark(getCurrentUserId());

        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "查询学生签到记录")
    @Path("calorie/findMarkRecord")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse findMarkRecord(
            @QueryParam("date") @ApiParam("当前时间年份+月份(具体日期不限)，返回该用户该月签到过的日期-如：2016-11-12")
            RequestDate date
    ) {
        List<Mark> marks = studentFacade.findMarkRecord(getCurrentUserId(),date);
        return ResponseBuilder.buildSuccessResponse(marks);
    }

    @ApiOperation(value = "消卡发布记录")
    @Path("calorie/sendRecord")
    @POST
    @Produces(MediaTypes.APPLICATION_JSON_UTF8_VALUE)
    public GenericResponse sendRecord(
            @BeanParam
            CreateCalorieRequest request) {

        studentFacade.sendRecord(getCurrentUserId(),request);

        return ResponseBuilder.buildSuccessResponse();
    }

    @ApiOperation(value = "获取所有的消卡项目")
    @Path("calorie/findAllCalorieItems")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse findAllCalorieItems(){
        List<CalorieItems> data = studentFacade.findAllCalorieItems();
        return ResponseBuilder.buildSuccessResponse(data);
    }

    @ApiOperation(value = "获取消卡排名")
    @Path("calorie/findRanking")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse findRanking(
            @QueryParam("date") @ApiParam("不给默认为当月")
            RequestDate requestDate){
        List<CalorieRecordInfoDto> data =  studentFacade.findRanking(requestDate);
        return ResponseBuilder.buildSuccessResponse(data);
    }

    @ApiOperation(value = "月消卡记录")
    @Path("calorie/findCalorieRecordList")
    @GET
    @Produces(MediaTypes.APPLICATION_JSON_VALUE)
    public GenericResponse findCalorieRecordList(
            @QueryParam("date") @ApiParam("-- 不给时间默认当前月")
            RequestDate requestDate
    ){
        List<CalorieRecordInfoDto> data =  studentFacade.findCalorieRecordList(getCurrentUserId(),requestDate);
        return ResponseBuilder.buildSuccessResponse(data);
    }

}
