package com.dianxian.web.controller;

import com.dianxian.core.resource.GenericResponse;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.resource.ResponseBuilder;
import com.dianxian.core.spring.mvc.AbstractAjaxController;
import com.dianxian.school.domain.Course;
import com.dianxian.school.facade.TeacherFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Produces;
import java.util.List;

/**
 * Created by xuwenhao on 2016/9/4.
 */
@Controller
@RequestMapping(value = "teacher")
public class TeacherAjaxController extends AbstractAjaxController {
    @Autowired
    private TeacherFacade teacherFacade;

    @RequestMapping(path = {"my-class/list-course"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public GenericResponse<List<Course>> listCourse(
            @RequestParam(name = "classId", required = true) Long classId,
            ModelMap model) {
        List<Course> courses = teacherFacade.getAssignedCoursesOfTeacherInClass(getCurrentUserId(), classId);
        return ResponseBuilder.buildSuccessResponse(courses);
    }
}
