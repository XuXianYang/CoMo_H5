package com.dianxian.school.service;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.dao.QuizDtoMapper;
import com.dianxian.school.dao.QuizScoreDtoMapper;
import com.dianxian.school.domain.Quiz;
import com.dianxian.school.dto.AnnouncementDto;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.QuizDto;
import com.dianxian.school.dto.QuizScoreDto;
import com.dianxian.school.utils.SchoolUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by lenovo on 2016/5/1.
 */
@Component
public class QuizService {
    @Autowired
    QuizDtoMapper quizDtoMapper;
    @Autowired
    QuizScoreDtoMapper quizScoreDtoMapper;
    @Autowired
    ClassService classService;

    public QuizDto create(Long operatorUserId, QuizDto params) {
        params.setCreatedBy(operatorUserId);
        quizDtoMapper.insert(params);
        return quizDtoMapper.selectByPrimaryKey(params.getId());
    }

    public void update(Long operatorUserId, QuizDto params) {
        params.setUpdatedBy(operatorUserId);
        params.setUpdatedAt(new Date());
        quizDtoMapper.updateByPrimaryKeySelective(params);
    }

    public List<QuizDto> getRecentQuizes(Long schoolId, Integer enrolYear) {
        return quizDtoMapper.getRecentQuizes(schoolId, enrolYear);
    }

    public List<QuizDto> getQuizOfClass(Long schoolId, Integer enrolYear) {
        return quizDtoMapper.getCourseQuizOfClass(schoolId, enrolYear);
    }

    public QuizDto validateQuizExists(Long quizId) {
        QuizDto quizDto = quizDtoMapper.selectByPrimaryKey(quizId);
        if (null == quizDto) {
            throw new BizLogicException(ServiceCodes.QUIZ_NOT_EXISTS, "Quiz not exists.");
        }
        return quizDto;
    }

    public List<QuizScoreDto> getQuizScoreOfClass(Long quizId, Long classId) {
        return quizScoreDtoMapper.getScoreOfClass(quizId, classId);
    }

    public void setQuizScore(Long operatorUserId, Long quizId, Long classId, Map<Long, Float> studentScores) {
        ClassDto classDto = classService.validateClassExists(classId);
        QuizDto quizDto = quizDtoMapper.selectByPrimaryKey(quizId);
        List<QuizScoreDto> newQuizScoreDtos = Lists.newArrayList();
        for (Map.Entry<Long, Float> entry : studentScores.entrySet()) {
            QuizScoreDto quizScoreDto = quizScoreDtoMapper.getScoreOfStudent(quizId, entry.getKey());
            if (null == quizScoreDto) {
                quizScoreDto = new QuizScoreDto();
                quizScoreDto.setQuizId(quizId);
                quizScoreDto.setCourseId(quizDto.getCourseId());
                quizScoreDto.setSchoolId(classDto.getSchoolId());
                quizScoreDto.setEnrolYear(classDto.getEnrolYear());
                quizScoreDto.setClassId(classDto.getId());
                quizScoreDto.setStudentId(entry.getKey());
                quizScoreDto.setScore(entry.getValue());
                quizScoreDto.setCreatedBy(operatorUserId);
                newQuizScoreDtos.add(quizScoreDto);
            } else {
                quizScoreDto.setScore(entry.getValue());
                quizScoreDto.setUpdatedBy(operatorUserId);
                quizScoreDtoMapper.updateScore(quizScoreDto);
            }
        }
        if (!CollectionUtils.isEmpty(newQuizScoreDtos)) {
            quizScoreDtoMapper.batchInsert(newQuizScoreDtos);
        }
    }

    /**
     * 获取学生成绩汇总中，包含的考试
     * @param schoolId
     * @param enrolYear
     * @param studyYear
     * @param category
     * @return
     */
    public List<QuizDto> getQuizOfScoreReport(Long schoolId, Integer enrolYear
            , Integer studyYear, Integer studyTerm, Integer category) {
        List<QuizDto> queryResult = quizDtoMapper.getQuizOfScoreReport(schoolId, enrolYear, studyYear, studyTerm, category);
        if (CollectionUtils.isEmpty(queryResult)) {
            return queryResult;
        }
        Map<Long, QuizDto> quizCourseMap = new LinkedHashMap<Long, QuizDto>();
        for (QuizDto quizDto : queryResult) {
            // 每个课程保留最后一次考试
            // 由于已经按时间排序，只要保留最早的那个
            if (!quizCourseMap.containsKey(quizDto.getCourseId())) {
                quizCourseMap.put(quizDto.getCourseId(), quizDto);
            }
        }

        return new ArrayList<QuizDto>(quizCourseMap.values());
    }

    /**
     * 根据考试，获取对应的分数
     * @param studentId
     * @param quizDtos
     * @return
     */
    public List<QuizScoreDto> getQuizScoresOfStudent(Long studentId, List<QuizDto> quizDtos) {
        if (CollectionUtils.isEmpty(quizDtos)) {
            return null;
        }
        List<Long> quizIds = Lists.newArrayList();
        for (QuizDto quizDto : quizDtos) {
            quizIds.add(quizDto.getId());
        }
        return quizScoreDtoMapper.getScoresOfStudent(quizIds, studentId);
    }

    /**
     *
     * @param classId
     * @param courseId
     * @return
     */
    public QuizDto getLatestQuiz(Long classId, Long courseId, Integer studyYear, Integer category) {
        ClassDto classDto = classService.validateClassExists(classId);
        return quizDtoMapper.getLatestQuiz(courseId, classDto.getSchoolId(), classDto.getEnrolYear(), studyYear, category);
    }

    /**
     * 获取单次考试成绩
     * @param quizId
     * @param studentId
     * @return
     */
    public QuizScoreDto getScoreOfStudent(Long quizId, Long studentId) {
        return quizScoreDtoMapper.getScoreOfStudent(quizId, studentId);
    }

    /**
     * 班级平均分
     * @param quizId
     * @param classId
     * @return
     */
    public Float getClassAvgScore(Long quizId, Long classId) {
        Float score = quizScoreDtoMapper.getClassAvgScore(quizId, classId);
        return SchoolUtils.formatScore(score);
    }

    /**
     * 年级平均分
     * @param quizId
     * @param schoolId
     * @param enrolYear
     * @return
     */
    public Float getGradeAvgScore(Long quizId, Long schoolId, Integer enrolYear) {
        Float score = quizScoreDtoMapper.getGradeAvgScore(quizId, schoolId, enrolYear);
        return SchoolUtils.formatScore(score);
    }

    /**
     * 获取学年中某一课程的所有考试
     * @param classId
     * @param courseId
     * @param studyYear
     * @return
     */
    public List<QuizDto> getCourseQuizOfClassByStudyYear(Long classId, Long courseId, Integer studyYear) {
        ClassDto classDto = classService.validateClassExists(classId);
        return quizDtoMapper.getCourseQuizOfClassByStudyYear(courseId, classDto.getSchoolId(), classDto.getEnrolYear(), studyYear);
    }

    /**
     * 用于支持学校管理员获取学校的考试列表
     * @param schoolId
     * @param queryPaging
     * @return
     */
    public Page<QuizDto> getQuizOfSchool(Long schoolId, QueryPaging queryPaging) {
        Page<QuizDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        quizDtoMapper.getQuizOfSchool(schoolId);
        return page;
    }

    /**
     * 根据班级和课程查询相关的考试记录
     * @param schoolId
     * @param enrolYear
     * @param courseIds
     * @return
     */
    public Page<QuizDto> getQuizOfClassByCourse(Long schoolId, Integer enrolYear, List<Long> courseIds, QueryPaging queryPaging) {
        Page<QuizDto> page = PageHelper.startPage(queryPaging.getPageNum(), queryPaging.getPageSize());
        quizDtoMapper.getQuizOfClassByCourse(schoolId, enrolYear, courseIds);
        return page;
    }

    /**
     * 根据考试Id获取考试详情
     * @param quizId
     * @return
     */
    public QuizDto getQuizById(Long quizId) {
        return quizDtoMapper.selectByPrimaryKey(quizId);
    }
}
