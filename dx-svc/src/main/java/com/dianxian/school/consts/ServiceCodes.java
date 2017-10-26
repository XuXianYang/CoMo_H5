package com.dianxian.school.consts;

/**
 * Created by XuWenHao on 4/21/2016.
 */
public class ServiceCodes {
    public static final int SCHOOL_ALREADY_EXISTS = 9001;
    public static final int SCHOOL_INVALID_CATEGORY = 9002;
    public static final int SCHOOL_GEN_CODE_FAIL = 9003;
    /** 学校不存在 */
    public static final int SCHOOL_NOT_EXISTS = 9004;
    public static final int SCHOOL_CODE_NOT_MATCH = 9005;

    public static final int CLASS_ALREADY_EXISTS = 9101;
    public static final int CLASS_NOT_EXISTS = 9102;
    public static final int CLASS_CODE_NOT_MATCH = 9103;

    public static final int TEACHER_ALREADY_JOINED_SCHOOL = 9201;
    public static final int TEACHER_NOT_JOINED_SCHOOL = 9202;
    public static final int TEACHER_NOT_A_TEACHER_OF_CLASS = 9203;
    public static final int TEACHER_NOT_A_TEACHER_OF_COURSE = 9204;
    public static final int TEACHER_SCHOOL_NOT_MATCH = 9205;
    public static final int TEACHER_MATERIAL_NOT_MATCH = 9206;
    public static final int TEACHER_NOT_A_TEACHER_OF_SCHOOL = 9207;

    public static final int STUDENT_ALREADY_JOINED_CLASS = 9301;
    public static final int STUDENT_NOT_JOINED_CLASS = 9302;
    public static final int STUDENT_CODE_NOT_MATCH = 9303;
    public static final int STUDENT_NOT_EXISTS = 9304;
    public static final int STUDENT_NOT_IN_CLASS = 9305;
    public static final int STUDENT_MAX_PARENT = 9306;

    public static final int COURSE_NOT_EXISTS = 9401;
    public static final int COURSE_ALREADY_ASSIGNED = 9402;
    public static final int COURSE_SCHEDULE_CONFLICTED = 9403;
    public static final int COURSE_MATERIAL_NOT_EXISTS = 9404;

    public static final int QUIZ_NOT_EXISTS = 9501;
    public static final int QUIZ_AND_CLASS_NOT_MATCH = 9502;
    public static final int QUIZ_SCORE_EMPTY_DATA = 9503;


    public static final int HOMEWORK_NOT_EXISTS = 9601;
    public static final int HOMEWORK_AND_TEACHER_NOT_MATCH = 9602;
    public static final int HOMEWORK_CANNOT_MODIFY_STUDY_DATE = 9603;

    public static final int SOS_SEND_FAIL = 9701;
    public static final int SOS_SEND_TO_TEACHER_FAIL = 9702;
    public static final int SOS_SEND_TO_PARENT_FAIL = 9703;

    public static final int ANNOUNCEMENT_NOT_EXISTS = 9801;

    public static final int PLAYGROUND_ACT_FORM_NULL = 9111;
    public static final int THIS_USER_ZANED = 9112;
    public static final int PLAYGROUND_ACT_DATA_IS_EXISTS = 9113;
    public static final int PLAYGROUND_ACT_DATA_ZAN_IS_EXISTS = 9114;
    public static final int PLAYGROUND_ACT_DATA_DISCUSS_IS_EXISTS = 9115;

    public static final int CALORIE_TOTAL_COMMIT_ERROR = 8001;
    public static final int CALORIE_MARK_RESUBMIT = 8002;

}
