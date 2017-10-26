package com.dianxian.web.controller;

import com.dianxian.core.resource.PagingInfo;
import com.dianxian.core.resource.QueryPaging;
import com.dianxian.core.spring.mvc.AbstractWebController;
import com.dianxian.im.domain.ImGroup;
import com.dianxian.im.facade.IMFacade;
import com.dianxian.school.domain.*;
import com.dianxian.school.dto.AnnouncementDto;
import com.dianxian.school.dto.SosMessageDto;
import com.dianxian.school.facade.TeacherFacade;
import com.dianxian.user.domain.Permissions;
import com.dianxian.user.domain.User;
import com.dianxian.user.facade.UserFacade;
import com.dianxian.user.filter.UserContext;
import com.dianxian.user.filter.UserContextUtils;
import com.dianxian.web.utils.DisplayUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by XuWenHao on 8/5/2016.
 */
@Controller
@RequestMapping(value = "teacher")
public class TeacherWebController2 extends AbstractWebController {

	@Autowired
	private TeacherFacade teacherFacade;

	@Autowired
	private IMFacade imFacade;

	@Autowired
	private UserFacade userFacade;



	@ModelAttribute
	public void headModel(ModelMap model) {
		Long userId = getUserId();
		model.put("currentUser", teacherFacade.getLoginTeacher(userId));

		Date today = Calendar.getInstance().getTime();
		model.put("currentDate", DisplayUtils.formatChineseDate(today));
		model.put("currentWeekDay", DisplayUtils.getWeekDay(today));
		model.put("currentSchool", teacherFacade.getSchoolInfoByTeacherUserId(userId));
		HasNewSneakingMessagesResult result = teacherFacade.hasNewSneakingMessages(getCurrentUserId());
		model.put("hasNewSneakingMessagesResult", result);
//       model.addAttribute("attributeName", abc);
	}

	/**
	 * 获取当前登陆用户的id
	 * @return
	 */
	private Long getUserId() {
		UserContext userContext = UserContextUtils.getCurrentUserContext();
		if (null != userContext) {
			return userContext.getUserId();
		} else {
			return null;
		}
	}
	/*公告列表*/
	@RequestMapping(value = "notice", method = RequestMethod.GET)
	public String noticelist(HttpServletRequest request,
								 @ModelAttribute QueryPaging paging,
								 ModelMap model) {
		Long userId = getUserId();
		String selclsss = null;

		//long selclsss = null;
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(userId);
		if(joinclass.size()>0){
			selclsss = joinclass.get(0).getId().toString();
		}
		String paramClassId = request.getParameter("classid");
		if(paramClassId !=null&&paramClassId!=""){
			selclsss = paramClassId;
		}
		model.put("selclsss", selclsss);

		PagingInfo<AnnouncementDto> pagingInfo = teacherFacade.getAnnouncementList(userId, Long.parseLong(selclsss), paging);
		model.put("pagingInfo", pagingInfo);
		model.put("joinclass", joinclass);
		return "teacher/notice/list.vm";
	}

	/*公告新增*/
	@RequestMapping(value = "notice/do", method = RequestMethod.GET)
	public String noticeedit(HttpServletRequest request,
							   ModelMap model) {
		Long userId = getUserId();
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(userId);
		model.put("joinclass", joinclass);
		return "teacher/notice/edit.vm";
	}
	/*公告update*/
	@RequestMapping(value = "notice/update", method = RequestMethod.GET)
	public String noticeupdate(HttpServletRequest request,
							   ModelMap model) {
		Long userId = getUserId();
		String noticeid = request.getParameter("id");
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(userId);
		AnnouncementDto announcementDto = teacherFacade.getAnnouncementDetail(userId, Long.parseLong(noticeid));
		model.put("noticeinfo",announcementDto);
		model.put("joinclass", joinclass);
		return "teacher/notice/update.vm";
	}


	/*安全助手列表*/
	@RequestMapping(value = "safe", method = RequestMethod.GET)
	public String safelist(HttpServletRequest request,
							 @ModelAttribute QueryPaging paging,
							 ModelMap model) {
		Long userId = getUserId();

		String selclsss = null;
		List<SchoolClassOfTeacher> joinclass = teacherFacade.getJoinedClasses(userId);
		if(joinclass.size()>0){
			selclsss = joinclass.get(0).getId().toString();
		}
		String paramClassId = request.getParameter("classid");
		if(paramClassId !=null&&paramClassId!=""){
			selclsss = paramClassId;
		}
		model.put("selclsss", selclsss);

		PagingInfo<SosMessageDto> pagingInfo = teacherFacade.getStudentMessageList(userId,Long.parseLong(selclsss),paging);
		model.put("pagingInfo", pagingInfo);
		return "teacher/safe/list.vm";
	}

	/*考试铃*/
	@RequestMapping(value = "ling", method = RequestMethod.GET)
	public String linglist(HttpServletRequest request,
						   @ModelAttribute QueryPaging paging,
						   ModelMap model) {
		Long userId = getUserId();
		
		// 获取是否有创建考试铃权限
		SchoolInfo school = (SchoolInfo) model.get("currentSchool");
		
		List<Long> permissions = teacherFacade.getPermissionsInSchool(userId, school.getId());
		if (null == permissions || permissions.isEmpty()) {
			model.put("canCreateQuiz", false);
		} else {
			model.put("canCreateQuiz", permissions.indexOf(Permissions.CREATE_QUIZ) >= 0);
		}
		
		PagingInfo<CourseQuiz> list = teacherFacade.getQuizList(userId, paging);
		System.out.println(list.getSize());
		model.put("pagingInfo", list);
		return "teacher/ling/list.vm";
	}

	/*考试铃新增*/
	@RequestMapping(value = "ling/do", method = RequestMethod.GET)
	public String lingedit(HttpServletRequest request,
							 ModelMap model) {
		// 获取是否有创建考试铃权限
		SchoolInfo school = (SchoolInfo) model.get("currentSchool");
		
		List<Long> permissions = teacherFacade.getPermissionsInSchool(this.getCurrentUserId(), school.getId());
		if (null == permissions || permissions.isEmpty()) {
			model.put("canCreateQuiz", false);
		} else {
			model.put("canCreateQuiz", permissions.indexOf(Permissions.CREATE_QUIZ) >= 0);
		}
		
		
		List<Course> courses = teacherFacade.getAllCourses();
		model.put("courses", courses);

		List<Integer> result = Lists.newArrayList();
		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		result.add(currentYear);
		for (int i = 0; i <5; i++) {
			result.add(currentYear - i);
		}

		model.put("now", new Date());
		model.put("years", result);

		return "teacher/ling/edit.vm";
	}

	/*考试铃update*/
	@RequestMapping(value = "ling/update", method = RequestMethod.GET)
	public String lingupdate(HttpServletRequest request,
							   ModelMap model) {
		Long userId = getUserId();
		String lingid = request.getParameter("id");

		CourseQuiz courseQuiz = teacherFacade.getQuizDetail(userId, Long.parseLong(lingid));


		List<Course> courses = teacherFacade.getAllCourses();
		model.put("courses", courses);
		model.put("courseQuiz", courseQuiz);

		List<Integer> result = Lists.newArrayList();
		Calendar now = Calendar.getInstance();
		int currentYear = now.get(Calendar.YEAR);
		result.add(currentYear);
		for (int i = 0; i <5; i++) {
			result.add(currentYear - i);
		}

		model.put("now", new Date());
		model.put("years", result);

		return "teacher/ling/update.vm";
	}

	/*群*/
	@RequestMapping(value = "group", method = RequestMethod.GET)
	public String group(HttpServletRequest request,
						   ModelMap model) {
		Long userId = getUserId();

		List<ImGroup> result = imFacade.getGroups(userId);
		String selid = request.getParameter("selid");

		model.put("grouplist",result);
		model.put("selid",selid);

		return "teacher/group/index.vm";
	}


	/*设置*/
	@RequestMapping(value = "setting", method = RequestMethod.GET)
	public String setting(HttpServletRequest request,
						ModelMap model) {
		Long userId = getUserId();

		User teacherinfo = userFacade.getUser(userId);
		model.put("teacherinfo",teacherinfo);

		return "teacher/setting/index.vm";
	}

	/*设置*/
	@RequestMapping(value = "logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request,HttpServletResponse response) throws IOException {


//		Cookie sidCookie = new Cookie("sid", "");
//		response.addCookie(sidCookie);
		response.sendRedirect(request.getContextPath()+request.getServletPath()+"/user/login");
		return null;
	}

}
