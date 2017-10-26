package com.dianxian.school.consts;

import com.dianxian.school.dto.CourseMaterialType;
import com.dianxian.school.dto.QuizCategory;
import com.dianxian.school.dto.SchoolCategory;
import com.dianxian.user.dto.ParentChildRelation;

/**
 * Created by xuwenhao on 2016/5/29.
 */
public class Comments {
    public static final String USER_ID = "用户id";
    public static final String REQUEST_DATE_FORMAT = "格式yyyy-mm-dd";
    public static final String REQUEST_DATE_TIME_FORMAT = "格式HH:mm:ss";
    public static final String REQUEST_TIME_FORMAT = "秒为单位";
    public static final String SCHOOL_CATEGORY = "学校类型. "
            + SchoolCategory.PRIMARY_VALUE + "->小学,"
            + SchoolCategory.JUNIOR_VALUE + "->初中,"
            + SchoolCategory.SENIOR_VALUE + "->高中,"
            + SchoolCategory.UNIVERSITY_VALUE + "->大学";
    public static final String SCHOOL_CODE = "学校唯一编码";
    public static final String SCHOOL_NAME = "学校名称";
    public static final String SCHOOL_ID = "学校id";
    public static final String ENROL_YEAR = "入学年度";
    public static final String CLASS_CODE = "班级唯一编码";
    public static final String CLASS_NUMBER = "班级号.和入学年度组合起来代表几几届入学的几班";
    public static final String COURSE_ID = "课程id";
    public static final String CLASS_ID = "班级id";
    public static final String QUIZ_ID = "考试id";
    public static final String QUIZ_START_TIME = "考试开始时间";
    public static final String QUIZ_END_TIME = "考试结束时间";
    public static final String QUIZ_CATEGORY = "考试类型. " + QuizCategory.FINAL_EXAM + "->期末考试," + QuizCategory.MID_TERM + "->期中考试," + QuizCategory.MONTHLY + "->月考";
    public static final String STUDY_YEAR = "学年";
    public static final String STUDY_TERM = "学期";
    public static final String COURSE_MATERIAL_ID = "课件id";
    public static final String COURSE_MATERIAL_TYPE = "材料类型. " + CourseMaterialType.PREVIEW + "->预习向导, "
            + CourseMaterialType.NOTE + "->课堂笔记, " + CourseMaterialType.COURSEWARE + "->课件";
    public static final String ATTACHMENT_ID = "附件id";
    public static final String HOMEWORK_STUDY_DATE = "哪天的作业." + REQUEST_DATE_FORMAT;
    public static final String HOMEWORK_NAME = "作业标题";
    public static final String HOMEWORK_ID = "作业Id";
    public static final String PARENT_CHILD_RELATION = "关系. " + ParentChildRelation.FATHER + "->父亲," + ParentChildRelation.MOTHER + "->母亲";
    public static final String STUDENT_CODE = "学生唯一编码";
    public static final String STUDENT_USER_ID = "学生的userId";
    public static final String STUDENT_ID = "学生Id";
    public static final String ANNOUNCEMENT_TITLE = "标题";
    public static final String ANNOUNCEMENT_ID = "公告id";
    public static final String PLAYGROUND_ACT_ID = "操场活动id";
    public static final String PLAYGROUND_ACT_CONTENT = "操场活动评论内容";


}
