package com.dianxian.school.facade;

import com.dianxian.core.resource.QueryPaging;
import com.dianxian.school.domain.CourseSchedule;
import com.dianxian.school.request.teacher.JoinClassRequest;
import com.dianxian.testframework.AbstractServiceTest;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.core.utils.lang.StringUtils;
import com.dianxian.school.domain.SetCourseScheduleItem;
import com.dianxian.school.dto.ClassDto;
import com.dianxian.school.dto.QuizCategory;
import com.dianxian.school.request.teacher.CreateQuizRequest;
import com.dianxian.testframework.ClassPathResourceLoader;
import com.dianxian.testframework.MockFormDataContentDisposition;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by XuWenHao on 4/27/2016.
 */
public class TeacherFacadeTest extends AbstractServiceTest {
    @Autowired
    TeacherFacade teacherFacade;

    @Test
    public void test_joinClass() {
        ClassDto classDto = new ClassDto();
        classDto.setName(StringUtils.getRandomString(12));
        classDto.setEnrolYear(2000);
        classDto.setClassNumber(1);
        JoinClassRequest request = new JoinClassRequest();
        BeanUtils.copyProperties(classDto, request);
        request.setCourseIds(Lists.newArrayList(1L, 2L));
        teacherFacade.joinClass(5L, request);
    }

    @Test
    public void test_setCourseSchedule() {
        List<SetCourseScheduleItem> params = Lists.newArrayList();
        for (int i = 1; i< 4; i++) {
            SetCourseScheduleItem param = new SetCourseScheduleItem();
            param.setCourseId(Long.valueOf(i));
            param.setDayOfWeek(i);
            param.setLessonOfDay(i);
            params.add(param);
        }
        teacherFacade.setCourseSchedule(3L, 1L, params);
    }

    @Test
    public void test_getCourseSchedule() {
        List<CourseSchedule> result = teacherFacade.getCourseSchedule(3L, 1L);
        logger.info(JsonUtils.toJson(result));
    }

    @Test
    public void test_createQuiz() {
        CreateQuizRequest params = new CreateQuizRequest();
        params.setName(StringUtils.getRandomString(16));
        params.setCourseId(1L);
        params.setEnrolYear(1999);
        params.setStartTime(new Date());
        params.setEndTime(new Date());
        params.setCategory(QuizCategory.FinalExam.value());
        params.setStudyYear(1999);
        params.setStudyTerm(2);
        params.setStudyMonth(0);
        params.setDescription(StringUtils.getRandomString(16));

        teacherFacade.createQuiz(101L, params);
    }

    @Test
    public void test_createQuiz_WithNoStudyYear() {
        CreateQuizRequest params = new CreateQuizRequest();
        params.setName(StringUtils.getRandomString(16));
        params.setCourseId(1L);
        params.setEnrolYear(1999);
        params.setStartTime(new Date());
        params.setEndTime(new Date());
        params.setCategory(QuizCategory.FinalExam.value());
        params.setStudyTerm(1);
//        params.setStudyTerm(2);
        params.setStudyMonth(0);
        params.setDescription(StringUtils.getRandomString(16));

        teacherFacade.createQuiz(101L, params);
    }

    @Test
    public void test_createQuiz_WithNoStudyTerm() {
        CreateQuizRequest params = new CreateQuizRequest();
        params.setName(StringUtils.getRandomString(16));
        params.setCourseId(1L);
        params.setEnrolYear(1999);
        params.setStartTime(new Date());
        params.setEndTime(new Date());
        params.setCategory(QuizCategory.FinalExam.value());
        params.setStudyYear(1999);
        params.setStudyMonth(0);
        params.setDescription(StringUtils.getRandomString(16));

        teacherFacade.createQuiz(101L, params);
    }

    @Test
    public void test_uploadHomework() throws UnsupportedEncodingException {
        Long resourceFileId = teacherFacade.uploadHomework(4L
                , ClassPathResourceLoader.getResourceAsStream("test.jpg"), new MockFormDataContentDisposition("file"
                , new String("测试图片.jpg".getBytes("UTF-8"), "ISO-8859-1")));
    }

    @Test
    public void test_uploadCourseMaterial() throws UnsupportedEncodingException {
        Long resourceFileId = teacherFacade.uploadCourseMaterial(4L
                , ClassPathResourceLoader.getResourceAsStream("test.jpg"), new MockFormDataContentDisposition("file"
                , new String("测试图片.jpg".getBytes("UTF-8"), "ISO-8859-1")));
    }

    @Test
    public void test_getStudentsOfClass() {
        teacherFacade.getStudentsOfClass(175L, 31L);
    }

    @Test
    public void test_removeStudentFromClass() {
        teacherFacade.removeStudentFromClass(175L, 31L, 48L);
    }

    @Test
    public void test_getCourseMaterialDetail() {
        teacherFacade.getCourseMaterialDetail(112L, 36L);
        teacherFacade.getCourseMaterialDetail(112L, 37L);
    }

    @Test
    public void test_getCourseMaterials() {
        teacherFacade.getCourseMaterials(112L, new QueryPaging());
    }

    @Test
    public void test_getStudentMessageList(){
        teacherFacade.getStudentMessageList(29L, 5L, new QueryPaging());
    }

    @Test
    public void test_getQuizOfSchool() {
        teacherFacade.getQuizList(28L, new QueryPaging());
    }

    @Test
    public void test_getQuizOfClass() {
        teacherFacade.getQuizOfClassByTeacher(29L, 5L);
    }

    @Test
    public void test_getQuizOfClassByCourse() {
        teacherFacade.getQuizOfClassByCourse(29L, 5L, 1L, new QueryPaging());
    }

    @Test
    public void test_getQuizDetail() {
        teacherFacade.getQuizDetail(28L, 12L);
    }

    @Test
    public void test_getCourseAssignmentsOfTeacher() {
        teacherFacade.getCourseAssignmentsOfTeacher(112L);
    }

    @Test
    public void test_getSneakingMessages() {
        teacherFacade.getSneakingMessages(5L, new QueryPaging());
    }
}
