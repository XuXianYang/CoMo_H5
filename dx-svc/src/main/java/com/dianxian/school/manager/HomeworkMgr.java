package com.dianxian.school.manager;

import com.dianxian.core.exception.BizLogicException;
import com.dianxian.core.utils.lang.MapUtils;
import com.dianxian.school.consts.ServiceCodes;
import com.dianxian.school.domain.HomeworkReviewListResult;
import com.dianxian.school.domain.SimpleTeacher;
import com.dianxian.school.dto.CourseDto;
import com.dianxian.school.dto.HomeworkDto;
import com.dianxian.school.dto.HomeworkReviewDto;
import com.dianxian.school.service.CourseService;
import com.dianxian.school.service.HomeworkService;
import com.dianxian.school.service.TeacherService;
import com.dianxian.user.service.UserService;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by XuWenHao on 6/7/2016.
 */
@Component
public class HomeworkMgr {
    @Autowired
    HomeworkService homeworkService;
    @Autowired
    CourseMgr courseMgr;
    @Autowired
    CourseService courseService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    UserService userService;

    public HomeworkReviewListResult getHomeworkReviewStatus(Long studentId, Long classId, Date studyDate) {
        HomeworkReviewListResult result = new HomeworkReviewListResult();
        List<HomeworkDto> homeworkDtos = homeworkService.getHomeworkOfClass(classId, studyDate);
        if (!CollectionUtils.isEmpty(homeworkDtos)) {
            Map<Long ,CourseDto> courseDtoMap = getAllCoursesMap();

            List<HomeworkReviewDto> homeworkReviewDtos = homeworkService.getReviewRecordsOfStudent(studentId, studyDate);
            Map<Long, HomeworkReviewDto> homeworkReviewDtoMap = Maps.newHashMap();
            if (!CollectionUtils.isEmpty(homeworkReviewDtos)) {
                for (HomeworkReviewDto dto : homeworkReviewDtos) {
                    homeworkReviewDtoMap.put(dto.getHomeworkId(), dto);
                }
            }

            Map<Long, HomeworkReviewListResult.CourseHomeworks> courseHomeworksMap = Maps.newHashMap();
            for (HomeworkDto homeworkDto : homeworkDtos) {
                CourseDto courseDto = courseDtoMap.get(homeworkDto.getCourseId());
                if (null == courseDto) {
                    throw new BizLogicException(ServiceCodes.COURSE_NOT_EXISTS, "Invalid course: " + homeworkDto.getCourseId());
                }
                HomeworkReviewListResult.CourseHomeworks courseHomeworks = courseHomeworksMap.get(homeworkDto.getCourseId());
                if (null == courseHomeworks) {
                    courseHomeworks = new HomeworkReviewListResult.CourseHomeworks();
                    courseHomeworksMap.put(homeworkDto.getCourseId(), courseHomeworks);
                    courseHomeworks.setCourse(courseMgr.convert(courseDto));
                    SimpleTeacher teacher = teacherService.getTeacherDomainByTeacherId(homeworkDto.getTeacherId());;
                    courseHomeworks.setTeacher(teacher);
                    courseHomeworks.setHomeworks(new ArrayList<HomeworkReviewListResult.HomeworkReview>());
                }
                HomeworkReviewListResult.HomeworkReview homeworkReview = new HomeworkReviewListResult.HomeworkReview();
                homeworkReview.setHomeworkId(homeworkDto.getId());
                homeworkReview.setName(homeworkDto.getName());
                homeworkReview.setDescription(homeworkDto.getDescription());
                boolean reviewed = homeworkReviewDtoMap.containsKey(homeworkDto.getId());
                homeworkReview.setReviewed(reviewed);
                // 有作业被阅过了，那么就认为这门课被阅过了
                if (reviewed) {
                    courseHomeworks.setReviewed(reviewed);
                }

                courseHomeworks.getHomeworks().add(homeworkReview);
            }
            result.setCourses(new ArrayList<HomeworkReviewListResult.CourseHomeworks>(courseHomeworksMap.values()));
        }
        result.setStudyDate(studyDate);

        return result;
    }

    private Map<Long, CourseDto> getAllCoursesMap() {
        List<CourseDto> courseDtos = courseService.getAll();
        return MapUtils.toMap(courseDtos);
    }
}
