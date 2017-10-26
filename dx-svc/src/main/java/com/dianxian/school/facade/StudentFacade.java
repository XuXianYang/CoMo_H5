package com.dianxian.school.facade;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.core.utils.time.DateUtils;
import com.dianxian.im.facade.IMFacade;
import com.dianxian.job.JobProperties;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.*;
import com.dianxian.school.manager.AnnouncementMgr;
import com.dianxian.school.manager.CourseMgr;
import com.dianxian.school.manager.HomeworkMgr;
import com.dianxian.school.manager.QuizMgr;
import com.dianxian.school.request.CreateSosMessageRequest;
import com.dianxian.school.request.RequestDate;
import com.dianxian.school.request.student.CreateCalorieRequest;
import com.dianxian.school.request.student.DiscussRequest;
import com.dianxian.school.service.*;
import com.dianxian.sms.dto.SmsDto;
import com.dianxian.sms.dto.SmsStatus;
import com.dianxian.sms.service.SmsService;
import com.dianxian.storage.utils.FileUploadUtils;
import com.dianxian.user.domain.User;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.TeacherDto;
import com.dianxian.user.dto.UserDto;
import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.service.UserService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by XuWenHao on 5/18/2016.
 */
@Component
public class StudentFacade {
    protected static Logger logger = LoggerFactory.getLogger(StudentFacade.class);
    @Autowired
    CourseMgr courseMgr;
    @Autowired
    QuizMgr quizMgr;
    @Autowired
    AnnouncementMgr announcementMgr;
    @Autowired
    HomeworkMgr homeworkMgr;
    @Autowired
    StudentService studentService;
    @Autowired
    ClassService classService;
    @Autowired
    UserService userService;
    @Autowired
    UserFacade userFacade;
    @Autowired
    SchoolService schoolService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    MessageService messageService;
    @Autowired
    SmsService smsService;
    @Autowired
    IMFacade imFacade;
    @Autowired
    JobProperties jobProperties;
    @Autowired
    PlaygroundActService playgroundActService;
    @Autowired
    MarkService markService;
    @Autowired
    CalorieRecordService calorieRecordService;

    /**
     * 获取学生的个人信息。带有学生唯一标识码。
     * @param currentUserId
     * @return
     */
    public User getMyInfo(Long currentUserId) {
        User child = userFacade.getUser(currentUserId);
        StudentDto studentDto = studentService.validateIsStudentByUserId(currentUserId);
        child.getStudentInfo().setCode(studentDto.getCode());

        return child;
    }

    public StudentHomeData getHomeData(Long currentUserId, Long studentUserId, Date studyDate) {
        Calendar queryDate = calculateQueryDate(studyDate);
        int dayOfWeek = queryDate.get(Calendar.DAY_OF_WEEK);
        // 在js里周一是1，java里周一是2，统一使用js里的值
        dayOfWeek = dayOfWeek - 1;

        StudentHomeData result = new StudentHomeData();
        SysInfo sysInfo = new SysInfo();
        sysInfo.setCurrentDate(DateUtils.getTodayDate().getTime());
        result.setSysInfo(sysInfo);
        result.setUser(userFacade.getUser(studentUserId));

        UserInfoDto currentUserInfo = userFacade.getUserInfoDto(currentUserId);
        result.setCurrentUserType(currentUserInfo.getType());

        if (null != result.getUser().getStudentInfo().getClassId()) {
            // 没有加入班级
            result.setLatestAnnouncement(getLatestAnnouncement(studentUserId).getList());

            List<CourseSchedule> courseSchedules = getCourseSchedule(studentUserId);
            Map<Integer, List<CourseSchedule>> courseSchedulesByDay = new LinkedHashMap<Integer, List<CourseSchedule>>();
            if (!CollectionUtils.isEmpty(courseSchedules)) {
                for (CourseSchedule courseSchedule : courseSchedules) {
                    List<CourseSchedule> list = courseSchedulesByDay.get(courseSchedule.getDayOfWeek());
                    if (null == list) {
                        list = Lists.newArrayList();
                        courseSchedulesByDay.put(courseSchedule.getDayOfWeek(), list);
                    }
                    list.add(courseSchedule);
                }
            }

            result.setCourseSchedule(courseSchedulesByDay.get(dayOfWeek));
            result.setHomeworkReviewStatus(getHomeworkReviewStatus(studentUserId, queryDate.getTime()));
        }
        return result;
    }

    private Calendar calculateQueryDate(Date requestDate) {
        Calendar queryDate = null;
        if (null == requestDate) {
            queryDate = DateUtils.getTodayDate();
            // 改为默认就取当天的日期
//            int dayOfWeek = queryDate.get(Calendar.DAY_OF_WEEK);
//            if (dayOfWeek == Calendar.SATURDAY) {
//                queryDate.add(Calendar.DATE, -1);
//            } else if (dayOfWeek == Calendar.SUNDAY) {
//                queryDate.add(Calendar.DATE, -2);
//            }
        } else {
            queryDate = Calendar.getInstance();
            queryDate.setTime(requestDate);
        }

        return queryDate;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void joinClass(Long userId, String classCode) {
        StudentDto studentDto = studentService.validateIsStudentByUserId(userId);
        if (!NumberUtils.isNullOrZero(studentDto.getSchoolId())
                || !NumberUtils.isNullOrZero(studentDto.getClassId())) {
            throw new BizLogicException(ServiceCodes.STUDENT_ALREADY_JOINED_CLASS, "Has joined class.");
        }
        ClassDto classDto = classService.getByCode(classCode);
        if (null == classDto) {
            throw new BizLogicException(ServiceCodes.CLASS_CODE_NOT_MATCH, "Class code does not match.");
        }
        studentService.joinClass(studentDto, classDto);

        if (!NumberUtils.isNullOrZero(classDto.getStudentImGroupId())) {
            imFacade.joinGroup(userId, classDto.getStudentImGroupId());
        }

        if (!NumberUtils.isNullOrZero(classDto.getParentImGroupId())) {
            List<UserDto> parentUsers = userService.getParentUsers(userId);
            if (!CollectionUtils.isEmpty(parentUsers)) {
                for (UserDto userDto : parentUsers) {
                    imFacade.joinGroup(userDto.getId(), classDto.getParentImGroupId());
                }
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseAssignment> getCourseAssignments(Long userId) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        ClassDto classDto = classService.validateClassExists(studentDto.getClassId());
        return courseMgr.getCourseAssignmentsOfClass(classDto);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseSchedule> getCourseSchedule(Long userId) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        return courseMgr.getCourseSchedule(studentDto.getClassId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseQuiz> getRecentQuizes(Long userId) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        return quizMgr.getRecentQuizes(studentDto.getClassId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Term> getTerms(Long userId) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        SchoolDto schoolDto = schoolService.validateSchoolExists(studentDto.getSchoolId());

        List<Term> result = Lists.newArrayList();
        List<Integer> studyYears = Lists.newArrayList();
        int years = 0;
        if (SchoolCategory.JUNIOR.value().equals(schoolDto.getCategory())) {
            years = 4;
        } else if (SchoolCategory.SENIOR.value().equals(schoolDto.getCategory())) {
            years = 3;
        }
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<Integer> terms = Lists.newArrayList(1, 2);
        for (int i = 0; i < years; i++) {
            for (Integer studyTerm : terms) {
                Term term = new Term();
                term.setStudyYear(currentYear + i);
                term.setStudyTerm(studyTerm);
                result.add(term);
            }
        }

        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public QuizScoreSummary getQuizScoreReport(Long userId, Integer studyYear, Integer studyTerm, Integer category) {
        return quizMgr.getQuizScoreReportOfStudent(userId, studyYear, studyTerm, category);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public CourseScoreReport getCourseScoreReport(Long userId, Long courseId, Integer studyYear, Integer category) {
        return quizMgr.getCourseScoreReport(userId, courseId, studyYear, category);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseQuizScore> getCourseScoreTrends(Long userId, Long courseId, Integer studyYear) {
        return quizMgr.getCourseScoreTrends(userId, courseId, studyYear);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<Announcement> getAnnouncements(Long userId, QueryPaging queryPaging) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        return announcementMgr.getAnnouncementsOfClass(studentDto.getClassId(), queryPaging);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Announcement getAnnouncementDetail(Long userId, Long id) {
        studentService.validateHasJoinedClass(userId);
        // TODO 校验id对应的Announcement是否可以读取
        return announcementMgr.getAnnouncementDetail(id);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<Announcement> getLatestAnnouncement(Long userId) {
        QueryPaging queryPaging = new QueryPaging();
        queryPaging.setPageNum(1);
        queryPaging.setPageSize(5);
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        return announcementMgr.getAnnouncementsOfClass(studentDto.getClassId(), queryPaging);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<CourseMaterial> getCourseMaterials(Long userId, Long courseId, Integer type, Date beginDate, Date endDate, QueryPaging queryPaging) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        return courseMgr.getCourseMaterials(studentDto.getClassId(), courseId, type, beginDate, endDate, queryPaging);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public HomeworkReviewListResult getHomeworkReviewStatus(Long userId, Date studyDate) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        Calendar queryDate = calculateQueryDate(studyDate);
        return homeworkMgr.getHomeworkReviewStatus(studentDto.getId(), studentDto.getClassId(), queryDate.getTime());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void sendSneakingMessage(Long userId, String content) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        TeacherDto teacherDto = teacherService.getHeadTeacherOfClass(studentDto.getClassId());

        SneakingMessageDto messageDto = new SneakingMessageDto();
        messageDto.setStudentId(studentDto.getId());
        messageDto.setTeacherId(teacherDto.getId());
        messageDto.setClassId(studentDto.getClassId());
        messageDto.setContent(content);
        messageService.sendSneakingMessage(userId, messageDto);

        SosMessageDto sosMessageDto = new SosMessageDto();
        sosMessageDto.setStudentId(studentDto.getId());
        sosMessageDto.setSchoolId(studentDto.getSchoolId());
        sosMessageDto.setClassId(studentDto.getClassId());
        sosMessageDto.setMessageType(MessageType.SNEAKING);
        sosMessageDto.setCreatedAt(new Date());
        long currentDayTimeStamp = genBizTimestamp();
        sosMessageDto.setBizTimestamp(currentDayTimeStamp);
        sosMessageDto.setContent(content);
        messageService.sendSosMessage(userId, sosMessageDto);
    }

    public void setSosMsgWarningTime(Long userId, Date exceptTime, Long timeout){
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        studentService.setExceptedHomeTime(studentDto, exceptTime, timeout);

    }

    public SosMessageWarningConfigTime getSosMsgWarningTime(Long userId){
        StudentDto studentDto = userService.getStudentByUserId(userId);
        SosMessageWarningConfigTime sosMessageWarningConfigTime = new SosMessageWarningConfigTime();
        Date waringConfigUpdateTime = studentDto.getExpectedAtHomeTimeUpdateAt();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date midnight = cal.getTime();
        if(waringConfigUpdateTime== null || waringConfigUpdateTime.before(midnight)){
            sosMessageWarningConfigTime.setExpectTime("");
            sosMessageWarningConfigTime.setTimeOut(0);
        }else{
            DateFormat expectTimeDateFormat = new SimpleDateFormat("HH:mm:ss");
            DateFormat timeoutDateFormat = new SimpleDateFormat("HH:mm:ss");
            String expectWarningConfiTime = studentDto.getExpectedAtHomeTime() == null ? "":expectTimeDateFormat.format(studentDto.getExpectedAtHomeTime());
            sosMessageWarningConfigTime.setExpectTime(expectWarningConfiTime);
            String timebuffer = studentDto.getExpectedAtHomeTimeBuffer() == null ? "" : timeoutDateFormat.format(studentDto.getExpectedAtHomeTimeBuffer());
            String []timeArray = timebuffer.split(":");
            sosMessageWarningConfigTime.setTimeOut(Integer.valueOf(timeArray[0])* 3600+ Integer.valueOf(timeArray[1]) * 60 + Integer.valueOf(timeArray[2]));

        }
         return sosMessageWarningConfigTime;
    }

    public void sendSosMessage(Long userId, CreateSosMessageRequest request) {
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        sendSafeAssistantMessage(studentDto, request, MessageType.SOS);
    }

    public void sendSafeMessage(Long userId, CreateSosMessageRequest request){
        StudentDto studentDto = studentService.validateHasJoinedClass(userId);
        sendSafeAssistantMessage(studentDto, request, MessageType.SAFE);
    }

    public void sendSafeAssistantMessage(StudentDto studentDto, CreateSosMessageRequest request,int messageType){
        saveSosMessage(studentDto, request, messageType);

        UserInfoDto userInfoDto = userService.validateUserInfoExists(studentDto.getUserId());
        Throwable sendSosMessageToTeacherExp = null;
        Throwable sendSosMessageToParentExp = null;

        // 只发送求救或超时短信给老师
        if (messageType == MessageType.SOS || messageType == MessageType.SYS_WARNING) {
            if (!NumberUtils.isNullOrZero(studentDto.getClassId())) {
                // 只有加入了班级才需要发短信给老师
                try {
                    sendSosMessageToTeacher(studentDto, userInfoDto.getName(), messageType, request.getContent());
                } catch (Throwable e) {
                    sendSosMessageToTeacherExp = e;
                    logger.error(e.getMessage(), e);
                }
            }
        }
        try {
            sendSosMessageToParent(studentDto, userInfoDto.getName(), messageType, request.getContent());
        } catch (Throwable e) {
            sendSosMessageToParentExp = e;
            logger.error(e.getMessage(), e);
        }

        if (null != sendSosMessageToTeacherExp && null != sendSosMessageToParentExp) {
            throw new BizLogicException(ServiceCodes.SOS_SEND_FAIL, "SOS_SEND_FAIL");
        } else if (null != sendSosMessageToTeacherExp) {
            throw new BizLogicException(ServiceCodes.SOS_SEND_TO_TEACHER_FAIL, "SOS_SEND_TO_TEACHER_FAIL");
        } else if (null != sendSosMessageToParentExp) {
            throw new BizLogicException(ServiceCodes.SOS_SEND_TO_PARENT_FAIL, "SOS_SEND_TO_PARENT_FAIL");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSosMessage(StudentDto studentDto, CreateSosMessageRequest request, int messageYpe) {
        SosMessageDto sosMessageDto = new SosMessageDto();
        if(request != null){
            BeanUtils.copyProperties(request, sosMessageDto);
        }
        sosMessageDto.setStudentId(studentDto.getId());
        sosMessageDto.setSchoolId(studentDto.getSchoolId());
        sosMessageDto.setClassId(studentDto.getClassId());
        sosMessageDto.setMessageType(messageYpe);
        if(messageYpe == MessageType.SYS_WARNING){
            sosMessageDto.setStatus(MessageStatus.ON);
        }
        sosMessageDto.setCreatedAt(new Date());
        long currentDayTimeStamp = genBizTimestamp();
        sosMessageDto.setBizTimestamp(currentDayTimeStamp);
        messageService.sendSosMessage(studentDto.getUserId(), sosMessageDto);
    }

    private long genBizTimestamp() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 0:0:0");//设置日期格式
        String msg = df.format(new Date());
        Date date;
        long currentDayTimeStamp = 0;
        try {
            date = df.parse(msg);
            currentDayTimeStamp = date.getTime();
        } catch (ParseException e) {
        }
        return currentDayTimeStamp;
    }

    public SmsDto sendSosMessageToTeacher(StudentDto studentDto, String studentName, int messageType, String content) {
        TeacherDto teacherDto = teacherService.getHeadTeacherOfClass(studentDto.getClassId());
        UserDto teacherUser = userService.getUserById(teacherDto.getUserId());
        if (messageType == MessageType.SOS){
            return smsService.sendSosSms(studentName, teacherUser.getMobileNo(), content);
        } else if (messageType == MessageType.SYS_WARNING) {
            return smsService.sendSecurityTimeoutSms(studentName, teacherUser.getMobileNo());
        } else {
            return smsService.sendSafeSms(studentName, teacherUser.getMobileNo());
        }
    }

    public void sendSosMessageToParent(StudentDto studentDto, String studentName, int messageType, String content) {
        List<UserDto> parents = userService.getParentUsers(studentDto.getUserId());
        if (!CollectionUtils.isEmpty(parents)) {
            for (UserDto userDto : parents) {
                SmsDto smsDto ;
                if (messageType == MessageType.SOS){
                    smsDto = smsService.sendSosSms(studentName, userDto.getMobileNo(), content);
                } else if (messageType == MessageType.SYS_WARNING) {
                    smsDto = smsService.sendSecurityTimeoutSms(studentName, userDto.getMobileNo());
                } else {
                    smsDto = smsService.sendSafeSms(studentName, userDto.getMobileNo());
                }
                if (!smsDto.getStatus().equals(SmsStatus.SEND_SUCCESS.getValue())) {
                    throw new BizLogicException(ServiceCodes.SOS_SEND_TO_PARENT_FAIL, "SOS_SEND_TO_PARENT_FAIL: parent: " + userDto.getId());
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendPgAct(Long userId, FormDataMultiPart form) {
        List<FormDataBodyPart> files = form.getFields("file");
        String content = form.getField("content").getValue();

        //图片跟文本不能同时为空
        if (null == files && null == content)
            throw new BizLogicException(ServiceCodes.PLAYGROUND_ACT_FORM_NULL, "PLAYGROUND_ACT_FORM_NULL");

        Map<String, InputStream> fileMap = Maps.newHashMap();
        if (files != null) {
            for (FormDataBodyPart file : files) {
                String fileName = FileUploadUtils.getFileName(file.getFormDataContentDisposition());
                if (StringUtils.isEmpty(fileName)) continue;/*上传文件名为空；也不做处理*/
                InputStream in = file.getValueAs(InputStream.class);
                fileMap.put(fileName, in);
            }
        }

        playgroundActService.sendPgAct(userId, content, fileMap);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<PlaygroundActDto> listPgAct(Long userId, QueryPaging pageInfo) {
        //校验下用户是否是学生
        StudentDto studentDto = userService.getStudentByUserId(userId);
        if (null == studentDto)
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, "STUDENT_NOT_EXISTS OR NOT IS A STUDENT!");

        //获取学生信息传递给service处理返回
        return playgroundActService.listPgAct(studentDto, pageInfo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deletePgAct(Long currentUserId, Long actId) {
        //校验用户是否是学生
        StudentDto studentDto = userService.getStudentByUserId(currentUserId);
        if (null == studentDto)
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, "STUDENT_NOT_EXISTS OR NOT IS A STUDENT!");

        //删除活动
        playgroundActService.deletePgAct(actId);

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PlaygroundActDto listDetailPgAct(Long actId) {

        return playgroundActService.listDetailPgAct(actId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveZan(Long userId, Long actId) {
        playgroundActService.saveZan(userId, actId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeZan(Long userId, Long actId) {

        playgroundActService.removeZan(userId, actId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveDiscuss(DiscussRequest request) {
        Discuss dis = new Discuss();
        BeanUtils.copyProperties(request, dis);
        playgroundActService.saveDiscuss(dis);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<PlaygroundActDto> discussPgAct(Long userId, QueryPaging pageInfo) {
        StudentDto studentDto = userService.getStudentByUserId(userId);
        if (studentDto == null) throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, "STUDENT_NOT_EXISTS");
        return playgroundActService.discussPgAct(studentDto, pageInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<PlaygroundActDto> zanPgAct2me(Long userId, QueryPaging pageInfo) {
        StudentDto studentDto = userService.getStudentByUserId(userId);
        if (studentDto == null) throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, "STUDENT_NOT_EXISTS");
        return playgroundActService.zanPgAct2me(studentDto, pageInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<PlaygroundActDto> sendPgAct2me(Long userId, QueryPaging pageInfo) {
        StudentDto studentDto = userService.getStudentByUserId(userId);

        if (studentDto == null) throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, "STUDENT_NOT_EXISTS");
        return playgroundActService.sendPgAct2me(studentDto, pageInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<PlaygroundActDto> discoverPdAct(Long userId, QueryPaging pageInfo) {
        StudentDto studentDto = userService.getStudentByUserId(userId);
        if (studentDto == null) throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, "STUDENT_NOT_EXISTS");
        return playgroundActService.discoverPdAct(studentDto, pageInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<Zan> getPageZan(Long actId, QueryPaging pageInfo) {
        return playgroundActService.getPageZan(actId, pageInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<Discuss> getPageDiscuss(Long actId, QueryPaging pageInfo) {
        return playgroundActService.getPageDiscuss(actId, pageInfo);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMark(Long userId) {
        StudentDto user = userService.getStudentByUserId(userId);
        if(user == null ) throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS,"STUDENT_NOT_EXISTS");
        markService.saveMark(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Mark> findMarkRecord(Long userId, Date date) {
        StudentDto user = userService.getStudentByUserId(userId);
        if(user == null ) throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS,"STUDENT_NOT_EXISTS");
        return markService.findMarkRecord(user,date);
     }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendRecord(Long userId, CreateCalorieRequest request) {
        CalorieRecord bean = new CalorieRecord();
        BeanUtils.copyProperties(request,bean);
        calorieRecordService.saveRecord(userId,bean);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CalorieItems> findAllCalorieItems() {
        return calorieRecordService.findAllCalorieItems();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CalorieRecordInfoDto> findRanking(RequestDate requestDate) {
        if(requestDate == null) return calorieRecordService.findRanking(Calendar.getInstance().getTime());
        else return calorieRecordService.findRanking(requestDate);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CalorieRecordInfoDto> findCalorieRecordList(Long userId, RequestDate requestDate) {
        if(requestDate == null) return calorieRecordService.findCalorieRecordList(userId, Calendar.getInstance().getTime());
        else return calorieRecordService.findCalorieRecordList(userId, requestDate);
    }
}
