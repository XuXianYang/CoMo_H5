package com.dianxian.web.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.dianxian.school.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.spring.mvc.AbstractWebController;
import com.dianxian.core.utils.json.JsonUtils;
import com.dianxian.school.dto.HomeworkDto;
import com.dianxian.school.dto.QuizScoreDto;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.user.domain.Permissions;
import com.dianxian.user.dto.UserInfoDto;
import com.dianxian.web.utils.DisplayUtils;
import com.google.common.collect.Lists;

/**
 * Created by XuWenHao on 8/5/2016.
 */
@Controller
@RequestMapping(value = "teacher")
public class TeacherWebController extends AbstractWebController {

	@Autowired
	private TeacherFacade teacherFacade;
	
	@ModelAttribute  
    public void headModel(ModelMap model) { 
		Long userId = getCurrentUserId();
		model.put("currentUser", teacherFacade.getLoginTeacher(userId));
		
		Date today = Calendar.getInstance().getTime();
		model.put("currentDate", DisplayUtils.formatChineseDate(today));
		model.put("currentWeekDay", DisplayUtils.getWeekDay(today));
		
		model.put("currentSchool", teacherFacade.getSchoolInfoByTeacherUserId(userId));
		
		HasNewSneakingMessagesResult result = teacherFacade.hasNewSneakingMessages(getCurrentUserId());
		model.put("hasNewSneakingMessagesResult", result);
//		model.put("loginTeacher", teacherFacade.getLoginTeacher(userId));
//		teacherFacade.getSchoolInfoByClassId(classId)
//       model.addAttribute("attributeName", abc);  
    } 

	/**
	 * ===========================================
	 *
	 * 我的班级
	 *
	 * ===========================================
	 */
	@RequestMapping({"", "/", "index", "/class/student/", "/class/student"})
	public String index(
			@RequestParam(name="classId", required=false) Long classId,
			ModelMap model, RedirectAttributes attrs) {
		attrs.addAttribute("classId", classId);
		return "redirect:/web/teacher/class/student/list";
	}

	@RequestMapping("/class/student/list")
	public String listStudents(
			@RequestParam(name="classId", required=false) Long classId,
			ModelMap model) {

		// 1, 获取老师所加入的所有班级
		Long userId = getCurrentUserId();
		List<SchoolClassOfTeacher> joinedClasses = teacherFacade.getJoinedClasses(userId);

		if (null == joinedClasses || joinedClasses.isEmpty()) {
			// 当老师没有加入任何班级时跳到加入页面
			return redirectJoinClass();
		}
		model.put("joinedClasses", joinedClasses);

		// 2，获取指定班级的班主任和班级信息
		SchoolClassOfTeacher currentClass = findClass(classId, joinedClasses);
		model.put("currentClass", currentClass);
		UserInfoDto masterTeacher = teacherFacade.getHeadTeacher(currentClass.getId());
		model.put("masterTeacher", masterTeacher);

		// 3，获取指定班级的学生列表
		List<StudentListItem> students = teacherFacade.getStudentsOfClass(userId, currentClass.getId());
		model.put("studentsOfClass", students);

		// 4， 查询老师是否有删除学生的权限
		List<Long> permissions = teacherFacade.getPermissionsInClass(userId, currentClass.getId());

		if (null == permissions || permissions.isEmpty()) {
			model.put("canRemoveStudent", false);
		} else {
			model.put("canRemoveStudent", permissions.indexOf(Permissions.REMOVE_STUDENT_FROM_CLASS) >= 0);
		}

		return "teacher/class/student/list.vm";
	}


	@RequestMapping("/class/assignment/list")
	public String listAssignments(
			@RequestParam(name="classId", required=false) Long classId,
			ModelMap model) {
		// 1, 获取老师所负责的班级列表
		Long userId = getCurrentUserId();
		List<SchoolClassOfTeacher> joinedClasses = teacherFacade.getJoinedClasses(userId);

		if (joinedClasses.isEmpty()) {
			//
			return redirectJoinClass();
		}
		model.put("joinedClasses", joinedClasses);

		// 2，获取指定班级的班主任和班级信息
		SchoolClassOfTeacher currentClass = findClass(classId, joinedClasses);
		model.put("currentClass", currentClass);
		UserInfoDto masterTeacher = teacherFacade.getHeadTeacher(currentClass.getId());
		model.put("masterTeacher", masterTeacher);

		// 获取指定班级的课程分配
		List<CourseAssignment> assignments = teacherFacade.getCourseAssignments(currentClass.getId());
		model.put("courseAssignments", assignments);

		return "teacher/class/assignment/list.vm";
	}

	@RequestMapping("/class/information")
	public String classInformation(
			@RequestParam(name="classId", required=false) Long classId,
			ModelMap model) {

		// 1, 获取老师所负责的班级列表
		Long userId = getCurrentUserId();
		List<SchoolClassOfTeacher> joinedClasses = teacherFacade.getJoinedClasses(userId);

		if (joinedClasses.isEmpty()) {
			//
			return redirectJoinClass();
		}
		model.put("joinedClasses", joinedClasses);

		// 2，获取指定班级的班主任和班级信息
		SchoolClassOfTeacher currentClass = findClass(classId, joinedClasses);
		model.put("currentClass", currentClass);
		UserInfoDto masterTeacher = teacherFacade.getHeadTeacher(currentClass.getId());
		model.put("masterTeacher", masterTeacher);
		
		model.put("currentSchool", teacherFacade.getSchoolInfoByClassId(currentClass.getId()));
//    	teacherFacade.getSchoolClassInfo(userId, classId)

		return "teacher/class/information.vm";
	}

	@RequestMapping("/class/join")
	public String joinClass(ModelMap model) {
		
		if (!teacherFacade.hasJoinedSchool(getCurrentUserId())) {
			return this.redirectJoinSchool();
		}
		
		//
		List<Course> courses = teacherFacade.getAllCourses();
		model.put("courses", courses);
		List<Integer> enrollYears = teacherFacade.getAvailableStudyYears();
		model.put("enrollYears", enrollYears);

		return "teacher/class/join.vm";
	}
	
	@RequestMapping("/school/join")
	public String joinSchool(ModelMap model) {
		// 如果已经加入学校跳转到学生列表页。
		if (teacherFacade.hasJoinedSchool(getCurrentUserId())) {
			return "redirect:/web/teacher/class/student/list";
		}
		
		model.put("schools", teacherFacade.getAllSchools());
		return "teacher/school/join.vm";
	}


	/**
	 * --------------------------------------------
	 *
	 * 我的发布
	 *
	 * --------------------------------------------
	 */

    /* ============================================
     * 课件
     * ============================================
     */

	@RequestMapping("/courseware/list")
	public String listCoursewares(
			@ModelAttribute QueryPaging paging,
			ModelMap model) {
		Long userId = getCurrentUserId();
		PagingInfo<CourseMaterialManagementListItem> pagingInfo = teacherFacade.getCourseMaterials(userId, paging);
		model.put("pagingInfo", pagingInfo);
		return "teacher/courseware/list.vm";
	}

	@RequestMapping("/courseware/create")
	public String createCourseware(ModelMap model) {
		// 1, 获取老师所负责的班级列表
		Long userId = getCurrentUserId();
		List<SchoolClassOfTeacher> joinedClasses = teacherFacade.getJoinedClasses(userId);

		if (joinedClasses.isEmpty()) {
			//
			return redirectJoinClass();
		}
		model.put("joinedClasses", joinedClasses);
		
		return "teacher/courseware/create.vm";
	}

	@RequestMapping("/courseware/detail")
	public String viewCourseware(
			@RequestParam("id") Long courseMaterialId,
			ModelMap model) {
		Long userId = getCurrentUserId();
		CourseMaterialManagementDetailInfo courseware = teacherFacade.getCourseMaterialDetail(userId, courseMaterialId);
		SchoolClassOfTeacher clazz = teacherFacade.getSchoolClassInfo(getCurrentUserId(), courseware.getClassId());
		model.put("courseware", courseware);
		model.put("clazz", clazz);
		return "teacher/courseware/detail.vm";
	}

	@RequestMapping("/courseware/edit")
	public String editCourseware(@RequestParam("id") Long courseMaterialId,
								 ModelMap model) {
		Long userId = getCurrentUserId();
		// 1, 获取老师所负责的班级列表
		List<SchoolClassOfTeacher> joinedClasses = teacherFacade.getJoinedClasses(userId);

		if (joinedClasses.isEmpty()) {
			//
			return redirectJoinClass();
		}
		model.put("joinedClasses", joinedClasses);
		CourseMaterialManagementDetailInfo courseware = teacherFacade.getCourseMaterialDetail(userId, courseMaterialId);
		model.put("courseware", courseware);
		return "teacher/courseware/edit.vm";
	}

    /* ============================================
     * 成绩
     * ============================================
     */
	
	@RequestMapping({"/score"})
	public String scoreIndex(
			@RequestParam(name="classId", required=false) Long classId,
			ModelMap model, RedirectAttributes attrs) {
		attrs.addAttribute("classId", classId);
		return "redirect:/web/teacher/score/list";
	}
	
	@RequestMapping("/score/list")
	public String listQuizScore(
			@RequestParam(name="classId", required=false) Long classId,
			ModelMap model) {
		
		// 1, 获取老师所加入的所有班级
		Long userId = getCurrentUserId();
		List<SchoolClassOfTeacher> joinedClasses = teacherFacade.getJoinedClasses(userId);

		if (null == joinedClasses || joinedClasses.isEmpty()) {
			// 当老师没有加入任何班级时跳到加入页面
			return redirectJoinClass();
		}
		model.put("joinedClasses", joinedClasses);

		// 2，获取指定班级的班主任和班级信息
		SchoolClassOfTeacher currentClass = findClass(classId, joinedClasses);
		model.put("currentClass", currentClass);
		classId = currentClass.getId();
		UserInfoDto masterTeacher = teacherFacade.getHeadTeacher(currentClass.getId());
		model.put("masterTeacher", masterTeacher);
		
		
		
		List<Course> assignedCources = teacherFacade.getAssignedCoursesOfTeacherInClass(userId, classId);
		if (null == assignedCources || assignedCources.isEmpty()) {
			model.put("emptyList", true);
			return "teacher/score/list.vm";
		}

        // TODO 换成调用getQuizOfClassByCourse
		PagingInfo<CourseQuiz> allCourseQuizes = teacherFacade.getQuizOfClassByTeacher(userId, classId);
		
		model.put("emptyList", allCourseQuizes.getList().isEmpty());
		model.put("allCourseQuizes", allCourseQuizes.getList());
		return "teacher/score/list.vm";
	}

	@RequestMapping("/score/create")
	public String createQuizScore(
			@RequestParam(name="classId", required=false) Long classId,
			@RequestParam(name="courseId", required=false) Long courseId,
			@RequestParam(name="quizId", required=false) Long quizId,
			ModelMap model) {
		// 1, 获取老师所负责的班级列表
		Long userId = getCurrentUserId();
		List<SchoolClassOfTeacher> joinedClasses = teacherFacade.getJoinedClasses(userId);

		if (joinedClasses.isEmpty()) {
			//
			return redirectJoinClass();
		}
		model.put("joinedClasses", joinedClasses);
		
		// 2，获取指定班级的班主任和班级信息
		SchoolClassOfTeacher currentClass = findClass(classId, joinedClasses);
		classId = currentClass.getId();
		model.put("currentClass", currentClass);
		UserInfoDto masterTeacher = teacherFacade.getHeadTeacher(currentClass.getId());
		model.put("masterTeacher", masterTeacher);
		
		// 3，获取任课的课程
		List<Course> assignedCources = teacherFacade.getAssignedCoursesOfTeacherInClass(userId, currentClass.getId());
		if (null == assignedCources || assignedCources.isEmpty()) {
			model.put("emptyCourseList", true);
			return "teacher/score/create.vm";
		}
		Course currentCourse = this.findCurrentCourse(courseId, assignedCources);
		model.put("assignedCourses", assignedCources);
		model.put("currentCourse", currentCourse);
		courseId = currentCourse.getId();
		
		// 4，获取考试列表
		QueryPaging paging = new QueryPaging();
		paging.setPageNum(0);
		paging.setPageSize(1000);
		PagingInfo<CourseQuiz> page = teacherFacade.getQuizOfClassByCourse(userId, 
				currentClass.getId(), currentCourse.getId(), paging);
		List<CourseQuiz> courseQuizes = page.getList();
		if (null != courseQuizes && !courseQuizes.isEmpty()) {
			List<CourseQuiz> allCourseQuizes = Lists.newArrayList();
			CourseQuiz currentQuiz = courseQuizes.get(0);
			for(CourseQuiz cq : courseQuizes) {
				if (null == cq.getQuiz().getEndTime() 
						|| System.currentTimeMillis() >= cq.getQuiz().getEndTime().getTime()) {
					List<QuizScoreDto> scores = teacherFacade.getQuizScoreOfClass(userId, 
							cq.getQuiz().getId(), currentClass.getId());
					if (null != scores && !scores.isEmpty()) {
						continue;
					}
					allCourseQuizes.add(cq);
					
					if (quizId == cq.getQuiz().getId()) {
						currentQuiz = cq;
					}
				}
			}
			
			if (null == currentQuiz) {
				currentQuiz = allCourseQuizes.get(0);
			}
			quizId = currentQuiz.getQuiz().getId();
			model.put("currentCourseQuiz", currentQuiz);
			model.put("allCourseQuizes", allCourseQuizes);
		} else {
			model.put("emptyQuizList", true);
			return "teacher/score/create.vm";
		}
		
		// 4，获取参与考试的学生列表
		List<StudentListItem> students = teacherFacade.getStudentsOfClass(userId,
				currentClass.getId());
		model.put("students", JsonUtils.toJson(students));
		return "teacher/score/create.vm";
	}
	
	private Course findCurrentCourse(Long courseId, List<Course> courses) {
		for(Course c : courses) {
			if (c.getId() == courseId) {
				return c;
			}
		}
		
		return courses.get(0);
	}

	@RequestMapping("/score/detail")
	public String getQuizDetail(
			@RequestParam(name="classId") Long classId,
			@RequestParam(name="quizId") Long quizId,
			ModelMap model) {
		// 1, 获取老师所负责的班级列表
		Long userId = getCurrentUserId();
		
		// 2，获取指定班级的班级信息
		SchoolClassOfTeacher currentClass = teacherFacade.getSchoolClassInfo(userId, classId);
		model.put("currentClass", currentClass);
		
		// 3，获取任课的课程
		CourseQuiz currentQuiz = teacherFacade.getQuizDetail(userId, quizId);
		model.put("currentCourseQuiz", currentQuiz);
		
		// 4，获取参与考试的学生列表
		List<StudentListItem> students = teacherFacade.getStudentsOfClass(userId,
				currentClass.getId());
		model.put("students", JsonUtils.toJson(students));
		
		// 5，获取成绩列表
		List<QuizScoreDto>  scores = teacherFacade.getQuizScoreOfClass(userId, quizId, classId);
		model.put("scores", JsonUtils.toJson(scores));
		return "teacher/score/detail.vm";
	}

	@RequestMapping("/score/edit")
	public String editQuiz(
			@RequestParam(name="classId") Long classId,
			@RequestParam(name="quizId") Long quizId,
			ModelMap model) {
		// 1, 获取老师所负责的班级列表
		Long userId = getCurrentUserId();
		
		// 2，获取指定班级的班级信息
		SchoolClassOfTeacher currentClass = teacherFacade.getSchoolClassInfo(userId, classId);
		model.put("currentClass", currentClass);
		
		// 3，获取任课的课程
		CourseQuiz currentQuiz = teacherFacade.getQuizDetail(userId, quizId);
		model.put("currentCourseQuiz", currentQuiz);
		
		// 4，获取参与考试的学生列表
		List<StudentListItem> students = teacherFacade.getStudentsOfClass(userId,
				currentClass.getId());
		model.put("students", JsonUtils.toJson(students));
		
		// 5，获取成绩列表
		List<QuizScoreDto>  scores = teacherFacade.getQuizScoreOfClass(userId, quizId, classId);
		model.put("scores", JsonUtils.toJson(scores));
		return "teacher/score/edit.vm";
	}

	@RequestMapping(value = "course", method = RequestMethod.GET)
	public String course(HttpServletRequest request, ModelMap model) {
		String selId = request.getParameter("classid");

		List<SchoolClassOfTeacher> result = Lists.newArrayList();
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(getCurrentUserId());
		for(SchoolClassOfTeacher sc:joinclass){
			Long it=new Long(3);
			if(sc.getPermissions().contains(it)){
				result.add(sc);
			}
		}
		if(selId==""&&result.size()>0){
			selId = result.get(0).getId().toString();
		}
		model.put("classlist",result);
		model.put("selid",selId);
		return "teacher/course.vm";
	}

	/*作业相关*/
	@RequestMapping(value = "homework", method = RequestMethod.GET)
	public String homework(HttpServletRequest request,
						   @ModelAttribute QueryPaging paging,
						   ModelMap model) {
		Long userId = getCurrentUserId();
		String selclsss = null;

		//long selclsss = null;
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(userId);
		if(joinclass!=null && joinclass.size()>0){
			selclsss = joinclass.get(0).getId().toString();
		}
		String paramClassId = request.getParameter("classid");
		if(paramClassId !=null&&paramClassId!=""){
			selclsss = paramClassId;
		}
		model.put("selclsss", selclsss);

		PagingInfo<HomeworkDto> pagingInfo = teacherFacade.getHomeworkList(userId, Long.parseLong(selclsss), paging);
		model.put("pagingInfo", pagingInfo);
		model.put("joinclass", joinclass);
		return "teacher/homework/list.vm";
	}

	/*作业新增*/
	@RequestMapping(value = "homework/do", method = RequestMethod.GET)
	public String homeworkedit(HttpServletRequest request,
						   ModelMap model) {
		Long userId = getCurrentUserId();
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(userId);
		model.put("joinclass", joinclass);
		model.put("now",new Date());
		return "teacher/homework/edit.vm";
	}

	/*作业更新*/
	@RequestMapping(value = "homework/update", method = RequestMethod.GET)
	public String homeworkupdate(HttpServletRequest request,
						   ModelMap model) {
		Long userId = getCurrentUserId();
		String homeworkid = request.getParameter("id");
		HomeworkDto homeworkDto =  teacherFacade.getHomeworkById(userId, Long.parseLong(homeworkid));
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(userId);
		model.put("homeinfo",homeworkDto);
		model.put("joinclass", joinclass);
		return "teacher/homework/update.vm";
	}


	/**
	 *  ==========================
	 *  小报告
	 *  ==========================
	 */
	@RequestMapping({"report/list", "report"})
	public String listReports(ModelMap model, 
			@ModelAttribute QueryPaging queryPaging) {
		PagingInfo<SneakingMessageListItem> paging = teacherFacade.getSneakingMessages(this.getCurrentUserId(), queryPaging);
		model.put("pagingInfo", paging);
		return "teacher/report/list.vm";
	}


	/**
	 * 根据班级id从班级列表中查找指定班级，如果没有找到返回列表中第一个班级
	 *
	 * @param classId
	 * @param joinedClasses
	 * @return
	 */
	private SchoolClassOfTeacher findClass(Long classId, List<SchoolClassOfTeacher> joinedClasses) {
		if (null != classId) {
			for (SchoolClassOfTeacher theClass : joinedClasses) {
				if (classId.equals(theClass.getId())) {
					return theClass;
				}
			}
		}

		return joinedClasses.get(0);
	}
	
	private String redirectJoinSchool() {
		return "redirect:/web/teacher/school/join";
	}

	private String redirectJoinClass() {
		return "redirect:/web/teacher/class/join";
	}
}
