package com.dianxian.school.facade;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.exception.InvalidParamException;
import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.resource.ResponseConstants;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.im.facade.IMFacade;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.*;
import com.dianxian.school.manager.CourseMgr;
import com.dianxian.school.manager.DomainConverter;
import com.dianxian.school.manager.QuizMgr;
import com.dianxian.school.manager.SchoolMgr;
import com.dianxian.school.request.teacher.*;
import com.dianxian.school.service.*;
import com.dianxian.school.utils.UploadCourseQuizFile;
import com.dianxian.storage.dto.ResourceFileCategory;
import com.dianxian.storage.dto.ResourceFileDto;
import com.dianxian.storage.service.StorageService;
import com.dianxian.storage.utils.FileUploadUtils;
import com.dianxian.user.domain.Permissions;
import com.dianxian.user.dto.*;
import com.dianxian.user.service.PermissionService;
import com.dianxian.user.service.UserService;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.*;

/**
 * Created by XuWenHao on 4/27/2016.
 */
@Component
public class TeacherFacade {
    @Autowired
    SchoolMgr schoolMgr;
    @Autowired
    CourseMgr courseMgr;
    @Autowired
    QuizMgr quizMgr;
    @Autowired
    PermissionService permissionService;
    @Autowired
    SchoolService schoolService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    StudentService studentService;
    @Autowired
    ClassService classService;
    @Autowired
    CourseService courseService;
    @Autowired
    TeacherRoleService teacherRoleService;
    @Autowired
    QuizService quizService;
    @Autowired
    AnnouncementService announcementService;
    @Autowired
    HomeworkService homeworkService;
    @Autowired
    StorageService storageService;
    @Autowired
    MessageService messageService;
    @Autowired
    IMFacade imFacade;
    @Autowired
    UserService userService;
    @Autowired
    DomainConverter domainConverter;

    /**
     * 加入学校
     *
     * @param userId
     * @param code
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void joinSchool(Long userId, String code, Long id) {
        SchoolDto schoolDto = schoolService.getByCode(code);
        if (null == schoolDto) {
            throw new BizLogicException(ServiceCodes.SCHOOL_CODE_NOT_MATCH, "Invalid code.");
        }
        if (!schoolDto.getId().equals(id)) {
            throw new BizLogicException(ServiceCodes.SCHOOL_CODE_NOT_MATCH, "Invalid code.");
        }
        teacherService.validateForJoinSchool(userId);

        teacherService.joinSchool(userId, schoolDto.getId());
    }

    /**
     * 获取班级的任课信息
     *
     * @param userId
     * @param enrolYear
     * @param classNumber
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseAssignment> getCourseAssignments(Long userId, Integer enrolYear, Integer classNumber) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        ClassDto classDto = classService.getByClassNumber(teacherDto.getSchoolId(), enrolYear, classNumber);
        return courseMgr.getCourseAssignmentsOfClass(classDto);
    }

    /**
     * 获取一个老师在指定的班级中所任教的课程列表
     *
     * @param userId
     * @param classId
     * @return
     */
    public List<Course> getAssignedCoursesOfTeacherInClass(Long userId, Long classId) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        return courseMgr.getAssignedCoursesOfTeacherInClass(teacherDto.getId(), classId);
    }

    /**
     * 获取班级的任课信息
     *
     * @param classId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseAssignment> getCourseAssignments(Long classId) {
        ClassDto classDto = classService.getByClassId(classId);
        return courseMgr.getCourseAssignmentsOfClass(classDto);
    }

    /**
     * 查询一个老师所有的任课信息
     * @param userId
     * @return
     */
    public List<TeacherCourseAssignment> getCourseAssignmentsOfTeacher(Long userId) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        return courseMgr.getCourseAssignmentsOfTeacher(teacherDto.getId());
    }

    public SchoolInfo getSchoolInfoByClassId(Long classId) {
        return schoolMgr.getSchoolInfoById(classService.validateClassExists(classId).getSchoolId());
    }
    
    public SchoolInfo getSchoolInfoByTeacherUserId(Long teacherUserid) {
        TeacherDto teacherDto = userService.validateIsTeacher(teacherUserid);
        if (NumberUtils.isNullOrZero(teacherDto.getSchoolId())) {
            return null;
        }
    	return schoolMgr.getSchoolInfoById(teacherDto.getSchoolId());
    }

    /**
     * 加入班级
     *
     * @param userId
     * @param request
     * @return
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public JoinClassResult joinClass(Long userId, JoinClassRequest request) {
        for (Integer classNumber : request.getClassNumber()) {
            if (0 >= classNumber) {
                throw new InvalidParamException("Class number cannot less or equal than 0.");
            }
        }
        JoinClassResult result = new JoinClassResult();
        TeacherDto teacherDto = teacherService.validateForJoinClass(userId);

        Set<Long> courseIdSet = new HashSet<Long>(request.getCourseIds());
        List<Long> distinctCourseIds = new ArrayList<Long>(courseIdSet);
        // 检查课程号
        courseService.validateCoursesExist(distinctCourseIds);

        Map<Integer, Integer> failureClasses = Maps.newLinkedHashMap();
        List<ClassDto> successClasses = Lists.newArrayList();
        for (Integer classNumber : request.getClassNumber()) {
            try {
                ClassDto classDto = schoolMgr.joinClass(teacherDto, request.getEnrolYear(), classNumber, distinctCourseIds);
                successClasses.add(classDto);
            } catch (BizLogicException e) {
                failureClasses.put(classNumber, e.getCode());
            } catch (Exception e) {
                failureClasses.put(classNumber, ResponseConstants.INTERNAL_ERROR);
            }
        }

        result.setSuccessClasses(successClasses);
        if (!CollectionUtils.isEmpty(failureClasses)) {
            result.setFailureClasses(failureClasses);
        }
        return result;
    }

    public List<SchoolClassOfTeacher> getJoinedClasses(Long userId) {
        TeacherDto teacherDto = teacherService.validateIsTeacher(userId);
        List<SchoolClassOfTeacher> result = Lists.newArrayList();
        if (!NumberUtils.isNullOrZero(teacherDto.getSchoolId())) {
            List<Long> classIds = teacherService.getJoinedClasses(teacherDto.getId());
            for (Long classId : classIds) {
                ClassDto classDto = classService.validateClassExists(classId);
                result.add(schoolMgr.convertToClassOfTeacher(teacherDto.getId(), classDto));
            }
        }
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public SchoolClassDetail getSchoolClassDetail(Long userId, Long classId) {
        ClassDto classDto = classService.validateClassExists(classId);
        TeacherDto teacherDto = teacherService.getHeadTeacherOfClass(classId);
        SimpleTeacher simpleTeacher = teacherService.getTeacherDomainByTeacherId(teacherDto.getId());
        SchoolInfo schoolInfo = schoolMgr.getSchoolInfoById(classDto.getSchoolId());
        SchoolClassBasicInfo classBasicInfo = domainConverter.convert(classDto);

        SchoolClassDetail classDetail = new SchoolClassDetail();
        classDetail.setHeadTeacher(simpleTeacher);
        classDetail.setSchoolInfo(schoolInfo);
        classDetail.setBasicInfo(classBasicInfo);

        return classDetail;
    }

    public SchoolClassOfTeacher getSchoolClassInfo(Long userId, Long classId) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        ClassDto classDto = classService.validateClassExists(classId);
        if (!(teacherDto.getSchoolId().equals(classDto.getSchoolId()))) {
            throw new BizLogicException(ServiceCodes.TEACHER_SCHOOL_NOT_MATCH, "Teacher is not in the same school.");
        }
        return schoolMgr.convertToClassOfTeacher(teacherDto.getId(), classDto);
    }

    public UserInfoDto getHeadTeacher(Long classId) {
        TeacherDto teacherDto = teacherService.getHeadTeacherOfClass(classId);
        return userService.getUserInfoById(teacherDto.getUserId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void setCourseSchedule(Long userId, Long classId, List<SetCourseScheduleItem> courseSchedules) {
        TeacherDto teacherDto = teacherService.validateIsTeacher(userId);
        teacherRoleService.validatePermissionInClass(teacherDto, classId, Permissions.SET_COURSE_SCHEDULE);

        courseService.validateForCreateCourseSchedule(courseSchedules);
        List<CourseScheduleDto> dtos = Lists.newArrayList();
        for (SetCourseScheduleItem item : courseSchedules) {
            CourseScheduleDto dto = new CourseScheduleDto();
            BeanUtils.copyProperties(item, dto);
            dtos.add(dto);
        }
        courseService.setCourseSchedule(userId, classId, dtos);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CourseSchedule> getCourseSchedule(Long userId, Long classId) {
        teacherService.validateIsTeacherOfClass(userId, classId);
        return courseMgr.getCourseSchedule(classId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Course> getAllCourses() {
        return courseMgr.getAllCourses();
    }

    /**
     * 创建考试
     *
     * @param operatorUserId
     * @param params
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public QuizDto createQuiz(Long operatorUserId, CreateQuizRequest params) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(operatorUserId);
        permissionService.validatePermission(teacherDto.getUserId(), Permissions.CREATE_QUIZ);

        courseService.validateCoursesExist(Lists.newArrayList(params.getCourseId()));

        setRequestParams(params);

        QuizDto dto = new QuizDto();
        BeanUtils.copyProperties(params, dto);
        dto.setSchoolId(teacherDto.getSchoolId());
        return quizService.create(operatorUserId, dto);
    }

    /**
     * 学校管理员获取学校的考试列表
     * @param userId
     * @param queryPaging
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<CourseQuiz> getQuizList(Long userId, QueryPaging queryPaging) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        return quizMgr.getQuizOfSchool(teacherDto.getSchoolId(), queryPaging);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public CourseQuiz getQuizDetail(Long userId, Long quizId) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        QuizDto quizDto = quizService.validateQuizExists(quizId);
        if (!teacherDto.getSchoolId().equals(quizDto.getSchoolId())) {
            throw new BizLogicException(ServiceCodes.TEACHER_NOT_A_TEACHER_OF_SCHOOL, "Not a teacher of school " + quizDto.getSchoolId());
        }
        CourseDto courseDto = courseService.validateCoursesExist(quizDto.getCourseId());
        return quizMgr.convertQuiz(quizDto, courseDto);
    }

    /**
     * 修改考试
     *
     * @param userId
     * @param request
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateQuiz(Long userId, UpdateQuizRequest request) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        permissionService.validatePermission(teacherDto.getUserId(), Permissions.CREATE_QUIZ);

        QuizDto quizDto = quizService.validateQuizExists(request.getId());
        if (!teacherDto.getSchoolId().equals(quizDto.getSchoolId())) {
            throw new BizLogicException(ServiceCodes.TEACHER_NOT_A_TEACHER_OF_SCHOOL, "Not a teacher of school " + quizDto.getSchoolId());
        }

        courseService.validateCoursesExist(Lists.newArrayList(request.getCourseId()));

        setRequestParams(request);

        QuizDto dto = new QuizDto();
        BeanUtils.copyProperties(request, dto);
        quizService.update(userId, dto);
    }

    private void setRequestParams(CreateQuizRequest request) {
        if (null == request.getStudyYear() || null == request.getStudyTerm()) {
            int studyTerm = schoolService.getCurrentStudyTerm();
            request.setStudyTerm(studyTerm);
            request.setStudyYear(schoolService.getCurrentStudyYear(studyTerm));
        }
    }

    /**
     * 获取一个班级的所有考试列表
     * 这个早期定的接口，应该不用了
     * @param teacherUserId
     * @param classId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<CourseQuiz> getQuizOfClassByTeacher(Long teacherUserId, Long classId) {
        TeacherDto teacherDto = teacherService.validateIsTeacherOfClass(teacherUserId, classId);
        ClassDto classDto = classService.validateClassExists(classId);

        return quizMgr.getQuizOfClass(teacherDto, classDto, new QueryPaging());
    }

    /**
     * 根据班级和课程查询相关的考试记录
     * @param teacherUserId
     * @param classId
     * @param courseId
     * @param queryPaging
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<CourseQuiz> getQuizOfClassByCourse(Long teacherUserId, Long classId, Long courseId, QueryPaging queryPaging) {
        teacherService.validateIsTeacherOfCourse(teacherUserId, classId, courseId);
        ClassDto classDto = classService.validateClassExists(classId);

        return quizMgr.getQuizOfClassByCourse(classDto.getSchoolId(), classDto.getEnrolYear(), courseId, queryPaging);
    }

    /**
     * 查询班级某一次考试的成绩列表
     *
     * @param teacherUserId
     * @param quizId
     * @param classId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<QuizScoreDto> getQuizScoreOfClass(Long teacherUserId, Long quizId, Long classId) {
        // 校验老师和班级的关系
        teacherService.validateIsTeacherOfClass(teacherUserId, classId);
        return quizMgr.getQuizScoreOfClass(quizId, classId);
    }

    /**
     * 设置考试成绩
     *
     * @param operatorUserId
     * @param quizId
     * @param classId
     * @param studentScores
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void setQuizScore(Long operatorUserId, Long quizId, Long classId, Map<Long, Float> studentScores) {
        if (CollectionUtils.isEmpty(studentScores)) {
            throw new BizLogicException(ServiceCodes.QUIZ_SCORE_EMPTY_DATA, "QUIZ_SCORE_EMPTY_DATA");
        }
        // 校验class存在
        ClassDto classDto = classService.validateClassExists(classId);
        // 校验quiz存在
        QuizDto quizDto = quizService.validateQuizExists(quizId);
        // 校验权限
        // 校验教师和class关系
        teacherService.validateIsTeacherOfCourse(operatorUserId, classId, quizDto.getCourseId());
        // 检查class和quiz匹配
        if (!(quizDto.getSchoolId().equals(classDto.getSchoolId()))
                || !(quizDto.getEnrolYear().equals(classDto.getEnrolYear()))) {
            throw new BizLogicException(ServiceCodes.QUIZ_AND_CLASS_NOT_MATCH, String.format("quiz %s does not match class %s.", quizId, classId));
        }
        // 检查student都存在
        List<Long> studentIds = Lists.newArrayList(studentScores.keySet());
        List<StudentDto> studentDtos = userService.getStudentsByIds(studentIds);
        List<Long> foundStudentIds = Lists.newArrayList();
        if (CollectionUtils.isEmpty(studentDtos)) {
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, String.format("Students %s not exist.", JsonUtils.toJson(studentIds)));
        }
        if (studentDtos.size() != studentIds.size()) {
            for (StudentDto studentDto : studentDtos) {
                foundStudentIds.add(studentDto.getId());
            }
            studentIds.removeAll(foundStudentIds);
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_EXISTS, String.format("Students %s not exist.", JsonUtils.toJson(studentIds)));
        }
        // 校验student都是该班级的
        List<Long> notInClassStudentIds = Lists.newArrayList();
        for (StudentDto studentDto : studentDtos) {
            if (!classId.equals(studentDto.getClassId())) {
                notInClassStudentIds.add(studentDto.getId());
            }
        }
        if (!CollectionUtils.isEmpty(notInClassStudentIds)) {
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_IN_CLASS
                    , String.format("Students %s are not in class %s.", JsonUtils.toJson(notInClassStudentIds), classId));
        }

        quizService.setQuizScore(operatorUserId, quizId, classId, studentScores);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Long createSchoolAnnouncement(Long teacherUserId, CreateAnnouncementRequest request) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(teacherUserId);
        if (AnnouncementType.SCHOOL == request.getType()) {
            permissionService.validatePermission(teacherUserId, Permissions.CREATE_SCHOOL_ANNOUNCEMENT);
        } else if (AnnouncementType.GRADE == request.getType()) {
            permissionService.validatePermission(teacherUserId, Permissions.CREATE_SCHOOL_ANNOUNCEMENT);
        } else {
            List<Long> teacherPermissions = teacherRoleService.getPermissionsInClass(teacherDto.getId(), request.getClassId());
            permissionService.validatePermission(teacherUserId, teacherPermissions, Permissions.CREATE_CLASS_ANNOUNCEMENT);
        }

        return announcementService.create(teacherUserId, teacherDto.getSchoolId(), request);
    }

    /**
     * 获取公告列表
     *
     * @param teacherUserId
     * @param classId
     * @param queryPaging
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public PagingInfo<AnnouncementDto> getAnnouncementList(Long teacherUserId, Long classId, QueryPaging queryPaging) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(teacherUserId);
        Page<AnnouncementDto> announcementDtos = announcementService.getAnnouncementsOfClass(null, null, classId, queryPaging);
        return new PagingInfo<AnnouncementDto>(announcementDtos, announcementDtos);
    }

    /**
     * 获取公告详情
     *
     * @param userId
     * @param announcementId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public AnnouncementDto getAnnouncementDetail(Long userId, Long announcementId) {
        AnnouncementDto announcementDto = announcementService.validateAnnouncementExists(announcementId);
        teacherService.validateIsTeacherOfSchool(userId, announcementDto.getSchoolId());
        return announcementDto;
    }

    /**
     * 修改公告
     *
     * @param teacherUserId
     * @param request
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateAnnouncement(Long teacherUserId, UpdateAnnouncementRequest request) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(teacherUserId);
        if (AnnouncementType.SCHOOL == request.getType()) {
            permissionService.validatePermission(teacherUserId, Permissions.CREATE_SCHOOL_ANNOUNCEMENT);
        } else if (AnnouncementType.GRADE == request.getType()) {
            permissionService.validatePermission(teacherUserId, Permissions.CREATE_SCHOOL_ANNOUNCEMENT);
        } else {
            List<Long> teacherPermissions = teacherRoleService.getPermissionsInClass(teacherDto.getId(), request.getClassId());
            permissionService.validatePermission(teacherUserId, teacherPermissions, Permissions.CREATE_CLASS_ANNOUNCEMENT);
        }
        announcementService.update(teacherUserId, teacherDto.getSchoolId(), request);
    }

    /**
     * 创建课件
     *
     * @param teacherUserId
     * @param request
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Long createCourseMaterial(Long teacherUserId, CreateCourseMaterialRequest request) {
        teacherService.validateIsTeacherOfClass(teacherUserId, request.getClassId());
        if (!NumberUtils.isNullOrZero(request.getResourceFileId())) {
            storageService.validateResource(request.getResourceFileId(), teacherUserId, ResourceFileCategory.COURSE_MATERIAL);
        }
        CourseMaterialDto courseMaterialDto = new CourseMaterialDto();
        BeanUtils.copyProperties(request, courseMaterialDto);
        CourseMaterialDto result = courseService.addCourseMaterial(teacherUserId, courseMaterialDto);
        return result.getId();
    }

    /**
     * 获取老师发布的课件列表
     *
     * @param userId
     * @param paging
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<CourseMaterialManagementListItem> getCourseMaterials(Long userId, QueryPaging paging) {
        teacherService.validateHasJoinedSchool(userId);
        return courseMgr.getCourseMaterialList(userId, paging);
    }

    /**
     * 获取课件详细信息
     *
     * @param userId
     * @param courseMaterialId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public CourseMaterialManagementDetailInfo getCourseMaterialDetail(Long userId, Long courseMaterialId) {
        teacherService.validateHasJoinedSchool(userId);
        CourseMaterialDto dto = courseService.getCourseMaterialById(courseMaterialId);
        if (null == dto) {
            throw new BizLogicException(ServiceCodes.COURSE_MATERIAL_NOT_EXISTS, String.format("Course material %s does not exist.", courseMaterialId));
        }

        return courseMgr.getCourseMaterialDetail(courseMaterialId);
    }

    /**
     * 修改课件
     *
     * @param teacherUserId
     * @param request
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCourseMaterial(Long teacherUserId, UpdateCourseMaterialRequest request) {
        teacherService.validateIsTeacherOfClass(teacherUserId, request.getClassId());
        if (!NumberUtils.isNullOrZero(request.getResourceFileId())) {
            storageService.validateResource(request.getResourceFileId(), teacherUserId, ResourceFileCategory.COURSE_MATERIAL);
        }
        CourseMaterialDto courseMaterialDto = new CourseMaterialDto();
        BeanUtils.copyProperties(request, courseMaterialDto);
        courseService.updateCourseMaterial(teacherUserId, courseMaterialDto);
    }

    /**
     * 创建作业
     *
     * @param teacherUserId
     * @param request
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public HomeworkDto createHomework(Long teacherUserId, CreateHomeworkRequest request) {
        TeacherDto teacherDto = teacherService.validateIsTeacherOfCourse(teacherUserId, request.getClassId(), request.getCourseId());
        if (!NumberUtils.isNullOrZero(request.getResourceFileId())) {
            storageService.validateResource(request.getResourceFileId(), teacherUserId, ResourceFileCategory.HOMEWORK);
        }
        HomeworkDto homeworkDto = new HomeworkDto();
        BeanUtils.copyProperties(request, homeworkDto);
        homeworkDto.setTeacherId(teacherDto.getId());
        return homeworkService.createHomework(teacherUserId, homeworkDto);
    }

    /**
     * 编辑作业
     *
     * @param teacherUserId
     * @param request
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateHomework(Long teacherUserId, UpdateHomeworkRequest request) {
        TeacherDto teacherDto = teacherService.validateIsTeacherOfCourse(teacherUserId, request.getClassId(), request.getCourseId());
        if (!NumberUtils.isNullOrZero(request.getResourceFileId())) {
            storageService.validateResource(request.getResourceFileId(), teacherUserId, ResourceFileCategory.HOMEWORK);
        }
        HomeworkDto homeworkDto = homeworkService.validateHomeworkExists(request.getId());
        if (homeworkDto.getStudyDate().getTime() != request.getStudyDate().getTime()) {
            List<HomeworkReviewDto> reviewRecords = homeworkService.getReviewRecordsByHomeworkId(request.getId());
            if (!CollectionUtils.isEmpty(reviewRecords)) {
                // 已经阅过，无法再修改日期, 否则会影响家长审阅
                throw new BizLogicException(ServiceCodes.HOMEWORK_CANNOT_MODIFY_STUDY_DATE
                        , String.format("Homework %s had been reviewed, cannot modify study date.", request.getId()));
            }
        }
        HomeworkDto newValue = new HomeworkDto();
        BeanUtils.copyProperties(request, newValue);
        homeworkDto.setTeacherId(teacherDto.getId());
        homeworkService.updateHomework(teacherUserId, newValue);
    }

    /**
     * 上传课件附件
     *
     * @param userId
     * @param inputStream
     * @param contentDispositionHeader
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Long uploadCourseMaterial(Long userId, InputStream inputStream, FormDataContentDisposition contentDispositionHeader) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        String originFileName = FileUploadUtils.getFileName(contentDispositionHeader);
        String destFileName = String.format("school/%s/course_material/%s/%s"
                , teacherDto.getSchoolId(), UUID.randomUUID(), originFileName);
        ResourceFileDto resourceFileDto = storageService.save(userId, inputStream
                , originFileName, destFileName, ResourceFileCategory.COURSE_MATERIAL);
        return resourceFileDto.getId();
    }

    /**
     * 上传作业附件
     *
     * @param userId
     * @param inputStream
     * @param contentDispositionHeader
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Long uploadHomework(Long userId, InputStream inputStream, FormDataContentDisposition contentDispositionHeader) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        String originFileName = FileUploadUtils.getFileName(contentDispositionHeader);
        String destFileName = String.format("school/%s/homework/%s/%s"
                , teacherDto.getSchoolId(), UUID.randomUUID(), originFileName);
        ResourceFileDto resourceFileDto = storageService.save(userId, inputStream, originFileName, destFileName, ResourceFileCategory.HOMEWORK);
        return resourceFileDto.getId();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<SneakingMessageListItem> getSneakingMessages(Long userId, QueryPaging queryPaging) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        Page<SneakingMessageDto> sneakingMessageDtos = messageService.getSneakingMessagesWithContent(teacherDto.getId(), queryPaging);
        List<SneakingMessageListItem> listItems = Lists.newArrayList();
        Set<Long> studentIds = Sets.newHashSet();
        Set<Long> classIds = Sets.newHashSet();
        for (SneakingMessageDto sneakingMessageDto : sneakingMessageDtos) {
            studentIds.add(sneakingMessageDto.getStudentId());
            classIds.add(sneakingMessageDto.getClassId());
        }

        List<ClassDto> classDtos = classService.getByIds(Lists.newArrayList(classIds));
        Map<Long, ClassDto> classIdToDtoMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(classDtos)) {
            for (ClassDto classDto : classDtos) {
                classIdToDtoMap.put(classDto.getId(), classDto);
            }
        }

        List<StudentDto> studentDtos = userService.getStudentsByIds(Lists.newArrayList(studentIds));
        Map<Long, StudentDto> studentIdToDtoMap = Maps.newHashMap();
        List<Long> studentUserIds = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(studentDtos)) {
            for (StudentDto studentDto : studentDtos) {
                studentUserIds.add(studentDto.getUserId());
                studentIdToDtoMap.put(studentDto.getId(), studentDto);
            }
        }

        List<UserInfoDto> studentUserInfoDtos = userService.getUserInfosByIds(studentUserIds);
        Map<Long, UserInfoDto> userIdToUserInfoMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(studentUserInfoDtos)) {
            for (UserInfoDto studentUserInfoDto : studentUserInfoDtos) {
                userIdToUserInfoMap.put(studentUserInfoDto.getUserId(), studentUserInfoDto);
            }
        }

        for (SneakingMessageDto sneakingMessageDto : sneakingMessageDtos) {
            SneakingMessageListItem item = new SneakingMessageListItem();
            listItems.add(item);

            item.setId(sneakingMessageDto.getId());
            item.setContent(sneakingMessageDto.getContent());
            item.setCreatedAt(sneakingMessageDto.getCreatedAt());

            StudentDto studentDto = studentIdToDtoMap.get(sneakingMessageDto.getStudentId());
            if (null != studentDto) {
                UserInfoDto userInfoDto = userIdToUserInfoMap.get(studentDto.getUserId());
                SimpleStudent simpleStudent = domainConverter.convert(studentDto, userInfoDto);
                item.setStudent(simpleStudent);
            }

            ClassDto classDto = classIdToDtoMap.get(sneakingMessageDto.getClassId());
            if (null != classDto) {
                ClassInfo classInfo = domainConverter.toClassInfo(classDto);
                item.setClassInfo(classInfo);
            }
        }

        teacherService.updateLastViewSneakingMsgTime(userId);

        return new PagingInfo<SneakingMessageListItem>(sneakingMessageDtos, listItems);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public HasNewSneakingMessagesResult hasNewSneakingMessages(Long userId) {
        TeacherDto teacherDto = teacherService.validateIsTeacher(userId);
        boolean hasNewMsg = messageService.hasNewSneakingMessages(teacherDto.getId(), teacherDto.getViewSneakingMsgAt());
        HasNewSneakingMessagesResult result = new HasNewSneakingMessagesResult();
        result.setHasNewSneakingMessages(hasNewMsg);
        result.setLastViewTime(teacherDto.getViewSneakingMsgAt());

        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<HomeworkDto> getHomeworkList(Long userId, Long classId, QueryPaging queryPaging) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        teacherService.validateIsTeacherOfClass(userId, classId);
        Page<HomeworkDto> homeworkDtoPagingInfo = homeworkService.getHomeworkList(teacherDto.getId(), classId, queryPaging);
        return new PagingInfo<HomeworkDto>(homeworkDtoPagingInfo, homeworkDtoPagingInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public HomeworkDto getHomeworkById(Long userId, Long homeworkId) {
        HomeworkDto homeworkDto = homeworkService.validateHomeworkExists(homeworkId);
        teacherService.validateIsTeacherOfCourse(userId, homeworkDto.getClassId(), homeworkDto.getCourseId());

        return homeworkDto;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public HomeworkReviewDetailByCourseDto getHomeworkReviewDetail(Long userId, Long homeworkId, Long classId) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        HomeworkReviewDetailByCourseDto detailByCourseDto = homeworkService.getHomeworkReviewDetail(teacherDto.getId(), homeworkId, classId);
        return detailByCourseDto;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<HomeworkDto> getHomeworkReviewDetail(Long userId, Long classId, Long courseId, QueryPaging queryPaging) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        teacherService.validateIsTeacherOfClass(userId, classId);
        teacherService.validateIsTeacherOfCourse(userId, classId, courseId);
        Page<HomeworkDto> homeworkDtoPagingInfo = homeworkService.getHomeworkList(teacherDto.getId(), classId, queryPaging);
        return new PagingInfo<HomeworkDto>(homeworkDtoPagingInfo, homeworkDtoPagingInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public PagingInfo<SosMessageDto> getStudentMessageList(Long userId, Long classId, QueryPaging queryPaging) {
        TeacherDto teacherDto = teacherService.validateHasJoinedSchool(userId);
        teacherService.validateIsTeacherOfClass(userId, classId);
        Page<SosMessageDto> sosMessageDtoInfo = messageService.getStudentMessageList(userId, classId, queryPaging);
        return new PagingInfo<SosMessageDto>(sosMessageDtoInfo, sosMessageDtoInfo);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<StudentListItem> getStudentsOfClass(Long userId, Long classId) {
        TeacherDto teacherDto = teacherService.validateIsTeacherOfClass(userId, classId);
        List<StudentDto> studentDtos = studentService.getStudentsOfClass(classId);
        List<StudentListItem> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(studentDtos)) {
            List<Long> userIds = Lists.newArrayList();
            Map<Long, StudentListItem> idToDomainMap = Maps.newLinkedHashMap();
            for (StudentDto studentDto : studentDtos) {
                StudentListItem listItem = new StudentListItem();
                listItem.setStudentId(studentDto.getId());
                listItem.setUserId(studentDto.getUserId());
                listItem.setStudentNo(studentDto.getStudentNo());
                listItem.setJoinClassAt(studentDto.getJoinClassAt());

                idToDomainMap.put(studentDto.getUserId(), listItem);
                userIds.add(studentDto.getUserId());
            }

            List<UserDto> userDtos = userService.getUsersByIds(userIds);
            if (!CollectionUtils.isEmpty(userDtos)) {
                for (UserDto userDto : userDtos) {
                    StudentListItem listItem = idToDomainMap.get(userDto.getId());
                    if (null == listItem) {
                        continue;
                    }
                    listItem.setMobileNo(userDto.getMobileNo());
                }
            }

            List<UserInfoDto> userInfoDtos = userService.getUserInfosByIds(userIds);
            if (!CollectionUtils.isEmpty(userInfoDtos)) {
                for (UserInfoDto userInfoDto : userInfoDtos) {
                    StudentListItem listItem = idToDomainMap.get(userInfoDto.getUserId());
                    if (null == listItem) {
                        continue;
                    }
                    listItem.setRealName(userInfoDto.getName());
                }
            }
            Map<Long, StudentListItem.Parent> userIdToParentMap = Maps.newHashMap();
            List<ParentChildDto> parentChildDtos = userService.getParentsByChildUserIds(userIds);
            if (!CollectionUtils.isEmpty(parentChildDtos)) {
                for (ParentChildDto parentChildDto : parentChildDtos) {
                    StudentListItem listItem = idToDomainMap.get(parentChildDto.getChildUserId());
                    if (null == listItem) {
                        continue;
                    }
                    StudentListItem.Parent parent = new StudentListItem.Parent();
                    parent.setUserId(parentChildDto.getParentUserId());
                    parent.setRelation(parentChildDto.getRelation());
                    // 同一个家长，可能同时是多个孩子的家长
                    // 但是同一个家长，他对任何孩子的信息应该是一样的
                    userIdToParentMap.put(parentChildDto.getParentUserId(), parent);

                    List<StudentListItem.Parent> parents = listItem.getParents();
                    if (null == parents) {
                        parents = Lists.newArrayList();
                        listItem.setParents(parents);
                    }
                    parents.add(parent);
                }
                List<Long> parentUserIds = Lists.newArrayList(userIdToParentMap.keySet());
                List<UserDto> parentUserDtos = userService.getUsersByIds(parentUserIds);
                if (!CollectionUtils.isEmpty(parentUserDtos)) {
                    for (UserDto userDto : parentUserDtos) {
                        StudentListItem.Parent parent = userIdToParentMap.get(userDto.getId());
                        if (null == parent) {
                            continue;
                        }
                        parent.setMobileNo(userDto.getMobileNo());
                    }
                }
                List<UserInfoDto> parentUserInfoDtos = userService.getUserInfosByIds(parentUserIds);
                if (!CollectionUtils.isEmpty(parentUserInfoDtos)) {
                    for (UserInfoDto userInfoDto : parentUserInfoDtos) {
                        StudentListItem.Parent parent = userIdToParentMap.get(userInfoDto.getUserId());
                        if (null == parent) {
                            continue;
                        }
                        parent.setRealName(userInfoDto.getName());
                    }
                }
            }

            result = Lists.newArrayList(idToDomainMap.values());
        }
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeStudentFromClass(Long userId, Long classId, Long studentId) {
        TeacherDto teacherDto = teacherService.validateIsTeacher(userId);
        teacherRoleService.validatePermissionInClass(teacherDto, classId, Permissions.REMOVE_STUDENT_FROM_CLASS);

        StudentDto studentDto = studentService.validateIsStudentById(studentId);
        if (!classId.equals(studentDto.getClassId())) {
            throw new BizLogicException(ServiceCodes.STUDENT_NOT_IN_CLASS
                    , String.format("Student %s is not in class %s.", studentId, classId));
        }
        ClassDto classDto = classService.validateClassExists(classId);
        studentService.removeFromClass(studentId);

        if (!NumberUtils.isNullOrZero(classDto.getStudentImGroupId())) {
            imFacade.quitGroup(studentDto.getUserId(), classDto.getStudentImGroupId());
        }

        if (!NumberUtils.isNullOrZero(classDto.getParentImGroupId())) {
            List<UserDto> parentUsers = userService.getParentUsers(studentDto.getUserId());
            if (!CollectionUtils.isEmpty(parentUsers)) {
                for (UserDto userDto : parentUsers) {
                    imFacade.quitGroup(userDto.getId(), classDto.getParentImGroupId());
                }
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void removeCourseMaterial(Long userId, Long coursewareId) {
        CourseMaterialDto dto = courseService.validateCourseMaterialExist(coursewareId);
        if (userId != dto.getCreatedBy()) {
            throw new BizLogicException(ServiceCodes.TEACHER_MATERIAL_NOT_MATCH, "No authentication");
        }
        courseService.deleteCourseMaterial(coursewareId);
//    	courseService.validateCoursesExist(courseId)
    }

    /**
     * 加入班级时，允许选择的入学年份
     *
     * @return
     */
    public List<Integer> getAvailableStudyYears() {
        return schoolService.getAvailableStudyYears();
    }
    
    
    public List<SchoolDto> getAllSchools() {
    	return schoolService.getAllSchools();
    }

    /**
     * 获取老师在一个班级里的权限列表
     *
     * @param teacherUserId
     * @param classId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Long> getPermissionsInClass(Long teacherUserId, Long classId) {
        TeacherDto teacherDto = teacherService.validateIsTeacherOfClass(teacherUserId, classId);
        List<Long> teacherPermissions = teacherRoleService.getPermissionsInClass(teacherDto.getId(), classId);
        return teacherPermissions;
    }
    
    /**
     * 获取老师在一个学校里的权限列表
     *
     * @param teacherUserId
     * @param schoolId
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Long> getPermissionsInSchool(Long teacherUserId, Long schoolId) {
        TeacherDto teacherDto = teacherService.validateIsTeacherOfSchool(teacherUserId, schoolId);
        List<Long> teacherPermissions = teacherRoleService.getPermissionsInSchool(teacherDto.getId(), schoolId);
        return teacherPermissions;
    }

    public UserInfoDto getLoginTeacher(Long teacherUserId) {
        return userService.getUserInfoById(teacherUserId);
    }

    public void uploadCourseQuiz(Long teacherUserId, Long classId, Long quizId, InputStream inputStream, FormDataContentDisposition contentDispositionHeader) {
        TeacherDto teacherDto = teacherService.validateIsTeacherOfClass(teacherUserId, classId);
        List<List<String>> quizContents = UploadCourseQuizFile.readExcel(inputStream, contentDispositionHeader);
        Map<Long, Float> studentScores = new HashMap<Long, Float>();
        for (List<String> quizContent : quizContents) {
            String studentNo = quizContent.get(0);
            String userName = quizContent.get(1);
            StudentDto studentDto = studentService.validStudentByNoName(studentNo, userName);
            if (studentDto == null) {
                continue;
            }
            Long studentId = studentDto.getId();
            Float scores = Float.parseFloat(quizContent.get(3));
            studentScores.put(studentId, scores);

        }
        quizService.setQuizScore(teacherUserId, quizId, classId, studentScores);


    }
    
    public Boolean hasJoinedSchool(Long userId) {
    	TeacherDto teacher = userService.getTeacherByUserId(userId);
    	return teacher != null && null != teacher.getSchoolId() && !Long.valueOf(0).equals(teacher.getSchoolId());
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<HomeworkReviewDto> findReviewList(Long userId, Long homeworkId) {
        teacherService.validateIsTeacher(userId);
        return homeworkService.findReviewList(homeworkId);
    }


    public List<CourseScheduleDto> findOneWeekCourses(Long userId, Long classId) {
        TeacherDto teacherDto = teacherService.validateIsTeacher(userId);
        return teacherService.findOneWeekCourses(teacherDto, classId);
    }

}