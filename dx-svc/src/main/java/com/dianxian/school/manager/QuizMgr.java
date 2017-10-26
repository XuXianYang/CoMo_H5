package com.dianxian.school.manager;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.utils.lang.NumberUtils;
import com.dianxian.core.utils.time.DateUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.*;
import com.dianxian.school.service.ClassService;
import com.dianxian.school.service.CourseService;
import com.dianxian.school.service.QuizService;
import com.dianxian.school.service.StudentService;
import com.dianxian.user.dto.StudentDto;
import com.dianxian.user.dto.TeacherDto;
import com.github.pagehelper.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by xuwenhao on 2016/5/21.
 */
@Component
public class QuizMgr {
    @Autowired
    ClassService classService;
    @Autowired
    StudentService studentService;
    @Autowired
    QuizService quizService;
    @Autowired
    CourseService courseService;

    public List<CourseQuiz> getRecentQuizes(Long classId) {
        List<CourseDto> courseDtos = courseService.getAll();
        if (CollectionUtils.isEmpty(courseDtos)) {
            throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS, "No course exists.");
        }
        ClassDto classDto = classService.validateClassExists(classId);
        List<QuizDto> quizDtos = quizService.getRecentQuizes(classDto.getSchoolId(), classDto.getEnrolYear());
        return convertQuiz(quizDtos, courseDtos);
    }

    private List<CourseQuiz> convertQuiz(List<QuizDto> quizDtos, List<CourseDto> courseDtos) {
        List<CourseQuiz> result = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(quizDtos)) {
            for (QuizDto quizDto : quizDtos) {
                CourseDto courseDto = findById(courseDtos, quizDto.getCourseId());
                if (null == courseDto) {
                    throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS
                            , String.format("Course %s does not exist.", quizDto.getCourseId()));
                }

                result.add(convertQuiz(quizDto, courseDto));
            }
        }
        return result;
    }

    public CourseQuiz convertQuiz(QuizDto quizDto, CourseDto courseDto) {
        CourseQuiz courseQuiz = new CourseQuiz();
        Quiz quiz = convert(quizDto);
        Course course = convert(courseDto);

        courseQuiz.setQuiz(quiz);
        courseQuiz.setCourse(course);

        return courseQuiz;
    }

    private Course convert(CourseDto courseDto) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDto, course);
        return course;
    }

    private Quiz convert(QuizDto quizDto) {
        Quiz quiz = new Quiz();
        BeanUtils.copyProperties(quizDto, quiz);
        quiz.setDaysAfterNow(DateUtils.dateDiff(new Date(), quizDto.getStartTime()));
        return quiz;
    }

    private CourseDto findById(List<CourseDto> courseDtos, Long id) {
        for (CourseDto courseDto : courseDtos) {
            if (id.equals(courseDto.getId())) {
                return courseDto;
            }
        }
        return null;
    }

    /**
     * 获取班级的考试列表
     * @param schoolId
     * @param enrolYear
     * @return
     */
    public List<CourseQuiz> getQuizOfClass(Long schoolId, Integer enrolYear) {
        List<QuizDto> quizDtos = quizService.getQuizOfClass(schoolId, enrolYear);
        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for(QuizDto quizDto: quizDtos){
            CourseDto courseDto = courseService.validateCoursesExist(quizDto.getCourseId());
            courseDtos.add(courseDto);
        }
        return convertQuiz(quizDtos, courseDtos);
    }

    public PagingInfo<CourseQuiz> getQuizOfClass(TeacherDto teacherDto, ClassDto classDto, QueryPaging queryPaging) {
        List<CourseTeacherDto> courseTeacherDtos = courseService.getAssignedCoursesOfTeacherInClass(teacherDto.getId(), classDto.getId());
        if (CollectionUtils.isEmpty(courseTeacherDtos)) {
            throw new BizLogicException(ServiceCodes.TEACHER_NOT_A_TEACHER_OF_CLASS, "Not a teacher of class " + classDto.getId());
        }
        List<Long> courseIds = Lists.newArrayList();
        List<CourseDto> courseDtos = Lists.newArrayList();
        for (CourseTeacherDto courseTeacherDto : courseTeacherDtos) {
            courseIds.add(courseTeacherDto.getCourseId());
            courseDtos.add(courseService.validateCoursesExist(courseTeacherDto.getCourseId()));
        }
        Page<QuizDto> quizDtos = quizService.getQuizOfClassByCourse(classDto.getSchoolId(), classDto.getEnrolYear(), courseIds, queryPaging);
        List<CourseQuiz> resultDomains = convertQuiz(quizDtos, courseDtos);
        return new PagingInfo<CourseQuiz>(quizDtos, resultDomains);
    }

    public List<QuizScoreDto> getQuizScoreOfClass(Long quizId, Long classId) {
        return quizService.getQuizScoreOfClass(quizId, classId);
    }

    /**
     * 根据学年，考试书签（月考，期中，期末），获取学生的成绩汇总
     * @param studentUserId
     * @param studyYear
     * @param studyTerm
     * @param category
     * @return
     */
    public QuizScoreSummary getQuizScoreReportOfStudent(Long studentUserId, Integer studyYear, Integer studyTerm, Integer category) {
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);

        List<QuizDto> quizDtos = quizService.getQuizOfScoreReport(studentDto.getSchoolId()
                                , studentDto.getEnrolYear(), studyYear, studyTerm, category);
        List<QuizScoreDto> quizScoreDtos = quizService.getQuizScoresOfStudent(studentDto.getId(), quizDtos);
        List<CourseDto> courseDtos = courseService.getAll();
        List<CourseQuizScore> scoreList = convertQuizScore(quizDtos, quizScoreDtos, courseDtos);
        for (CourseQuizScore score : scoreList) {
            // 统一逻辑里如果没有出成绩则不会填studentId
            score.setStudentId(studentDto.getId());
        }

        QuizScoreSummary summary = new QuizScoreSummary();
        summary.setStudyYear(studyYear);
        summary.setStudyTerm(studyTerm);
        summary.setCategory(category);
        summary.setQuizScores(scoreList);
        return summary;
    }

    /**
     * 统一将考试，成绩和课程记录转换成对应的业务对象
     * @param quizDtos
     * @param quizScoreDtos
     * @param courseDtos
     * @return
     */
    private List<CourseQuizScore> convertQuizScore(List<QuizDto> quizDtos, List<QuizScoreDto> quizScoreDtos, List<CourseDto> courseDtos) {
        Map<Long, QuizScoreDto> quizScoreDtoMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(quizScoreDtos)) {
            for (QuizScoreDto quizScoreDto : quizScoreDtos) {
                quizScoreDtoMap.put(quizScoreDto.getQuizId(), quizScoreDto);
            }
        }
        Map<Long, CourseDto> courseDtoMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(courseDtos)) {
            for (CourseDto courseDto : courseDtos) {
                courseDtoMap.put(courseDto.getId(), courseDto);
            }
        }

        List<CourseQuizScore> result = Lists.newArrayList();
        for (QuizDto quizDto : quizDtos) {
            CourseDto courseDto = courseDtoMap.get(quizDto.getCourseId());
            if (null == courseDto) {
                throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS
                        , String.format("quizId: %s, courseId: %s", quizDto.getId(), quizDto.getCourseId()));
            }

            CourseQuizScore courseQuizScore = new CourseQuizScore();
            QuizScoreDto quizScoreDto = quizScoreDtoMap.get(quizDto.getId());
            if (null != quizScoreDto) {
                // 考试成绩已录入
                courseQuizScore.setScore(quizScoreDto.getScore());
                courseQuizScore.setStudentId(quizScoreDto.getStudentId());
            }
            Quiz quiz = convert(quizDto);
            Course course = convert(courseDto);
            courseQuizScore.setQuiz(quiz);
            courseQuizScore.setCourse(course);

            result.add(courseQuizScore);
        }
        return result;
    }

    /**
     * 获取单门课程的成绩
     * @param studentUserId
     * @param courseId
     * @param studyYear
     * @param category
     * @return
     */
    public CourseScoreReport getCourseScoreReport(Long studentUserId, Long courseId, Integer studyYear, Integer category) {
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        CourseScoreReport report = new CourseScoreReport();
        QuizDto quizDto = quizService.getLatestQuiz(studentDto.getClassId(), courseId, studyYear, category);
        if (null != quizDto) {
            CourseQuizScore courseQuizScore = new CourseQuizScore();
            report.setLatestScore(courseQuizScore);

            courseQuizScore.setQuiz(convert(quizDto));
            CourseDto courseDto = courseService.validateCoursesExist(courseId);
            courseQuizScore.setCourse(convert(courseDto));

            ClassDto classDto = classService.validateClassExists(studentDto.getClassId());
            QuizScoreDto quizScoreDto = quizService.getScoreOfStudent(quizDto.getId(), studentDto.getId());
            if (null != quizScoreDto) {
                courseQuizScore.setScore(quizScoreDto.getScore());
            }

            Float classAvg = quizService.getClassAvgScore(quizDto.getId(), classDto.getId());
            report.setClassAvgScore(classAvg);
            Float gradeAvg = quizService.getGradeAvgScore(quizDto.getId(), classDto.getSchoolId(), classDto.getEnrolYear());
            report.setGradeAvgScore(gradeAvg);
        }
        return report;
    }

    /**
     * 获取单门课程的成绩趋势
     * @param studentUserId
     * @param courseId
     * @param studyYear
     * @return
     */
    public List<CourseQuizScore> getCourseScoreTrends(Long studentUserId, Long courseId, Integer studyYear) {
        StudentDto studentDto = studentService.validateHasJoinedClass(studentUserId);
        List<CourseQuizScore> result = Lists.newArrayList();
        List<QuizDto> quizDtos = quizService.getCourseQuizOfClassByStudyYear(studentDto.getClassId(), courseId, studyYear);

        if (!CollectionUtils.isEmpty(quizDtos)) {
            List<QuizScoreDto> quizScoreDtos = quizService.getQuizScoresOfStudent(studentDto.getId(), quizDtos);

            Map<Long, QuizScoreDto> quizScoreDtoMap = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(quizScoreDtos)) {
                for (QuizScoreDto quizScoreDto : quizScoreDtos) {
                    quizScoreDtoMap.put(quizScoreDto.getQuizId(), quizScoreDto);
                }
            }

            for (QuizDto quizDto : quizDtos) {
                CourseQuizScore courseQuizScore = new CourseQuizScore();
                result.add(courseQuizScore);

                courseQuizScore.setQuiz(convert(quizDto));
                QuizScoreDto quizScoreDto = quizScoreDtoMap.get(quizDto.getId());
                if (null != quizScoreDto) {
                    courseQuizScore.setScore(quizScoreDto.getScore());
                } else {
                    courseQuizScore.setScore(0f);
                }
            }
        }
        return result;
    }

    /**
     * 用于支持学校管理员获取学校的考试列表
     * @param schoolId
     * @param queryPaging
     * @return
     */
    public PagingInfo<CourseQuiz> getQuizOfSchool(Long schoolId, QueryPaging queryPaging) {
        Page<QuizDto> quizDtos = quizService.getQuizOfSchool(schoolId, queryPaging);
        List<CourseDto> courseDtos = new ArrayList<CourseDto>();
        for(QuizDto quizDto: quizDtos){
            CourseDto courseDto = courseService.validateCoursesExist(quizDto.getCourseId());
            courseDtos.add(courseDto);
        }
        List<CourseQuiz> resultDomains = convertQuiz(quizDtos, courseDtos);
        return new PagingInfo<CourseQuiz>(quizDtos, resultDomains);
    }

    /**
     * 根据班级和课程查询相关的考试记录
     * @param schoolId
     * @param enrolYear
     * @param courseId
     * @param queryPaging
     * @return
     */
    public PagingInfo<CourseQuiz> getQuizOfClassByCourse(Long schoolId, Integer enrolYear, Long courseId, QueryPaging queryPaging) {
        Page<QuizDto> quizDtos = quizService.getQuizOfClassByCourse(schoolId, enrolYear, Lists.newArrayList(courseId), queryPaging);
        CourseDto courseDto = courseService.validateCoursesExist(courseId);
        List<CourseQuiz> resultDomains = convertQuiz(quizDtos, Lists.newArrayList(courseDto));
        return new PagingInfo<CourseQuiz>(quizDtos, resultDomains);
    }
}
